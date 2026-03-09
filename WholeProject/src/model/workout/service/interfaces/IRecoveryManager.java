package model.workout.service.interfaces;

import model.workout.enums.MuscleGroup;
import java.util.Map;
import java.util.Set;

/**
 * Interface for enforcing recovery rules.
 */
public interface IRecoveryManager {

    /**
     * Enforces minimum spacing between appearances of high-stress muscles.
     */
    void enforceSpacing(Map<Integer, Set<MuscleGroup>> dayMuscleFocus, int minHoursBetween);
}
