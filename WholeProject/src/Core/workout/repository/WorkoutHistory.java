package model.workout.repository;

import model.workout.domain.WorkoutPlan;
import java.util.ArrayList;
import java.util.List;

public class WorkoutHistory {
    private final List<WorkoutPlan> completedWorkouts;

    public WorkoutHistory() {
        this.completedWorkouts = new ArrayList<>();
    }

    // In-memory functionality as requested for the class structure
    public void addWorkout(WorkoutPlan plan) {
        completedWorkouts.add(plan);
    }

    public List<WorkoutPlan> getHistory() {
        return new ArrayList<>(completedWorkouts);
    }
}
