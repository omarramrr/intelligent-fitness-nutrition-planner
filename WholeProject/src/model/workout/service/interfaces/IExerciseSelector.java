package model.workout.service.interfaces;

import model.workout.enums.ExerciseLibrary;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.service.components.selection.RotationTracker;
import java.util.List;


/**
 * Interface for selecting exercises.
 */
public interface IExerciseSelector {

    /**
     * Selects exercises based on criteria.
     */
    List<ExerciseLibrary> selectExercises(MuscleGroup group, List<PreferredEquipment> preferences,
            RotationTracker rotationTracker, int limit, int currentDay);
}
