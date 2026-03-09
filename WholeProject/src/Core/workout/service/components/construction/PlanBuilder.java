package model.workout.service.components.construction;

import model.user.domain.Profile;
import model.user.enums.FitnessLevel;
import model.workout.domain.RepsRange;
import model.workout.domain.WorkoutExerciseEntry;
import model.workout.enums.ExerciseLibrary;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.enums.TrainingSplit;
import model.workout.service.interfaces.IPlanBuilder;
import model.workout.service.components.selection.ExerciseSelector;
import model.workout.service.components.selection.RotationTracker;
import java.util.*;

/**
 * 1. Decides which muscles you train each day
 * 
 * → by building a "split focus" (e.g., Upper/Lower, Anterior/Posterior, or Full
 * Body rotation).
 * 
 * 2. Builds all exercises for each day
 * 
 * → by selecting exercises, distributing sets, and creating structured workout
 * entries..
 */
public class PlanBuilder implements IPlanBuilder {
    private static final int MIN_SETS_PER_EX = 2;
    private static final int MAX_SETS_PER_EX = 5;

    private final ExerciseSelector exerciseSelector;

    public PlanBuilder(ExerciseSelector exerciseSelector) {
        this.exerciseSelector = exerciseSelector;
    }

    // Creates the basic muscle split (which muscles on which day).
    /*
     * Day 1 → {CHEST, SHOULDERS, TRICEPS}
     * Day 2 → {BACK, BICEPS}
     * Day 3 → {LEGS}
     */
    public Map<Integer, Set<MuscleGroup>> buildSplitFocus(TrainingSplit split, int trainingDays) {
        Map<Integer, Set<MuscleGroup>> focus = new HashMap<>();
        List<Set<MuscleGroup>> templates = new ArrayList<>();

        // Branch logic: Low Frequency (2-4 days) vs High Frequency (5-6 days)
        boolean isHighFrequency = trainingDays >= 5;

        if (split == TrainingSplit.ANTERIOR_POSTERIOR) {
            if (isHighFrequency) {
                templates.addAll(buildAnteriorPosteriorCycle());
            } else {
                templates.addAll(buildCoreAnteriorPosteriorCycle());
            }
        } else if (split == TrainingSplit.UPPER_LOWER) {
            if (isHighFrequency) {
                templates.addAll(buildUpperLowerCycle());
            } else {
                templates.addAll(buildCoreUpperLowerCycle());
            }
        } else {
            // Full Body
            if (trainingDays > 3) {
                 templates.addAll(buildFullBodyCycle()); 
            } else {
                templates.addAll(buildCoreFullBodyCycle());
            }
        }

        // Prevents 0 training days → ensures at least 1 day.
        int safeDays = Math.max(1, trainingDays);
        
        // Assign a template to each day, cycling through them.
        for (int day = 1; day <= safeDays; day++) {
             // Use modulo arithmetic to cycle through the available templates
            int templateIndex = (day - 1) % templates.size();
            Set<MuscleGroup> template = templates.get(templateIndex);
            focus.put(day, new HashSet<>(template));
        }
        return focus;
    }
            
    /*
     * Builds the actual exercises for ONE specific day, based on:
     * • Muscle groups
     * • Sets per muscle
     * • Equipment preferences
     * • Exercise rotation
     * • Restrictions
     * • Profile attributes (age/height/goal etc.)
     */

    // Builds the actual exercise list for a specific day.
    public List<WorkoutExerciseEntry> buildDayExercises(int dayIndex,
            Map<MuscleGroup, Integer> dayMuscles,
            Map<MuscleGroup, List<PreferredEquipment>> equipmentMap, RotationTracker rotationTracker,
            Profile profile, TrainingSplit split) {

        ArrayList<WorkoutExerciseEntry> entries = new ArrayList<>();
        if (dayMuscles == null)// If no muscles this day → return empty list.
            return entries;

        // Loop through each muscle group scheduled for this day.
        /*
         * CHEST → 10 sets
         * BACK → 12 sets
         * LEGS → 16 sets
         */
        for (Map.Entry<MuscleGroup, Integer> entry : dayMuscles.entrySet()) {
            MuscleGroup muscle = entry.getKey();
            int totalSets = entry.getValue();

            // Decide how many different exercises to do for this muscle.
            int exerciseCount = determineExerciseCount(totalSets);

            // Override for Upper/Lower split: Ensure 2 exercises for Quads and Hamstrings
            if (split == TrainingSplit.UPPER_LOWER) {
                if (muscle == MuscleGroup.QUADRICEPS || muscle == MuscleGroup.HAMSTRINGS) {
                    exerciseCount = 2;
                }
            }

            // Get equipment preferences.
            List<PreferredEquipment> prefs = null;

            /*
             * Example:
             * CHEST → [BARBELL, DUMBBELL, MACHINE]
             * BACK → [BARBELL, DUMBBELL, MACHINE]
             * LEGS → [BARBELL, DUMBBELL, MACHINE]
             */
            if (equipmentMap != null) {
                prefs = equipmentMap.get(muscle); //
            }

            // Select the exercises using our selector logic.
            List<ExerciseLibrary> selected = exerciseSelector.selectExercises(
                    muscle, prefs, rotationTracker, exerciseCount, dayIndex);

            /*
             * Selector handles:
             * Avoiding repeated exercises (rotation)
             * Matching equipment
             * Filtering based on restrictions (injuries)
             * Matching the required exercise count
             */

            if (selected.isEmpty())
                continue;

            // Distribute the total sets among the selected exercises.
            List<Integer> setDist = distributeSets(totalSets, selected.size());

            /*
             * Example:
             * totalSets = 12, exerciseCount = 3
             * → distribution: [4, 4, 4]
             * or
             * totalSets = 10, exerciseCount = 2
             * → [5, 5]
             * Respects MIN/MAX sets:
             * Minimum sets = 2
             * Maximum = 5
             */
            // Create the workout entries.
            for (int i = 0; i < selected.size(); i++) {
                ExerciseLibrary ex = selected.get(i);
                int sets = setDist.get(i);

                // So next week it picks different exercises.
                entries.add(toWorkoutEntry(ex, muscle, sets, profile));

                // Record usage for rotation.
                if (rotationTracker != null) {
                    rotationTracker.record(ex.getExerciseId(), muscle, dayIndex);
                }
            }
        }
        return entries;
    }

    // --- HIGH FREQUENCY VARIETY CYCLES (For 5-6 Days) ---

    // Creates A/B/C rotation (6 days) for Upper/Lower.
    private List<Set<MuscleGroup>> buildUpperLowerCycle() {
        // ... (Existing Logic kept as "High Freq") ...
        List<Set<MuscleGroup>> cycle = new ArrayList<>();

        // 1. Upper A (Compound Power)
        Set<MuscleGroup> upperA = new HashSet<>();
        upperA.add(MuscleGroup.CHEST);
        upperA.add(MuscleGroup.BACK);
        upperA.add(MuscleGroup.SHOULDERS);
        upperA.add(MuscleGroup.TRICEPS);
        cycle.add(upperA);

        // 2. Lower A (Squat Focus)
        Set<MuscleGroup> lowerA = new HashSet<>();
        lowerA.add(MuscleGroup.QUADRICEPS);
        lowerA.add(MuscleGroup.CALVES);
        lowerA.add(MuscleGroup.ABS);
        cycle.add(lowerA);

        // 3. Upper B (Hypertrophy/Width)
        Set<MuscleGroup> upperB = new HashSet<>();
        upperB.add(MuscleGroup.LATS);
        upperB.add(MuscleGroup.SHOULDERS);
        upperB.add(MuscleGroup.BICEPS);
        upperB.add(MuscleGroup.FOREARMS);
        cycle.add(upperB);

        // 4. Lower B (Hinge/Glute Focus)
        Set<MuscleGroup> lowerB = new HashSet<>();
        lowerB.add(MuscleGroup.HAMSTRINGS);
        lowerB.add(MuscleGroup.GLUTES);
        lowerB.add(MuscleGroup.ABS);
        cycle.add(lowerB);

        // 5. Upper C (Volume/Arms)
        Set<MuscleGroup> upperC = new HashSet<>();
        upperC.add(MuscleGroup.CHEST);
        upperC.add(MuscleGroup.BACK);
        upperC.add(MuscleGroup.BICEPS);
        upperC.add(MuscleGroup.TRICEPS);
        cycle.add(upperC);

        // 6. Lower C (Unilateral/Stability)
        Set<MuscleGroup> lowerC = new HashSet<>();
        lowerC.add(MuscleGroup.QUADRICEPS);
        lowerC.add(MuscleGroup.HAMSTRINGS);
        lowerC.add(MuscleGroup.CALVES);
        cycle.add(lowerC);

        return cycle;
    }

    // --- CORE COMPREHENSIVE CYCLES (For 2-4 Days) ---

    private List<Set<MuscleGroup>> buildCoreUpperLowerCycle() {
        List<Set<MuscleGroup>> cycle = new ArrayList<>();
        
        // Comprehensive Upper (Hits everything)
        Set<MuscleGroup> upper = new HashSet<>();
        upper.add(MuscleGroup.CHEST);
        upper.add(MuscleGroup.BACK);
        upper.add(MuscleGroup.SHOULDERS);
        upper.add(MuscleGroup.BICEPS);
        upper.add(MuscleGroup.TRICEPS);
        upper.add(MuscleGroup.LATS);
        cycle.add(upper);

        // Comprehensive Lower (Hits everything)
        Set<MuscleGroup> lower = new HashSet<>();
        lower.add(MuscleGroup.QUADRICEPS);
        lower.add(MuscleGroup.HAMSTRINGS);
        lower.add(MuscleGroup.GLUTES);
        lower.add(MuscleGroup.CALVES);
        lower.add(MuscleGroup.ABS);
        cycle.add(lower);

        return cycle;
    }

    private List<Set<MuscleGroup>> buildCoreAnteriorPosteriorCycle() {
        List<Set<MuscleGroup>> cycle = new ArrayList<>();
        
        // Comprehensive Anterior (Front)
        Set<MuscleGroup> ant = new HashSet<>();
        ant.add(MuscleGroup.CHEST);
        ant.add(MuscleGroup.SHOULDERS);
        ant.add(MuscleGroup.QUADRICEPS);
        ant.add(MuscleGroup.BICEPS);
        ant.add(MuscleGroup.ABS);
        cycle.add(ant);

        // Comprehensive Posterior (Back)
        Set<MuscleGroup> post = new HashSet<>();
        post.add(MuscleGroup.BACK);
        post.add(MuscleGroup.LATS);
        post.add(MuscleGroup.HAMSTRINGS);
        post.add(MuscleGroup.GLUTES);
        post.add(MuscleGroup.TRICEPS);
        post.add(MuscleGroup.CALVES);
        cycle.add(post);

        return cycle;
    }

    private List<Set<MuscleGroup>> buildCoreFullBodyCycle() {
         List<Set<MuscleGroup>> cycle = new ArrayList<>();
         
         // Comprehensive Full Body (The "Classic" 1-Day template)
         Set<MuscleGroup> full = new HashSet<>();
         full.add(MuscleGroup.CHEST);
         full.add(MuscleGroup.BACK);
         full.add(MuscleGroup.SHOULDERS);
         full.add(MuscleGroup.QUADRICEPS);
         full.add(MuscleGroup.HAMSTRINGS);
         full.add(MuscleGroup.BICEPS);
         full.add(MuscleGroup.TRICEPS);
         full.add(MuscleGroup.ABS);
         cycle.add(full);
         
         return cycle;
    }

    // Creates A/B/C rotation (6 days) for Anterior/Posterior.
    private List<Set<MuscleGroup>> buildAnteriorPosteriorCycle() {
        List<Set<MuscleGroup>> cycle = new ArrayList<>();

        // 1. Anterior A (Front of Body Strength)
        Set<MuscleGroup> antA = new HashSet<>();
        antA.add(MuscleGroup.CHEST);
        antA.add(MuscleGroup.QUADRICEPS);
        antA.add(MuscleGroup.BICEPS); // Anterior arm
        antA.add(MuscleGroup.ABS);
        cycle.add(antA);

        // 2. Posterior A (Back of Body Strength)
        Set<MuscleGroup> postA = new HashSet<>();
        postA.add(MuscleGroup.BACK);
        postA.add(MuscleGroup.HAMSTRINGS);
        postA.add(MuscleGroup.TRICEPS); // Posterior arm
        postA.add(MuscleGroup.GLUTES);
        cycle.add(postA);

        // 3. Anterior B (Shoulders & Arms Focus)
        Set<MuscleGroup> antB = new HashSet<>();
        antB.add(MuscleGroup.SHOULDERS);
        antB.add(MuscleGroup.BICEPS);
        antB.add(MuscleGroup.FOREARMS);
        antB.add(MuscleGroup.ABS);
        cycle.add(antB);

        // 4. Posterior B (Lats & Traps Focus)
        Set<MuscleGroup> postB = new HashSet<>();
        postB.add(MuscleGroup.LATS);
        postB.add(MuscleGroup.TRAPS);
        postB.add(MuscleGroup.TRICEPS);
        postB.add(MuscleGroup.CALVES);
        cycle.add(postB);

        // 5. Anterior C (Volume / Hypertrophy)
        Set<MuscleGroup> antC = new HashSet<>();
        antC.add(MuscleGroup.CHEST);
        antC.add(MuscleGroup.SHOULDERS);
        antC.add(MuscleGroup.QUADRICEPS);
        cycle.add(antC);

        // 6. Posterior C (Volume / Hypertrophy)
        Set<MuscleGroup> postC = new HashSet<>();
        postC.add(MuscleGroup.BACK);
        postC.add(MuscleGroup.LATS);
        postC.add(MuscleGroup.GLUTES);
        postC.add(MuscleGroup.HAMSTRINGS);
        cycle.add(postC);

        return cycle;
    }

    // Creates the 6-day (A-F) Full Body cycle templates.
    private List<Set<MuscleGroup>> buildFullBodyCycle() {
        List<Set<MuscleGroup>> cycle = new ArrayList<>();

        // A: Quads, Chest, Back (Thickness)
        Set<MuscleGroup> sessionA = new HashSet<>();
        sessionA.add(MuscleGroup.QUADRICEPS);
        sessionA.add(MuscleGroup.CHEST);
        sessionA.add(MuscleGroup.BACK);
        sessionA.add(MuscleGroup.TRICEPS);
        cycle.add(sessionA);

        // B: Hams, Shoulders, Lats
        Set<MuscleGroup> sessionB = new HashSet<>();
        sessionB.add(MuscleGroup.HAMSTRINGS);
        sessionB.add(MuscleGroup.SHOULDERS);
        sessionB.add(MuscleGroup.LATS);
        sessionB.add(MuscleGroup.BICEPS);
        cycle.add(sessionB);

        // C: Glutes, Chest, Back (Width)
        Set<MuscleGroup> sessionC = new HashSet<>();
        sessionC.add(MuscleGroup.GLUTES);
        sessionC.add(MuscleGroup.CHEST);
        sessionC.add(MuscleGroup.BACK);
        sessionC.add(MuscleGroup.ABS);
        cycle.add(sessionC);

        // D: Quads, Shoulders, Arms
        Set<MuscleGroup> sessionD = new HashSet<>();
        sessionD.add(MuscleGroup.QUADRICEPS);
        sessionD.add(MuscleGroup.SHOULDERS);
        sessionD.add(MuscleGroup.BICEPS);
        sessionD.add(MuscleGroup.TRICEPS);
        cycle.add(sessionD);

        // E: Hams, Lats, Calves
        Set<MuscleGroup> sessionE = new HashSet<>();
        sessionE.add(MuscleGroup.HAMSTRINGS);
        sessionE.add(MuscleGroup.LATS);
        sessionE.add(MuscleGroup.CALVES);
        sessionE.add(MuscleGroup.ABS);
        cycle.add(sessionE);

        // F: Mixed Compound / Functional
        Set<MuscleGroup> sessionF = new HashSet<>();
        sessionF.add(MuscleGroup.QUADRICEPS);
        sessionF.add(MuscleGroup.BACK);
        sessionF.add(MuscleGroup.GLUTES);
        sessionF.add(MuscleGroup.SHOULDERS);
        cycle.add(sessionF);

        return cycle;
    }



    // Simple rule to decide number of exercises.
    private int determineExerciseCount(int totalSets) {
        if (totalSets >= 12)
            return 3;
        if (totalSets >= 8)
            return 2;
        return 1;
    }

    // Distributes sets as evenly as possible.
    private List<Integer> distributeSets(int totalSets, int exercises) {
        ArrayList<Integer> distribution = new ArrayList<>();
        if (exercises <= 0)
            return distribution;

        // Calculate base sets per exercise.
        int baseSets = totalSets / exercises;
        int remainder = totalSets % exercises;

        // Distribute sets, giving extra sets to the first few exercises.
        for (int i = 0; i < exercises; i++) {
            int sets = baseSets + (i < remainder ? 1 : 0);
            // Clamp to MIN/MAX.
            sets = Math.max(MIN_SETS_PER_EX, Math.min(MAX_SETS_PER_EX, sets));
            distribution.add(sets);
        }
        return distribution;
    }

    // Converts an ExerciseLibrary entry to a WorkoutExerciseEntry.
    private WorkoutExerciseEntry toWorkoutEntry(ExerciseLibrary ex, MuscleGroup muscle, int sets, Profile profile) {
        RepsRange reps = determineRepsRange(profile);
        return new WorkoutExerciseEntry(ex.getDisplayName(), muscle, sets, reps);
    }

    // Determines the rep range based on user fitness level.
    private RepsRange determineRepsRange(Profile profile) {
        FitnessLevel fitnessLevel = profile.getFitnessLevel();

        // Standard rep ranges based on fitness level
        if (fitnessLevel == FitnessLevel.BEGINNER) {
            return new RepsRange(10, 15);
        } else if (fitnessLevel == FitnessLevel.INTERMEDIATE) {
            return new RepsRange(8, 12);
        } else {
            // Advanced
            return new RepsRange(6, 10);
        }
    }
}
