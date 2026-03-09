package model.workout.enums;

// Enum of supported equipment types as preferred by the user for building science-based plans.
public enum PreferredEquipment {
    BARBELL("Barbell"),
    DUMBBELL("Dumbbell"),
    MACHINE("Machine"),
    CABLE("Cable"),
    BODYWEIGHT("Bodyweight"),
    KETTLEBELL("Kettlebell"),
    RESISTANCE_BAND("Resistance Band"),
    SMITH_MACHINE("Smith Machine");

    private final String displayName;

    PreferredEquipment(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
