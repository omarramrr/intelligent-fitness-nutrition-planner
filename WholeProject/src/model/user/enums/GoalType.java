package model.user.enums;

/**
 * GOAL TYPE (User Fitness Goals, Enum)
 * -----------------------------------
 * Aggregates all supported user goals found in Profile, User, and logic classes.
 */
public enum GoalType {
    // Goal focused on reducing body weight and body fat.
    LOSE_WEIGHT("Lose Weight / Cut"),

    // Goal focused on increasing muscle mass and size.
    GAIN_WEIGHT("Gain Weight / Build Muscle"),

    // Goal focused on maintaining current weight and fitness level.
    MAINTENANCE("Maintenance");

    // Each goal has a human-readable description.
    private final String description;

    // Constructor that sets the description for each goal type.
    GoalType(String description) {
        this.description = description;
    }

    // This method returns a human-readable description of the goal.
    // Useful for displaying goal information in user interfaces.
    public String getDescription() {
        return description;
    }
}
