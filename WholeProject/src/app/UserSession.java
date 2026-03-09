package app;

import model.user.domain.User;

import model.user.domain.Profile;

/**
 * UserSession - Singleton class to manage the current logged-in user session.
 * This class maintains the state of the currently authenticated user across
 * all scenes in the JavaFX application.
 */
public class UserSession {
    private static UserSession instance;

    private User currentUser;
    private Profile currentProfile;

    // Private constructor for singleton pattern
    private UserSession() {
        this.currentUser = null;
        this.currentProfile = null;
    }

    /**
     * Gets the singleton instance of UserSession
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Logs in a user and sets their profile
     */
    public void login(User user, Profile profile) {
        this.currentUser = user;
        this.currentProfile = profile;
        System.out.println("User logged in: " + user.getUsername());
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getUsername());
        }
        this.currentUser = null;
        this.currentProfile = null;
    }

    /**
     * Checks if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Gets the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the current user's profile
     */
    public Profile getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Sets or updates the current profile
     */
    public void setCurrentProfile(Profile profile) {
        this.currentProfile = profile;
    }
}
