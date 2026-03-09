package model.workout.service.interfaces;

import model.workout.enums.MuscleGroup;
import java.util.Map;
import java.util.Set;

/**
 * Interface for managing weak point prioritization.
 */
public interface IWeakPointManager {

    /**
     * Increases volume for weak point muscles.
     */
    Map<MuscleGroup, Integer> applyPriority(Map<MuscleGroup, Integer> baseVolumes,
            Set<MuscleGroup> weakPoints);

    /**
     * Creates a safe copy of the weak points set.
     */
    Set<MuscleGroup> sanitizeWeakPoints(Set<MuscleGroup> weakPoints);
}
