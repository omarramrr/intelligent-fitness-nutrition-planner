package app;

import model.user.domain.User;

import model.user.domain.Profile;
import model.user.enums.Gender;
import model.user.enums.GoalType;
import model.user.enums.FitnessLevel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * UserManager - Manages user registration and authentication.
 * This is a temporary in-memory storage solution.
 * In production, this would be replaced with database operations.
 */
public class UserManager {
    private static UserManager instance;

    // In-memory storage: username -> User
    private Map<String, User> users;
    // In-memory storage: username -> Profile
    private Map<String, Profile> profiles;

    private UserManager() {
        users = new HashMap<>();
        profiles = new HashMap<>();

        // Create a demo user for testing
        createDemoUser();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Creates a demo user for testing purposes
     */
    private void createDemoUser() {
        try {
            User demoUser = new User("demo", "demo@fitness.com");
            demoUser.setPassword("Demo123!");

            Profile demoProfile = new Profile(demoUser, 175.0, 75.0, GoalType.LOSE_WEIGHT, Gender.MALE,
                    LocalDate.of(1995, 5, 15), FitnessLevel.INTERMEDIATE);
            // demoProfile.setGender(Gender.MALE); // Constructor sets this
            // demoProfile.setDateOfBirth(LocalDate.of(1995, 5, 15)); // Constructor sets
            // this
            // demoProfile.setHeightInCm(175.0); // Constructor sets this
            // demoProfile.setWeightInKg(75.0); // Constructor sets this
            // demoProfile.setGoal(GoalType.LOSE_WEIGHT); // Constructor sets this
            // demoProfile.setFitnessLevel(FitnessLevel.INTERMEDIATE); // Constructor sets
            // this
            // demoProfile.setActivityLevel(ActivityLevel.MODERATE); // Removed obsolete
            // field

            users.put(demoUser.getUsername().toLowerCase(), demoUser);
            profiles.put(demoUser.getUsername().toLowerCase(), demoProfile);

            System.out.println("Demo user created - Username: demo, Password: Demo123!");
        } catch (Exception e) {
            System.err.println("Error creating demo user: " + e.getMessage());
        }
    }

    /**
     * Registers a new user with basic information
     */
    public boolean registerUser(String username, String email, String password) {
        try {
            // Check if user already exists
            if (userExists(username)) {
                throw new IllegalArgumentException("Username already exists");
            }

            // Create new user
            User newUser = new User(username, email);
            newUser.setPassword(password);

            // Create basic profile with default placeholder values
            // These will be updated when the user completes their profile
            Profile newProfile = new Profile(newUser, 170.0, 70.0, GoalType.MAINTENANCE, Gender.MALE,
                    LocalDate.of(2000, 1, 1), FitnessLevel.BEGINNER);

            // Store in memory
            users.put(username.toLowerCase(), newUser);
            profiles.put(username.toLowerCase(), newProfile);

            System.out.println("User registered successfully: " + username);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Registration failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Registers a new user with complete profile information
     */
    public boolean registerUserWithProfile(String username, String email, String password,
            Gender gender, LocalDate dateOfBirth,
            double height, double weight,
            GoalType goal, FitnessLevel fitnessLevel) {
        try {
            // Register basic user first
            registerUser(username, email, password);

            // Update profile with additional information
            Profile profile = profiles.get(username.toLowerCase());
            profile.setGender(gender);
            profile.setDateOfBirth(dateOfBirth);
            profile.setHeightInCm(height);
            profile.setWeightInKg(weight);
            profile.setGoal(goal);
            profile.setFitnessLevel(fitnessLevel);
            // profile.setActivityLevel(ActivityLevel.MODERATE); // Default - Removed
            // obsolete field

            System.out.println("User registered with complete profile: " + username);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Registration failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Authenticates a user with username/email and password
     */
    public User authenticateUser(String usernameOrEmail, String password) {
        // Try to find user by username first
        User user = users.get(usernameOrEmail.toLowerCase());

        // If not found, try to find by email
        if (user == null) {
            user = users.values().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(usernameOrEmail))
                    .findFirst()
                    .orElse(null);
        }

        // Validate password
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            System.out.println("Authentication successful: " + user.getUsername());
            return user;
        }

        System.err.println("Authentication failed for: " + usernameOrEmail);
        return null;
    }

    /**
     * Gets a user's profile
     */
    public Profile getProfile(String username) {
        return profiles.get(username.toLowerCase());
    }

    /**
     * Checks if a username already exists
     */
    public boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    /**
     * Gets a user by username
     */
    public User getUser(String username) {
        return users.get(username.toLowerCase());
    }

    /**
     * Updates a user's profile
     */
    public void updateProfile(String username, Profile profile) {
        profiles.put(username.toLowerCase(), profile);
        System.out.println("Profile updated for: " + username);
    }

    /**
     * Update an existing user (e.g., after password change)
     * 
     * @param user The user object with updated information
     */
    public void updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String username = user.getUsername().toLowerCase();

        // Check if user exists
        if (!users.containsKey(username)) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        // Update the user in memory
        users.put(username, user);

        System.out.println("User updated successfully: " + username);
    }
}
