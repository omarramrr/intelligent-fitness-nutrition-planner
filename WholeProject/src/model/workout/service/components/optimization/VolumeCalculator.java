package model.workout.service.components.optimization;

import model.user.domain.Profile;
import model.user.enums.FitnessLevel;
import model.workout.enums.MuscleGroup;
import model.workout.enums.TrainingSplit;
import model.workout.service.components.data.MuscleGroups;
import model.workout.service.interfaces.IVolumeCalculator;
import java.util.*;

/**
 * Handles volume calculations and daily allocation.
 */
public class VolumeCalculator implements IVolumeCalculator {
    private final WeakPointManager weakPointManager;

    public VolumeCalculator(WeakPointManager weakPointManager) {
        this.weakPointManager = weakPointManager;
    }

    // Calculates total weekly sets per muscle based on profile and goals.
    public Map<MuscleGroup, Integer> calculateWeeklyVolume(Profile profile, Set<MuscleGroup> weakPoints,
            TrainingSplit split, int trainingDays) {

        // Check for valid inputs.
        if (profile == null)
            throw new IllegalArgumentException("profile must not be null");
        if (trainingDays <= 0)
            throw new IllegalArgumentException("trainingDays must be >= 1");

        Map<MuscleGroup, Integer> base = new EnumMap<>(MuscleGroup.class);

        // Determine base sets based on fitness level (e.g. Beginner = 8 sets).
        int baseSets = determineBaseSets(profile.getFitnessLevel());

        // Cap the max sets per muscle to avoid overtraining.
        int capPerMuscle = trainingDays * 6;

        // Calculate volume for each muscle group.
        for (MuscleGroup muscle : MuscleGroup.values()) {
            int sizeAdj = 0;
            // Larger muscles get a bit more volume.
            if (MuscleGroups.LARGE_MUSCLES.contains(muscle)) {
                sizeAdj = 2;
            }

            int splitAdj = 0;
            // Full body splits usually have slightly lower per-muscle volume to manage
            // fatigue.
            if (split == TrainingSplit.FULL_BODY) {
                splitAdj = -1;
            }

            // Calculate target sets.
            int target = baseSets + sizeAdj + splitAdj;

            // Clamp the target between 6 and the cap.
            if (target < 6)
                target = 6;
            if (target > capPerMuscle)
                target = capPerMuscle;

            base.put(muscle, target);
        }

        // Apply extra volume for weak points.
        return weakPointManager.applyPriority(base, weakPoints);
    }

    // Helper to get base sets from fitness level.
    private int determineBaseSets(FitnessLevel level) {
        if (level == null)
            return 8;
        switch (level) {
            case BEGINNER:
                return 8;
            case INTERMEDIATE:
                return 10;
            case ADVANCED:
                return 14;
            case PROFESSIONAL:
                return 16;
            default:
                return 8;
        }
    }

    // Splits the weekly volume into daily sessions.
    public Map<Integer, Map<MuscleGroup, Integer>> allocateDailyVolume(
            Map<Integer, Set<MuscleGroup>> dayMuscleFocus,
            Map<MuscleGroup, Integer> weeklyVolumes,
            int trainingDays,
            Set<MuscleGroup> weakPoints) {

        Map<Integer, Map<MuscleGroup, Integer>> allocation = new HashMap<>();

        // Now go through each day and assign sets.
        for (Map.Entry<Integer, Set<MuscleGroup>> entry : dayMuscleFocus.entrySet()) {
            Map<MuscleGroup, Integer> dayPlan = new EnumMap<>(MuscleGroup.class);
            Set<MuscleGroup> muscles = entry.getValue();

            if (muscles != null) {
                for (MuscleGroup muscle : muscles) {
                    boolean isWeak = weakPoints != null && weakPoints.contains(muscle);
                    int sets = determineDailySets(trainingDays, isWeak);
                    dayPlan.put(muscle, sets);
                }
            }
            allocation.put(entry.getKey(), dayPlan);
        }
        return allocation;
    }

    /**
     * Determines the number of sets per day for a muscle group based on training
     * frequency.
     * 
     * Rules:
     * 1. If training days > 3:
     * - Weak Point: 3 sets
     * - Normal: 2 sets
     * 2. If training days <= 3:
     * - Weak Point: 4 sets
     * - Normal: 3 sets
     * 
     * @param trainingDays Number of days the user trains per week.
     * @param isWeakPoint  True if the muscle is a weak point.
     * @return The number of sets to perform for this muscle.
     */
    public int determineDailySets(int trainingDays, boolean isWeakPoint) {
        if (trainingDays > 3) {
            // Rule 1: > 3 days
            return isWeakPoint ? 3 : 2;
        } else {
            // Rule 2: <= 3 days
            return isWeakPoint ? 4 : 3;
        }
    }
}
