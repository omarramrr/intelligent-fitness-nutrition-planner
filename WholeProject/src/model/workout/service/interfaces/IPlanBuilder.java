package model.workout.service.interfaces;

import model.user.domain.Profile;
import model.workout.domain.WorkoutExerciseEntry;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.enums.TrainingSplit;
import model.workout.service.components.selection.RotationTracker;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for building workout plan structure.
 */
public interface IPlanBuilder {

    /**
     * Build a mapping of training day → targeted muscle groups.
     */
    Map<Integer, Set<MuscleGroup>> buildSplitFocus(TrainingSplit split, int trainingDays);

    /**
     * Build the list of exercises for a single training day.
     */
    List<WorkoutExerciseEntry> buildDayExercises(int dayIndex,
            Map<MuscleGroup, Integer> dayMuscles,
            Map<MuscleGroup, List<PreferredEquipment>> equipmentMap, RotationTracker rotationTracker,
            Profile profile, TrainingSplit split);
}
