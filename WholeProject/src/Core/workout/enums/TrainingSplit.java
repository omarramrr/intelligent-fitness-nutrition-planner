package model.workout.enums;

// Enum for available training split styles.
public enum TrainingSplit {
    ANTERIOR_POSTERIOR("Anterior/Posterior", "Alternates between anterior and posterior muscle groups"),
    FULL_BODY("Full Body", "All muscle groups trained each session"),
    UPPER_LOWER("Upper/Lower", "Alternates between upper and lower body");

    private final String displayName;
    private final String description;

    TrainingSplit(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
