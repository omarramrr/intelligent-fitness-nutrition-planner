package app.util;

import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

import model.workout.enums.TrainingSplit;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.user.enums.GoalType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for handling user input with validation and error handling.
 * Provides methods for collecting workout-related user preferences.
 */
public class InputUtils {

    /**
     * Gets valid input from user with custom validation.
     */
    public static String getValidInput(Scanner scanner, String prompt, InputValidator validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.isValid(input)) {
                return input;
            }
            System.out.println("   ⚠️  " + errorMessage);
        }
    }

    /**
     * Prompts user to select training days per week (2-6).
     * 
     * @param scanner Scanner for user input
     * @return Number of training days (2-6)
     */
    public static int selectTrainingDays(Scanner scanner) {
        System.out.println("\nHow many days per week can you train?");
        System.out.println("   2. 2 Days (Full Body / Upper-Lower / Anterior-Posterior)");
        System.out.println("   3. 3 Days (Full Body)");
        System.out.println("   4. 4 Days (Full Body / Upper-Lower / Anterior-Posterior)");
        System.out.println("   5. 5 Days (Full Body)");
        System.out.println("   6. 6 Days (Full Body / Upper-Lower / Anterior-Posterior)");

        while (true) {
            System.out.print("Enter choice (2-6): ");
            try {
                int days = Integer.parseInt(scanner.nextLine().trim());
                if (days >= 2 && days <= 6) {
                    return days;
                }
                System.out.println("⚠️  Please enter a number between 2 and 6.");
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Prompts user to select a training split based on available days.
     * Only shows splits that are appropriate for the given number of days.
     * 
     * @param days    Number of training days per week
     * @param scanner Scanner for user input
     * @return Selected TrainingSplit enum
     */
    public static TrainingSplit selectTrainingSplit(int days, Scanner scanner) {
        System.out.println("\nRecommended Splits for " + days + " Days:");
        List<TrainingSplit> allowedSplits = new ArrayList<>();

        // Determine allowed splits based on training days
        if (days == 2) {
            allowedSplits.add(TrainingSplit.FULL_BODY);
            allowedSplits.add(TrainingSplit.ANTERIOR_POSTERIOR);
            allowedSplits.add(TrainingSplit.UPPER_LOWER);
        } else if (days == 3) {
            allowedSplits.add(TrainingSplit.FULL_BODY);
        } else if (days == 5) {
            allowedSplits.add(TrainingSplit.FULL_BODY);
        } else { // 4 or 6 days
            allowedSplits.add(TrainingSplit.UPPER_LOWER);
            allowedSplits.add(TrainingSplit.ANTERIOR_POSTERIOR);
            allowedSplits.add(TrainingSplit.FULL_BODY);
        }

        return selectEnumFromList("Choose your training split:", allowedSplits, scanner);
    }

    /**
     * Prompts user to select weak points to prioritize (multi-select).
     * 
     * @param scanner Scanner for user input
     * @return Set of selected MuscleGroup enums (empty if none selected)
     */
    public static Set<MuscleGroup> selectWeakPoints(Scanner scanner) {
        Set<MuscleGroup> weakPoints = new HashSet<>();
        System.out.print("\nDo you have any weak points to prioritize? (y/n): ");

        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            return weakPoints; // Return empty set
        }

        System.out.println("\nSelect weak points (enter numbers separated by commas, e.g., '1, 3'):");
        System.out.println("You can select multiple muscles to prioritize.");

        MuscleGroup[] muscles = MuscleGroup.values();
        displayEnumOptions(muscles);

        weakPoints = selectMultipleEnums(muscles, scanner);

        if (!weakPoints.isEmpty()) {
            System.out.println("✅ Selected weak points: " +
                    weakPoints.stream()
                            .map(MuscleGroup::getDisplayName)
                            .collect(Collectors.joining(", ")));
        }

        return weakPoints;
    }

    /**
     * Prompts user to select available equipment (multi-select).
     * Returns a map with the same equipment list applied to all muscle groups.
     * 
     * @param scanner Scanner for user input
     * @return Map of MuscleGroup to List of PreferredEquipment
     */
    public static Map<MuscleGroup, List<PreferredEquipment>> selectEquipment(Scanner scanner) {
        Map<MuscleGroup, List<PreferredEquipment>> equipmentMap = new EnumMap<>(MuscleGroup.class);

        System.out.println("\nSelect available equipment (enter numbers separated by commas, e.g., '2, 3'):");
        System.out.println("You can select multiple equipment types.");

        PreferredEquipment[] equipmentList = PreferredEquipment.values();
        displayEnumOptions(equipmentList);

        Set<PreferredEquipment> selectedEquipment = selectMultipleEnums(equipmentList, scanner);

        if (!selectedEquipment.isEmpty()) {
            System.out.println("✅ Selected equipment: " +
                    selectedEquipment.stream()
                            .map(PreferredEquipment::getDisplayName)
                            .collect(Collectors.joining(", ")));
        }

        // Apply global equipment selection to all muscle groups
        List<PreferredEquipment> equipmentAsList = new ArrayList<>(selectedEquipment);
        for (MuscleGroup muscle : MuscleGroup.values()) {
            equipmentMap.put(muscle, new ArrayList<>(equipmentAsList));
        }

        return equipmentMap;
    }
    // ====== HELPER METHODS ======

    /**
     * Displays enum options with numbered list.
     * 
     * @param values Array of enum values to display
     */
    private static <T extends Enum<T>> void displayEnumOptions(T[] values) {
        for (int i = 0; i < values.length; i++) {
            String displayName = getEnumDisplayName(values[i]);
            System.out.printf("   %d. %s%n", i + 1, displayName);
        }
    }

    /**
     * Gets display name for an enum value.
     * Handles special cases like GoalType which has getDescription().
     * 
     * @param enumValue Enum value to get display name for
     * @return Display name string
     */
    private static String getEnumDisplayName(Enum<?> enumValue) {
        // Check if enum has a getDisplayName() method
        if (enumValue instanceof MuscleGroup) {
            return ((MuscleGroup) enumValue).getDisplayName();
        } else if (enumValue instanceof PreferredEquipment) {
            return ((PreferredEquipment) enumValue).getDisplayName();
        } else if (enumValue instanceof TrainingSplit) {
            return ((TrainingSplit) enumValue).getDisplayName();
        } else if (enumValue instanceof GoalType) {
            return ((GoalType) enumValue).getDescription();
        }

        // Default: format enum name (replace underscores with spaces)
        return enumValue.name().replace("_", " ");
    }

    /**
     * Allows user to select multiple enum values via comma-separated input.
     * 
     * @param values  Array of enum values to choose from
     * @param scanner Scanner for user input
     * @return Set of selected enum values
     */
    private static <T extends Enum<T>> Set<T> selectMultipleEnums(T[] values, Scanner scanner) {
        Set<T> selected = new HashSet<>();

        while (true) {
            System.out.print("Enter choices: ");
            String input = scanner.nextLine().trim();

            try {
                String[] parts = input.split(",");
                boolean valid = false;

                for (String part : parts) {
                    int index = Integer.parseInt(part.trim()) - 1;
                    if (index >= 0 && index < values.length) {
                        selected.add(values[index]);
                        valid = true;
                    }
                }

                if (valid) {
                    return selected;
                }

                System.out.println("⚠️  Please select at least one valid option.");
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Invalid format. Use numbers separated by commas (e.g., 1, 2).");
            }
        }
    }

    /**
     * Allows user to select a single enum value from a list.
     * 
     * @param prompt  Prompt message to display
     * @param values  List of enum values to choose from
     * @param scanner Scanner for user input
     * @return Selected enum value
     */
    private static <T extends Enum<T>> T selectEnumFromList(String prompt, List<T> values, Scanner scanner) {
        System.out.println("\n" + prompt);

        // Display options
        for (int i = 0; i < values.size(); i++) {
            String displayName = getEnumDisplayName(values.get(i));
            System.out.printf("   %d. %s%n", i + 1, displayName);
        }

        // Get user choice
        while (true) {
            System.out.print("Enter choice (1-" + values.size() + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= values.size()) {
                    return values.get(choice - 1);
                }
                System.out.println("⚠️  Please enter a number between 1 and " + values.size() + ".");
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Allows user to select a single enum value from an array.
     * 
     * @param prompt  Prompt message to display
     * @param values  Array of enum values to choose from
     * @param scanner Scanner for user input
     * @return Selected enum value
     */
    public static <T extends Enum<T>> T selectEnum(String prompt, T[] values, Scanner scanner) {
        System.out.println("\n" + prompt);
        displayEnumOptions(values);

        while (true) {
            System.out.print("Enter choice (1-" + values.length + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= values.length) {
                    return values[choice - 1];
                }
                System.out.println("⚠️  Please enter a number between 1 and " + values.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Gets a valid password with strong validation.
     */
    public static String getValidPassword(Scanner scanner) {
        System.out.println("\nPassword Requirements:");
        System.out.println("   • At least 8 characters");
        System.out.println("   • At least one uppercase letter");
        System.out.println("   • At least one lowercase letter");
        System.out.println("   • At least one digit");
        System.out.println("   • At least one special character (!@#$%^&*...)");

        while (true) {
            System.out.print("\nEnter password: ");
            String password = scanner.nextLine();

            if (password.length() < 8) {
                System.out.println("   ⚠️  Password must be at least 8 characters!");
                continue;
            }
            if (!password.matches(".*[A-Z].*")) {
                System.out.println("   ⚠️  Password must contain at least one uppercase letter!");
                continue;
            }
            if (!password.matches(".*[a-z].*")) {
                System.out.println("   ⚠️  Password must contain at least one lowercase letter!");
                continue;
            }
            if (!password.matches(".*\\d.*")) {
                System.out.println("   ⚠️  Password must contain at least one digit!");
                continue;
            }
            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                System.out.println("   ⚠️  Password must contain at least one special character!");
                continue;
            }

            return password;
        }
    }

    /**
     * Gets a valid double within range.
     */
    public static double getValidDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("   ⚠️  Value must be between " + min + " and " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println("   ⚠️  Please enter a valid number!");
            }
        }
    }

    /**
     * Gets a valid date of birth.
     */
    public static LocalDate getValidDateOfBirth(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Enter date of birth (YYYY-MM-DD): ");
            try {
                LocalDate dob = LocalDate.parse(scanner.nextLine().trim(), formatter);
                if (dob.isAfter(LocalDate.now())) {
                    System.out.println("   ⚠️  Date of birth cannot be in the future!");
                    continue;
                }
                if (dob.isBefore(LocalDate.now().minusYears(120))) {
                    System.out.println("   ⚠️  Date of birth exceeds age limit!");
                    continue;
                }
                int age = calculateAge(dob);
                if (age < 10) {
                    System.out.println("   ⚠️  You must be at least 10 years old!");
                    continue;
                }
                return dob;
            } catch (DateTimeParseException e) {
                System.out.println("   ⚠️  Invalid date format! Use YYYY-MM-DD (e.g., 1996-03-15)");
            }
        }
    }

    /**
     * Calculates age from date of birth.
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Centers text within a given width.
     * 
     * @param text  Text to center
     * @param width Total width for centering
     * @return Centered text with padding
     */
    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }

    /**
     * Gets a valid non-empty string from the user.
     */
    public static String getValidString(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("   ⚠️  " + errorMessage);
        }
    }

    /**
     * Gets a valid email address from the user.
     */
    public static String getValidEmail(Scanner scanner) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,7}$";
        while (true) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            if (email.matches(emailRegex)) {
                return email;
            }
            System.out.println("   ⚠️  Invalid email format! Please try again.");
        }
    }
}
