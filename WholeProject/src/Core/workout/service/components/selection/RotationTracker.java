package model.workout.service.components.selection;

import model.workout.enums.MuscleGroup;
import java.util.*;

/**
 * Tracks all exercises used for each muscle within the current planning cycle (week).
 * Ensures variety by preventing the same exercise from being selected multiple times
 * for the same muscle group unless necessary (fallback).
 */
public class RotationTracker {
    // Maps MuscleGroup -> Set of Exercise IDs used this week
    private final Map<MuscleGroup, Set<String>> usedExercises = new EnumMap<>(MuscleGroup.class);

    public RotationTracker() {
    }

    /**
     * Checks if an exercise has already been used for this muscle group in the current cycle.
     */
    public boolean isUsed(String exerciseId, MuscleGroup muscleGroup) {
        return usedExercises.getOrDefault(muscleGroup, Collections.emptySet())
                            .contains(exerciseId);
    }

    /**
     * Records that an exercise has been used.
     */
    public void record(String exerciseId, MuscleGroup muscleGroup, int currentDay) {
        usedExercises.computeIfAbsent(muscleGroup, k -> new HashSet<>())
                     .add(exerciseId);
    }

    /**
     * Resets the tracker (e.g., for a new week).
     */
    public void reset() {
        usedExercises.clear();
    }
    
    // Kept for compatibility if needed, but delegates to isUsed
    public boolean shouldSkip(String exerciseId, MuscleGroup muscleGroup, int currentDay) {
        return isUsed(exerciseId, muscleGroup);
    }
}
