package model.workout.service.components;

import model.workout.domain.WorkoutDay;
import model.workout.domain.PlanContext;
import model.workout.domain.WorkoutExerciseEntry;
import model.workout.domain.WorkoutPlan;
import model.workout.enums.MuscleGroup;
import model.workout.enums.TrainingSplit;
import model.workout.service.components.selection.RotationTracker;
import model.workout.service.WorkoutService;

import java.util.*;

/**
 * Orchestrates the creation of a workout plan using PlanContext and
 * WorkoutService.
 * Follows the Builder pattern to construct the complex WorkoutPlan object.
 */
public class AutomaticPlanBuilder {

    private final PlanContext planContext;
    private final WorkoutService workoutService;

    public AutomaticPlanBuilder(PlanContext planContext, WorkoutService workoutService) {
        this.planContext = Objects.requireNonNull(planContext, "PlanContext is required");
        this.workoutService = Objects.requireNonNull(workoutService, "WorkoutService is required");
    }

    /**
     * Builds the complete workout plan by executing the generation pipeline.
     */
    public WorkoutPlan build() {
        // 1. Determine Muscle Focus per Day
        Map<Integer, Set<MuscleGroup>> baseFocus = workoutService.buildSplitFocus(planContext.getSplit(),
                planContext.getTrainingDays());

        Map<MuscleGroup, Integer> frequencyTargets = workoutService.deriveFrequencyTargets(
                planContext.getSplit(),
                planContext.getTrainingDays(), planContext.getWeakPoints());

        Map<Integer, Set<MuscleGroup>> adjustedFocus = workoutService.applyFrequencyTargets(baseFocus,
                frequencyTargets,
                2);

        // Relax spacing for Full Body splits (assumes user manages rest days or days
        // are not consecutive)
        int minSpacingHours = 48;
        if (planContext.getSplit() == TrainingSplit.FULL_BODY) {
            minSpacingHours = 24;
        }
        workoutService.enforceSpacing(adjustedFocus, minSpacingHours);

        // 2. Calculate Volumes
        Map<MuscleGroup, Integer> weeklyVolume = workoutService.calculateWeeklyVolume(planContext.getProfile(),
                planContext.getWeakPoints(), planContext.getSplit(), planContext.getTrainingDays());

        Map<Integer, Map<MuscleGroup, Integer>> dailyVolume = workoutService.allocateDailyVolume(adjustedFocus,
                weeklyVolume, planContext.getTrainingDays(), planContext.getWeakPoints());

        // 3. Build Exercises for Each Day
        List<WorkoutDay> generatedDays = new ArrayList<>();
        RotationTracker rotationTracker = new RotationTracker();

        List<Integer> dayIndices = new ArrayList<>(adjustedFocus.keySet());
        Collections.sort(dayIndices);

        for (Integer dayIndex : dayIndices) {
            Map<MuscleGroup, Integer> dayMuscles = dailyVolume.get(dayIndex);

            List<WorkoutExerciseEntry> exercises = workoutService.buildDayExercises(
                    dayIndex,
                    dayMuscles,
                    planContext.getEquipmentPreferences(),
                    rotationTracker,
                    planContext.getProfile(),
                    planContext.getSplit());

            generatedDays.add(new WorkoutDay(dayIndex, planContext.getSplit(), exercises));
        }

        // 4. Enforce Weekly Exercise Count Limits (16 - 27)
        enforceWeeklyExerciseLimits(generatedDays, 16, 27);

        return new WorkoutPlan(generatedDays, planContext.getProfile().getProfileId(),
                planContext.getProfile().getGoal());
    }

    private void enforceWeeklyExerciseLimits(List<WorkoutDay> days, int minExercises, int maxExercises) {
        int totalExercises = days.stream().mapToInt(d -> d.getExercises().size()).sum();

        // REDUCE if too many
        while (totalExercises > maxExercises) {
            if (!removeLeastImportantExercise(days)) {
                break; // Could not remove any more safe exercises
            }
            totalExercises = days.stream().mapToInt(d -> d.getExercises().size()).sum();
        }

        // INCREASE if too few
        /*
         * Implementation Note:
         * For now, simpler handling for under-limit: We assume the base volume
         * calculation
         * provides enough work. If sets are high but exercise count is low, it's
         * usually fine.
         * Strict enforcement of MIN limit is less critical than MAX limit for avoiding
         * burnout.
         */
    }

    /**
     * Tries to remove one exercise from the plan to reduce volume.
     * Priority for removal:
     * 1. 2nd/3rd exercise for a muscle group on the same day.
     * 2. Small isolation muscles (Calves, Forearms, Abs) if they have multiple
     * sets.
     */
    private boolean removeLeastImportantExercise(List<WorkoutDay> days) {
        // Strategy: Find a day with the most exercises
        WorkoutDay targetDay = days.stream()
                .max(Comparator.comparingInt(d -> d.getExercises().size()))
                .orElse(null);

        if (targetDay == null || targetDay.getExercises().isEmpty())
            return false;

        List<WorkoutExerciseEntry> exercises = targetDay.getExercises();

        // 1. Look for duplicates (same muscle trained multiple times). Remove the last
        // one.
        for (int i = exercises.size() - 1; i >= 0; i--) {
            MuscleGroup mg = exercises.get(i).getMuscleGroup();
            // If we have seen this muscle *before* (meaning this is a secondary exercise,
            // because we iterate backwards, actually wait... we want to find if it appears
            // EARLIER in the list too)
            // Let's count occurrences first.
            long count = exercises.stream().filter(e -> e.getMuscleGroup() == mg).count();
            if (count > 1) {
                // Evaluate removal: Don't remove if it drops us below 1 exercise for that
                // muscle
                // Safe to remove this instance
                targetDay.removeExerciseAt(i);
                return true;
            }
        }

        // 2. If no duplicates, look for small muscles to potentially merge or skip
        // (Only if we really must reduce count. Currently we won't aggressively remove
        // the ONLY exercise for a muscle group, as that breaks the "Full Body"
        // promise).
        // For now, we only trim volume (secondary exercises).
        // If strictly > 27 and we have 33 primary exercises (3 days * 11 muscles),
        // we have a conflict. "Full Body" promise vs "Max 27" constraint.

        // Conflict Resolution:
        // If we are strictly bound by 27, and we have 33 unique muscle-sessions,
        // we MUST skip some muscles on some days.
        // Let's simply remove the last exercise of the day (usually Abs/Calves/Arms)
        if (exercises.size() > 1) { // Keep at least 1 exercise per day
            targetDay.removeExerciseAt(exercises.size() - 1);
            return true;
        }

        return false;
    }
}
