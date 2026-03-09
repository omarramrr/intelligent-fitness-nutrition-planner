package model.workout.domain;

import model.workout.enums.TrainingSplit;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDay {
    private final int dayIndex;
    private final TrainingSplit splitType;
    private final List<WorkoutExerciseEntry> exercises;

    public WorkoutDay(int dayIndex, TrainingSplit splitType, List<WorkoutExerciseEntry> exercises) {
        this.dayIndex = dayIndex;
        this.splitType = splitType;
        this.exercises = new ArrayList<>(exercises);
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public TrainingSplit getSplitType() {
        return splitType;
    }

    public List<WorkoutExerciseEntry> getExercises() {
        return new ArrayList<>(exercises);
    }

    public int getTotalExercises() {
        return exercises.size();
    }

    public void removeExerciseAt(int index) {
        if (index >= 0 && index < exercises.size()) {
            exercises.remove(index);
        }
    }
}
