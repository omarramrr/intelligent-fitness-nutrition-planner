package model.workout.domain;

import model.workout.enums.MuscleGroup;

public class WorkoutExerciseEntry {
    private final String exerciseName;
    private final MuscleGroup muscleGroup;
    private final int sets;
    private final RepsRange repsRange;

    public WorkoutExerciseEntry(String exerciseName, MuscleGroup muscleGroup, int sets,
            RepsRange repsRange) {
        if (exerciseName == null || exerciseName.isEmpty())
            throw new IllegalArgumentException("Exercise Name required");
        if (muscleGroup == null)
            throw new IllegalArgumentException("Muscle group required");
        if (sets < 1)
            throw new IllegalArgumentException("Sets must be >= 1");
        if (repsRange == null)
            throw new IllegalArgumentException("Reps range required");

        this.exerciseName = exerciseName;
        this.muscleGroup = muscleGroup;
        this.sets = sets;
        this.repsRange = repsRange;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }

    public int getSets() {
        return sets;
    }

    public RepsRange getRepsRange() {
        return repsRange;
    }
}
