package model.workout.service.components.data;

import model.workout.enums.MuscleGroup;
import java.util.EnumSet;
import java.util.Set;

/**
 * Defines muscle group sets used across the workout generation logic.
 */
public class MuscleGroups {

    public static final Set<MuscleGroup> LARGE_MUSCLES = EnumSet.of(
            MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.QUADRICEPS,
            MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.SHOULDERS, MuscleGroup.LATS);

    public static final Set<MuscleGroup> HIGH_STRESS_MUSCLES = EnumSet.of(
            MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS, MuscleGroup.QUADRICEPS,
            MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.LATS, MuscleGroup.TRAPS);

    public static final Set<MuscleGroup> ANTERIOR = EnumSet.of(
            MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.QUADRICEPS,
            MuscleGroup.ABS, MuscleGroup.BICEPS, MuscleGroup.FOREARMS);

    public static final Set<MuscleGroup> POSTERIOR = EnumSet.of(
            MuscleGroup.BACK, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES,
            MuscleGroup.TRAPS, MuscleGroup.LATS, MuscleGroup.TRICEPS);

    public static final Set<MuscleGroup> UPPER = EnumSet.of(
            MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS,
            MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.FOREARMS,
            MuscleGroup.LATS, MuscleGroup.TRAPS, MuscleGroup.ABS);

    public static final Set<MuscleGroup> LOWER = EnumSet.of(
            MuscleGroup.QUADRICEPS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CALVES);
}
