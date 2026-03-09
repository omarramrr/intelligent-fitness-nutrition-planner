package model.workout.service.interfaces;

import model.workout.enums.MuscleGroup;
import model.workout.enums.TrainingSplit;
import java.util.Map;
import java.util.Set;

/**
 * Interface for managing training frequency calculations.
 */
public interface IFrequencyManager {

    /**
     * Derives how many times per week each muscle should be trained.
     */
    Map<MuscleGroup, Integer> deriveFrequencyTargets(TrainingSplit split, int trainingDays,
            Set<MuscleGroup> weakPoints);

    /**
     * Applies the derived frequency targets onto an existing day→muscle mapping.
     */
    Map<Integer, Set<MuscleGroup>> applyFrequencyTargets(Map<Integer, Set<MuscleGroup>> baseFocus,
            Map<MuscleGroup, Integer> frequencyTargets, int minSpacingDays);
}
