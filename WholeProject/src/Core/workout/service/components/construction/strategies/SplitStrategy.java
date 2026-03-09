package model.workout.service.components.construction.strategies;

import model.workout.enums.MuscleGroup;
import java.util.Map;
import java.util.Set;

/**
 * Strategy interface for generating training split focus.
 * Defines how muscles are distributed across training days.
 */
public interface SplitStrategy {

    /**
     * Generates the muscle group focus for each training day.
     * 
     * @param trainingDays Number of training days per week
     * @return Map of day index to set of muscle groups
     */
    Map<Integer, Set<MuscleGroup>> generateSplit(int trainingDays);
}
