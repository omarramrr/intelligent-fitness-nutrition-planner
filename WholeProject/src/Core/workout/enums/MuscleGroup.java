package model.workout.enums;

// Science-based enumeration of major muscle groups for workout programming.
public enum MuscleGroup {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    QUADRICEPS("Quadriceps"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    CALVES("Calves"),
    ABS("Abs"),
    FOREARMS("Forearms"),
    LATS("Lats"),
    TRAPS("Traps");

    private final String displayName;
    MuscleGroup(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
