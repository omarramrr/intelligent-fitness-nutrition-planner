package model.user.enums;

/**
 * GENDER (Enum for user gender identity as used in Profile)
 * -----------------------------------
 */

// This enum defines the possible gender identities that users can specify.
// It's used in profile information and affects calculations like BMR.
// The enum includes options for different gender identities and preferences.
public enum Gender {
    // Male gender identity.
    MALE("Male"),

    // Female gender identity.
    FEMALE("Female");

    // Each gender option has a human-readable label.
    private final String label;

    // Constructor that sets the label for each gender option.
    Gender(String label) {
        this.label = label;
    }

    // This method returns a human-readable label for the gender.
    // Useful for displaying gender options in user interfaces.
    public String getLabel() {
        return label;
    }
}
