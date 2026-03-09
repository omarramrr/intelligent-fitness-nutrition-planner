package model.user.enums;

/**
 * FITNESS LEVEL (Enum for fitness experience levels)
 * -----------------------------------
 */

// This enum defines the different levels of fitness experience that users can have.
// It's used to determine appropriate workout intensity and activity level calculations.
// Higher fitness levels correspond to more advanced training and higher calorie needs.
public enum FitnessLevel {
    // User has little to no fitness experience and is just starting out.
    BEGINNER("Beginner", 1.2),

    // User has some fitness experience and basic knowledge of training.
    INTERMEDIATE("Intermediate", 1.4),

    // User has extensive fitness experience and trains regularly.
    ADVANCED("Advanced", 1.6),

    // User is a professional athlete or has elite-level fitness experience.
    PROFESSIONAL("Professional", 1.8);

    // Each fitness level has a descriptive name.
    private final String description;
    private final double multiplier;

    // Constructor that sets the description for each fitness level.
    FitnessLevel(String description, double multiplier) {
        this.description = description;
        this.multiplier = multiplier;
    }

    // This method returns a human-readable description of the fitness level.
    public String getDescription() {
        return description;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
