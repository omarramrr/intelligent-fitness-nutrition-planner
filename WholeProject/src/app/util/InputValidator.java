package app.util;

/**
 * Functional interface for input validation.
 */
@FunctionalInterface
public interface InputValidator {
    /**
     * Checks if the input string is valid according to the implemented rule.
     * 
     * @param input The input string to validate.
     * @return true if valid, false otherwise.
     */
    boolean isValid(String input);
}
