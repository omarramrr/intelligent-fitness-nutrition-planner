package model.workout.domain;

import model.user.enums.GoalType;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlan {
    private final List<WorkoutDay> days;
    private final String profileId;
    private final GoalType goal;

    public WorkoutPlan(List<WorkoutDay> days, String profileId, GoalType goal) {
        if (days.isEmpty()) {
            throw new IllegalArgumentException("Days list cannot be empty");
        }
        if (profileId == null || profileId.isEmpty()) {
            throw new IllegalArgumentException("Profile ID cannot be empty");
        }
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        this.days = new ArrayList<>(days);
        this.profileId = profileId;
        this.goal = goal;
    }

    public List<WorkoutDay> getDays() {
        return new ArrayList<>(days);
    }

    public String getProfileId() {
        return profileId;
    }

    public GoalType getGoal() {
        return goal;
    }

    /**
     * Displays the workout plan in a professional table format.
     * Shows each day with exercises, sets, reps, and target muscles.
     */
    public void displayWorkout() {
        System.out.println("\n╔" + "═".repeat(78) + "╗");
        System.out.println("║" + centerText("YOUR PERSONALIZED WORKOUT PLAN", 78) + "║");
        System.out.println("╠" + "═".repeat(78) + "╣");
        System.out.println("║ Goal: " + String.format("%-69s", goal.getDescription()) + "║");
        System.out.println("╚" + "═".repeat(78) + "╝\n");

        for (WorkoutDay day : days) {
            // Day Header
            System.out.println("┌" + "─".repeat(78) + "┐");
            System.out.println("│ DAY " + day.getDayIndex() + " - " + 
                String.format("%-68s", day.getSplitType().getDisplayName()) + "│");
            System.out.println("├" + "─".repeat(78) + "┤");
            
            // Table Header
            System.out.println("│ " + 
                String.format("%-40s", "EXERCISE") + " │ " +
                String.format("%-12s", "MUSCLE") + " │ " +
                String.format("%-6s", "SETS") + " │ " +
                String.format("%-10s", "REPS") + " │");
            System.out.println("├" + "─".repeat(78) + "┤");
            
            // Exercise Rows
            List<WorkoutExerciseEntry> exercises = day.getExercises();
            for (WorkoutExerciseEntry exercise : exercises) {
                RepsRange reps = exercise.getRepsRange();
                
                String repsDisplay = reps.getMinReps() == reps.getMaxReps() 
                    ? String.valueOf(reps.getMinReps())
                    : reps.getMinReps() + "-" + reps.getMaxReps();
                
                System.out.println("│ " + 
                    String.format("%-40s", exercise.getExerciseName()) + " │ " +
                    String.format("%-12s", exercise.getMuscleGroup().getDisplayName()) + " │ " +
                    String.format("%-6s", exercise.getSets()) + " │ " +
                    String.format("%-10s", repsDisplay) + " │");
            }
            
            System.out.println("└" + "─".repeat(78) + "┘\n");
        }
        
        System.out.println("═".repeat(80));
        System.out.println("Total Training Days: " + days.size());
        int totalExercises = days.stream().mapToInt(WorkoutDay::getTotalExercises).sum();
        System.out.println("Total Exercises: " + totalExercises);
        System.out.println("═".repeat(80));
    }

    /**
     * Helper method to center text within a given width.
     */
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + 
               " ".repeat(Math.max(0, width - text.length() - padding));
    }
}
