package model.user.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private java.util.UUID userId; // Matches uniqueidentifier in DB
    private String username;
    private String email;
    private String password;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String username, String email) {
        this.userId = java.util.UUID.randomUUID(); // Generate new ID for new users
        setUsername(username);
        setEmail(email);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructs a new User with all details.
     *
     * @param username  The username of the user.
     * @param email     The email address of the user.
     * @param isActive  Whether the user is active (currently unused but kept for
     *                  compatibility).
     * @param createdAt The timestamp when the user was created.
     * @param updatedAt The timestamp when the user was last updated.
     */
    public User(String username, String email, boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        setUsername(username);
        setEmail(email);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    /**
     * Sets the username.
     *
     * @param username The new username.
     * @throws IllegalArgumentException if the username is null, length is not
     *                                  between 3-20 characters,
     *                                  or contains invalid characters.
     */
    public void setUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 20
                || !username.matches("^[a-zA-Z0-9_ -]+$"))
            throw new IllegalArgumentException("Username 3-20 chars, letters, digits, _, -, or spaces only");
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Strong email validation regex
        // 1. Local part:
        // - Allowed chars: A-Z, a-z, 0-9, _, +, &, *, -
        // - Must start with allowed char (no leading dot)
        // - Dots must be followed by allowed chars (no trailing dot, no consecutive
        // dots)
        // 2. Domain part:
        // - Allowed chars: A-Z, a-z, 0-9, -
        // - Segments separated by dots
        // - No leading/trailing hyphens in segments
        // 3. TLD:
        // - 2 to 7 letters
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,7}$";

        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email address format. Example: user@example.com");
        }

        if (email.length() > 100) {
            throw new IllegalArgumentException("Email too long (max 100 chars)");
        }

        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }

        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public java.util.UUID getUserId() {
        return userId;
    }

    public void setUserId(java.util.UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(username, other.username) && Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }

    @Override
    public String toString() {
        return String.format("User{username='%s', email='%s",
                username, email);
    }
}
