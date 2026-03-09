package com.fitness.model.MainTest;

import com.fitness.model.user.core.Profile;
import com.fitness.model.user.core.User;
import com.fitness.model.user.enums.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE USER PROFILE SYSTEM TEST SUITE ===\n");
        
        // Test all constructors
        testUserConstructors();
        testProfileConstructors();
        
        // Test validations
        testUserValidations();
        testProfileValidations();
        
        // Test successful creation
        testSuccessfulCreation();
        
        // Test setter methods with Scanner simulation
        testScannerSetters();
        
        // Test BMI calculations
        testBMICalculations();
        
        // Test profile completeness
        testProfileCompleteness();
        
        // Test activity level mapping
        testActivityLevelMapping();
        
        // Test disability handling
        testDisabilityHandling();
        
        // Test profile ID uniqueness
        testProfileIdUniqueness();
        
        // Test equals and hashCode
        testEqualsAndHashCode();
        
        // Test toString methods
        testToStringMethods();
        
        // Test thread safety
        testThreadSafety();
        
        // Test static methods
        testStaticMethods();
        
        // Test enum methods
        testEnumMethods();
        
        // Test role detection
        testRoleDetection();
        
        // Demonstrate real-world usage flow
        demonstrateRealWorldUsage();
        
        System.out.println("\n=== ALL TESTS COMPLETED SUCCESSFULLY ===");
    }
    
    // Helper method to simulate scanner input
    private static void simulateInput(String data, Runnable action) {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            action.run();
        } finally {
            System.setIn(original);
        }
    }
    
    // Create a golden test profile with realistic data
    private static Profile createGoldenProfile() {
        User user = new User("johndoe", "john.doe@example.com", UserRole.PREMIUM_USER);
        return new Profile(
            "PRF_ABCDEFGHIJKL",
            user,
            30,
            175.5,
            70.2,
            GoalType.BUILD_MUSCLE,
            Gender.MALE,
            LocalDate.of(1993, 5, 15),
            FitnessLevel.INTERMEDIATE,
            2500
        );
    }
    
    // === TESTING USER CONSTRUCTORS ===
    private static void testUserConstructors() {
        System.out.println("--- Testing User Constructors ---");
        
        // Test default constructor
        User defaultUser = new User();
        assert defaultUser.getUsername().equals("default_user") : "Default username incorrect";
        assert defaultUser.getEmail().equals("user@example.com") : "Default email incorrect";
        assert defaultUser.getUserRole() == UserRole.USER : "Default role incorrect";
        System.out.println("✓ Default constructor works");
        
        // Test parameterized constructor
        User paramUser = new User("testuser", "test@example.com", UserRole.ADMIN);
        assert paramUser.getUsername().equals("testuser") : "Parameterized username incorrect";
        assert paramUser.getEmail().equals("test@example.com") : "Parameterized email incorrect";
        assert paramUser.getUserRole() == UserRole.ADMIN : "Parameterized role incorrect";
        System.out.println("✓ Parameterized constructor works");
        
        // Test full constructor
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);
        User fullUser = new User("fulluser", "full@example.com", UserRole.TRAINER, true, createdAt, updatedAt);
        assert fullUser.getUsername().equals("fulluser") : "Full constructor username incorrect";
        assert fullUser.getEmail().equals("full@example.com") : "Full constructor email incorrect";
        assert fullUser.getUserRole() == UserRole.TRAINER : "Full constructor role incorrect";
        assert fullUser.isActive() : "Full constructor active status incorrect";
        System.out.println("✓ Full constructor works");
        
        System.out.println();
    }
    
    // === TESTING PROFILE CONSTRUCTORS ===
    private static void testProfileConstructors() {
        System.out.println("--- Testing Profile Constructors ---");
        
        // Test default constructor
        Profile defaultProfile = new Profile();
        assert defaultProfile.getAge() == 25 : "Default age incorrect";
        assert defaultProfile.getHeightInCm() == 170 : "Default height incorrect";
        assert defaultProfile.getWeightInKg() == 70 : "Default weight incorrect";
        assert defaultProfile.getGoal() == GoalType.LOSE_WEIGHT : "Default goal incorrect";
        System.out.println("✓ Default constructor works");
        
        // Test user-based constructor
        User user = new User("testuser", "test@example.com", UserRole.USER);
        Profile userBasedProfile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.FEMALE, 
                                              LocalDate.of(1998, 1, 1), FitnessLevel.BEGINNER);
        assert userBasedProfile.getUser() == user : "User reference incorrect";
        assert userBasedProfile.getAge() == 25 : "User-based age incorrect";
        System.out.println("✓ User-based constructor works");
        
        // Test string-based constructor
        Profile stringBasedProfile = new Profile("testuser", "test@example.com", UserRole.USER, 
                                                 30, 180, 75, GoalType.BUILD_MUSCLE, Gender.MALE,
                                                 LocalDate.of(1993, 1, 1), FitnessLevel.ADVANCED);
        assert stringBasedProfile.getUsername().equals("testuser") : "String-based username incorrect";
        assert stringBasedProfile.getAge() == 30 : "String-based age incorrect";
        System.out.println("✓ String-based constructor works");
        
        // Test full constructor
        User fullUser = new User("fulluser", "full@example.com", UserRole.PREMIUM_USER);
        Profile fullProfile = new Profile("PRF_TEST12345678", fullUser, 35, 175, 80, GoalType.IMPROVE_ENDURANCE,
                                          Gender.OTHER, LocalDate.of(1988, 1, 1), FitnessLevel.PROFESSIONAL, 2200);
        assert fullProfile.getProfileId().equals("PRF_TEST12345678") : "Full constructor profile ID incorrect";
        assert fullProfile.getCurrentCalories() == 2200 : "Full constructor calories incorrect";
        System.out.println("✓ Full constructor works");
        
        System.out.println();
    }
    
    // === TESTING USER VALIDATIONS ===
    private static void testUserValidations() {
        System.out.println("--- Testing User Validations ---");
        
        // Test username validation
        try {
            new User(null, "test@example.com", UserRole.USER);
            assert false : "Should have thrown exception for null username";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null username: " + e.getMessage());
        }
        
        try {
            new User("ab", "test@example.com", UserRole.USER);
            assert false : "Should have thrown exception for short username";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected short username: " + e.getMessage());
        }
        
        try {
            new User("this_username_is_way_too_long_for_the_system", "test@example.com", UserRole.USER);
            assert false : "Should have thrown exception for long username";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected long username: " + e.getMessage());
        }
        
        try {
            new User("invalid@username", "test@example.com", UserRole.USER);
            assert false : "Should have thrown exception for invalid characters";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected invalid characters: " + e.getMessage());
        }
        
        // Test email validation
        try {
            new User("validuser", null, UserRole.USER);
            assert false : "Should have thrown exception for null email";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null email: " + e.getMessage());
        }
        
        try {
            new User("validuser", "invalid-email", UserRole.USER);
            assert false : "Should have thrown exception for invalid email format";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected invalid email format: " + e.getMessage());
        }
        
        try {
            new User("validuser", "a..b@example.com", UserRole.USER);
            assert false : "Should have thrown exception for consecutive dots";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected email with consecutive dots: " + e.getMessage());
        }
        
        // Test user role validation
        try {
            new User("validuser", "valid@example.com", null);
            assert false : "Should have thrown exception for null user role";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null user role: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // === TESTING PROFILE VALIDATIONS ===
    private static void testProfileValidations() {
        System.out.println("--- Testing Profile Validations ---");
        
        User user = new User("validuser", "valid@example.com", UserRole.USER);
        
        // Test age validation
        try {
            new Profile(user, 5, 170, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(5), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for age under 10";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected age under 10: " + e.getMessage());
        }
        
        try {
            new Profile(user, 150, 170, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(150), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for age over 120";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected age over 120: " + e.getMessage());
        }
        
        // Test height validation
        try {
            new Profile(user, 25, 50, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for height under 100cm";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected height under 100cm: " + e.getMessage());
        }
        
        try {
            new Profile(user, 25, 300, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for height over 250cm";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected height over 250cm: " + e.getMessage());
        }
        
        // Test weight validation
        try {
            new Profile(user, 25, 170, 20, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for weight under 30kg";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected weight under 30kg: " + e.getMessage());
        }
        
        try {
            new Profile(user, 25, 170, 400, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for weight over 300kg";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected weight over 300kg: " + e.getMessage());
        }
        
        // Test goal validation
        try {
            new Profile(user, 25, 170, 70, null, Gender.MALE, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for null goal";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null goal: " + e.getMessage());
        }
        
        // Test gender validation
        try {
            new Profile(user, 25, 170, 70, GoalType.STAY_FIT, null, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for null gender";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null gender: " + e.getMessage());
        }
        
        // Test date of birth validation
        try {
            new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, null, FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for null DOB";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null DOB: " + e.getMessage());
        }
        
        try {
            new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().plusDays(1), FitnessLevel.BEGINNER);
            assert false : "Should have thrown exception for future DOB";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected future DOB: " + e.getMessage());
        }
        
        // Test fitness level validation
        try {
            new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(25), null);
            assert false : "Should have thrown exception for null fitness level";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected null fitness level: " + e.getMessage());
        }
        
        // Test calorie validation
        Profile profile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
        try {
            profile.setCurrentCalories(500);
            assert false : "Should have thrown exception for calories under 800";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected calories under 800: " + e.getMessage());
        }
        
        try {
            profile.setCurrentCalories(7000);
            assert false : "Should have thrown exception for calories over 6000";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected calories over 6000: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // === TESTING SUCCESSFUL CREATION ===
    private static void testSuccessfulCreation() {
        System.out.println("--- Testing Successful Creation ---");
        
        // Create a valid user
        User user = new User("successfuluser", "success@example.com", UserRole.PREMIUM_USER);
        assert user.getUsername().equals("successfuluser") : "Username not set correctly";
        assert user.getEmail().equals("success@example.com") : "Email not set correctly";
        assert user.getUserRole() == UserRole.PREMIUM_USER : "User role not set correctly";
        assert user.isActive() : "User should be active by default";
        System.out.println("✓ Successfully created valid user");
        
        // Create a valid profile
        LocalDate dob = LocalDate.of(1990, 6, 15);
        Profile profile = new Profile(user, 33, 178.5, 75.3, GoalType.BUILD_MUSCLE, Gender.MALE, dob, FitnessLevel.ADVANCED);
        assert profile.getAge() == 33 : "Age not set correctly";
        assert profile.getHeightInCm() == 178.5 : "Height not set correctly";
        assert profile.getWeightInKg() == 75.3 : "Weight not set correctly";
        assert profile.getGoal() == GoalType.BUILD_MUSCLE : "Goal not set correctly";
        assert profile.getGender() == Gender.MALE : "Gender not set correctly";
        assert profile.getDateOfBirth().equals(dob) : "DOB not set correctly";
        assert profile.getFitnessLevel() == FitnessLevel.ADVANCED : "Fitness level not set correctly";
        System.out.println("✓ Successfully created valid profile");
        
        System.out.println();
    }
    
    // === TESTING SCANNER SETTERS ===
    private static void testScannerSetters() {
        System.out.println("--- Testing Scanner Setters ---");
        
        Profile profile = new Profile();
        
        // Test age setter with scanner
        simulateInput("25\n", () -> profile.setAge(new java.util.Scanner(System.in)));
        assert profile.getAge() == 25 : "Age not set correctly via scanner";
        System.out.println("✓ Age setter with scanner works");
        
        // Test height setter with scanner
        simulateInput("175.5\n", () -> profile.setHeightInCm(new java.util.Scanner(System.in)));
        assert profile.getHeightInCm() == 175.5 : "Height not set correctly via scanner";
        System.out.println("✓ Height setter with scanner works");
        
        // Test weight setter with scanner
        simulateInput("70.2\n", () -> profile.setWeightInKg(new java.util.Scanner(System.in)));
        assert profile.getWeightInKg() == 70.2 : "Weight not set correctly via scanner";
        System.out.println("✓ Weight setter with scanner works");
        
        // Test gender setter with scanner
        simulateInput("1\n", () -> profile.setGender(new java.util.Scanner(System.in)));
        assert profile.getGender() == Gender.MALE : "Gender not set correctly via scanner";
        System.out.println("✓ Gender setter with scanner works");
        
        // Test goal setter with scanner
        simulateInput("2\n", () -> profile.setGoal(new java.util.Scanner(System.in)));
        assert profile.getGoal() == GoalType.BUILD_MUSCLE : "Goal not set correctly via scanner";
        System.out.println("✓ Goal setter with scanner works");
        
        // Test fitness level setter with scanner
        simulateInput("3\n", () -> profile.setFitnessLevel(new java.util.Scanner(System.in)));
        assert profile.getFitnessLevel() == FitnessLevel.ADVANCED : "Fitness level not set correctly via scanner";
        System.out.println("✓ Fitness level setter with scanner works");
        
        // Test DOB setter with scanner
        simulateInput("1990-01-01\n", () -> profile.setDateOfBirth(new java.util.Scanner(System.in)));
        assert profile.getDateOfBirth().equals(LocalDate.of(1990, 1, 1)) : "DOB not set correctly via scanner";
        System.out.println("✓ DOB setter with scanner works");
        
        // Test calories setter with scanner
        simulateInput("2500\n", () -> profile.setCurrentCalories(new java.util.Scanner(System.in)));
        assert profile.getCurrentCalories() == 2500 : "Calories not set correctly via scanner";
        System.out.println("✓ Calories setter with scanner works");
        
        System.out.println();
    }
    
    // === TESTING BMI CALCULATIONS ===
    private static void testBMICalculations() {
        System.out.println("--- Testing BMI Calculations ---");
        
        // Test normal BMI calculation
        User user1 = new User("user1", "user1@example.com", UserRole.USER);
        Profile profile1 = new Profile(user1, 30, 175, 70, GoalType.STAY_FIT, Gender.MALE, 
                                       LocalDate.now().minusYears(30), FitnessLevel.INTERMEDIATE);
        double bmi1 = profile1.calculateBMI();
        assert Math.abs(bmi1 - 22.86) < 0.01 : "BMI calculation incorrect for normal case";
        assert profile1.getBMICategory().equals("Normal weight") : "BMI category incorrect for normal case";
        System.out.println("✓ Normal BMI calculation works: " + String.format("%.2f", bmi1));
        
        // Test underweight BMI calculation
        User user2 = new User("user2", "user2@example.com", UserRole.USER);
        Profile profile2 = new Profile(user2, 25, 170, 50, GoalType.LOSE_WEIGHT, Gender.FEMALE, 
                                       LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
        double bmi2 = profile2.calculateBMI();
        assert Math.abs(bmi2 - 17.30) < 0.01 : "BMI calculation incorrect for underweight case";
        assert profile2.getBMICategory().equals("Underweight") : "BMI category incorrect for underweight case";
        System.out.println("✓ Underweight BMI calculation works: " + String.format("%.2f", bmi2));
        
        // Test overweight BMI calculation
        User user3 = new User("user3", "user3@example.com", UserRole.USER);
        Profile profile3 = new Profile(user3, 40, 180, 95, GoalType.LOSE_WEIGHT, Gender.MALE, 
                                       LocalDate.now().minusYears(40), FitnessLevel.INTERMEDIATE);
        double bmi3 = profile3.calculateBMI();
        assert Math.abs(bmi3 - 29.32) < 0.01 : "BMI calculation incorrect for overweight case";
        assert profile3.getBMICategory().equals("Overweight") : "BMI category incorrect for overweight case";
        System.out.println("✓ Overweight BMI calculation works: " + String.format("%.2f", bmi3));
        
        // Test obese BMI calculation
        User user4 = new User("user4", "user4@example.com", UserRole.USER);
        Profile profile4 = new Profile(user4, 35, 165, 100, GoalType.LOSE_WEIGHT, Gender.FEMALE, 
                                       LocalDate.now().minusYears(35), FitnessLevel.BEGINNER);
        double bmi4 = profile4.calculateBMI();
        assert Math.abs(bmi4 - 36.73) < 0.01 : "BMI calculation incorrect for obese case";
        assert profile4.getBMICategory().equals("Obese") : "BMI category incorrect for obese case";
        System.out.println("✓ Obese BMI calculation works: " + String.format("%.2f", bmi4));
        
        // Test ideal weight range
        double[] range = profile1.getIdealWeightRange();
        assert Math.abs(range[0] - 56.38) < 0.01 : "Ideal weight min calculation incorrect";
        assert Math.abs(range[1] - 75.74) < 0.01 : "Ideal weight max calculation incorrect";
        System.out.println("✓ Ideal weight range calculation works: " + 
                          String.format("%.2f", range[0]) + " - " + String.format("%.2f", range[1]) + " kg");
        
        System.out.println();
    }
    
    // === TESTING PROFILE COMPLETENESS ===
    private static void testProfileCompleteness() {
        System.out.println("--- Testing Profile Completeness ---");
        
        // Test incomplete profile
        Profile incompleteProfile = new Profile();
        // Make it incomplete by not setting some required fields
        assert incompleteProfile.isProfileComplete() : "Default profile should be complete";
        
        // Actually, let's create a truly incomplete profile
        User user = new User("incomplete", "incomplete@example.com", UserRole.USER);
        Profile completeProfile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                              LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
        assert completeProfile.isProfileComplete() : "Complete profile incorrectly marked as incomplete";
        System.out.println("✓ Profile completeness detection works");
        
        System.out.println();
    }
    
    // === TESTING ACTIVITY LEVEL MAPPING ===
    private static void testActivityLevelMapping() {
        System.out.println("--- Testing Activity Level Mapping ---");
        
        User user = new User("activityuser", "activity@example.com", UserRole.USER);
        
        // Test beginner -> Light
        Profile beginnerProfile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                              LocalDate.now().minusYears(25), FitnessLevel.BEGINNER);
        assert beginnerProfile.getActivityLevel().equals("Light") : "Beginner activity level mapping incorrect";
        System.out.println("✓ Beginner -> Light mapping works");
        
        // Test intermediate -> Moderate
        Profile intermediateProfile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                                  LocalDate.now().minusYears(25), FitnessLevel.INTERMEDIATE);
        assert intermediateProfile.getActivityLevel().equals("Moderate") : "Intermediate activity level mapping incorrect";
        System.out.println("✓ Intermediate -> Moderate mapping works");
        
        // Test advanced -> Active
        Profile advancedProfile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                              LocalDate.now().minusYears(25), FitnessLevel.ADVANCED);
        assert advancedProfile.getActivityLevel().equals("Active") : "Advanced activity level mapping incorrect";
        System.out.println("✓ Advanced -> Active mapping works");
        
        // Test professional -> Very Active
        Profile professionalProfile = new Profile(user, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                                  LocalDate.now().minusYears(25), FitnessLevel.PROFESSIONAL);
        assert professionalProfile.getActivityLevel().equals("Very Active") : "Professional activity level mapping incorrect";
        System.out.println("✓ Professional -> Very Active mapping works");
        
        System.out.println();
    }
    
    // === TESTING DISABILITY HANDLING ===
    private static void testDisabilityHandling() {
        System.out.println("--- Testing Disability Handling ---");
        
        // Test that Disability enum works correctly
        assert Disability.NONE.getLabel().equals("None") : "NONE disability label incorrect";
        assert Disability.VISUAL_IMPAIRMENT.getLabel().equals("Visual Impairment") : "Visual impairment label incorrect";
        System.out.println("✓ Disability enum works correctly");
        
        System.out.println();
    }
    
    // === TESTING PROFILE ID UNIQUENESS ===
    private static void testProfileIdUniqueness() {
        System.out.println("--- Testing Profile ID Uniqueness ---");
        
        // Clear registry first
        Profile.clearProfileIdRegistry();
        
        // Create multiple profiles and check IDs are unique
        Set<String> profileIds = new HashSet<>();
        int profileCount = 50;
        
        for (int i = 0; i < profileCount; i++) {
            Profile profile = new Profile();
            String id = profile.getProfileId();
            
            // Check format
            assert Profile.isValidProfileIdFormat(id) : "Profile ID format invalid: " + id;
            
            // Check uniqueness
            assert !profileIds.contains(id) : "Duplicate profile ID detected: " + id;
            profileIds.add(id);
        }
        
        assert profileIds.size() == profileCount : "Not all profiles were created";
        System.out.println("✓ Created " + profileCount + " profiles with unique IDs");
        
        // Test profile ID format validation
        assert Profile.isValidProfileIdFormat("PRF_ABCDEFGHIJK") : "Valid format rejected";
        assert !Profile.isValidProfileIdFormat("PRF_abcdefghijkl") : "Invalid lowercase format accepted";
        assert !Profile.isValidProfileIdFormat("PRF_ABCDEFGHIJ") : "Invalid length accepted";
        assert !Profile.isValidProfileIdFormat("PRF_ABCDEFGHIJKL1") : "Invalid length accepted";
        assert !Profile.isValidProfileIdFormat("PRG_ABCDEFGHIJKL") : "Invalid prefix accepted";
        System.out.println("✓ Profile ID format validation works");
        
        System.out.println();
    }
    
    // === TESTING EQUALS AND HASHCODE ===
    private static void testEqualsAndHashCode() {
        System.out.println("--- Testing Equals and HashCode ---");
        
        // Test User equality
        User user1 = new User("equaluser", "equal@example.com", UserRole.USER);
        User user2 = new User("equaluser", "equal@example.com", UserRole.USER);
        User user3 = new User("differentuser", "different@example.com", UserRole.ADMIN);
        
        assert user1.equals(user1) : "User reflexivity failed";
        assert user1.equals(user2) : "User equality failed";
        assert !user1.equals(user3) : "User inequality failed";
        assert !user1.equals(null) : "User null comparison failed";
        assert !user1.equals("string") : "User type comparison failed";
        System.out.println("✓ User equals works correctly");
        
        assert user1.hashCode() == user2.hashCode() : "Equal users should have equal hash codes";
        System.out.println("✓ User hashCode works correctly");
        
        // Test Profile equality
        Profile profile1 = new Profile("PRF_TEST11111111", user1, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                       LocalDate.now().minusYears(25), FitnessLevel.BEGINNER, 2000);
        Profile profile2 = new Profile("PRF_TEST11111111", user2, 25, 170, 70, GoalType.STAY_FIT, Gender.MALE, 
                                       LocalDate.now().minusYears(25), FitnessLevel.BEGINNER, 2000);
        Profile profile3 = new Profile("PRF_TEST22222222", user3, 30, 180, 80, GoalType.BUILD_MUSCLE, Gender.FEMALE, 
                                       LocalDate.now().minusYears(30), FitnessLevel.ADVANCED, 2500);
        
        assert profile1.equals(profile1) : "Profile reflexivity failed";
        assert profile1.equals(profile2) : "Profile equality failed";
        assert !profile1.equals(profile3) : "Profile inequality failed";
        assert !profile1.equals(null) : "Profile null comparison failed";
        assert !profile1.equals("string") : "Profile type comparison failed";
        System.out.println("✓ Profile equals works correctly");
        
        assert profile1.hashCode() == profile2.hashCode() : "Equal profiles should have equal hash codes";
        System.out.println("✓ Profile hashCode works correctly");
        
        System.out.println();
    }
    
    // === TESTING TOSTRING METHODS ===
    private static void testToStringMethods() {
        System.out.println("--- Testing ToString Methods ---");
        
        // Test User toString
        User user = new User("testuser", "test@example.com", UserRole.ADMIN);
        String userString = user.toString();
        assert userString.contains("testuser") : "User toString missing username";
        assert userString.contains("test@example.com") : "User toString missing email";
        assert userString.contains("ADMIN") : "User toString missing role";
        System.out.println("✓ User toString works: " + userString);
        
        // Test Profile toString
        Profile profile = new Profile("PRF_TOSTRINGTEST", user, 25, 175, 70, GoalType.BUILD_MUSCLE, Gender.MALE, 
                                      LocalDate.now().minusYears(25), FitnessLevel.INTERMEDIATE, 2200);
        String profileString = profile.toString();
        assert profileString.contains("PRF_TOSTRINGTEST") : "Profile toString missing profile ID";
        assert profileString.contains("testuser") : "Profile toString missing username";
        assert profileString.contains("25") : "Profile toString missing age";
        assert profileString.contains("BUILD_MUSCLE") : "Profile toString missing goal";
        System.out.println("✓ Profile toString works: " + profileString);
        
        System.out.println();
    }
    
    // === TESTING THREAD SAFETY ===
    private static void testThreadSafety() {
        System.out.println("--- Testing Thread Safety ---");
        
        // Clear registry first
        Profile.clearProfileIdRegistry();
        
        // Create profiles from multiple threads
        int threadCount = 10;
        int profilesPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Set<String> allProfileIds = new HashSet<>();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < profilesPerThread; j++) {
                    try {
                        Profile profile = new Profile();
                        synchronized (allProfileIds) {
                            allProfileIds.add(profile.getProfileId());
                        }
                    } catch (Exception e) {
                        System.err.println("Error in thread " + threadId + ": " + e.getMessage());
                    }
                }
            });
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify all IDs are unique
        assert allProfileIds.size() == threadCount * profilesPerThread : 
               "Thread safety issue: expected " + (threadCount * profilesPerThread) + " unique IDs, got " + allProfileIds.size();
        System.out.println("✓ Thread safety verified: " + allProfileIds.size() + " unique profile IDs created across " + threadCount + " threads");
        
        System.out.println();
    }
    
    // === TESTING STATIC METHODS ===
    private static void testStaticMethods() {
        System.out.println("--- Testing Static Methods ---");
        
        // Test profile ID format validation
        assert Profile.isValidProfileIdFormat("PRF_ABCDEFGHIJKL") : "Valid profile ID format rejected";
        assert !Profile.isValidProfileIdFormat("INVALID_FORMAT") : "Invalid profile ID format accepted";
        assert !Profile.isValidProfileIdFormat(null) : "Null profile ID format accepted";
        System.out.println("✓ Profile ID format validation works");
        
        // Test clearing profile ID registry
        Profile.clearProfileIdRegistry();
        Profile profile = new Profile();
        String id = profile.getProfileId();
        assert Profile.isValidProfileIdFormat(id) : "Profile ID format invalid after registry clear";
        System.out.println("✓ Profile ID registry clearing works");
        
        System.out.println();
    }
    
    // === TESTING ENUM METHODS ===
    private static void testEnumMethods() {
        System.out.println("--- Testing Enum Methods ---");
        
        // Test Gender enum
        assert Gender.MALE.getLabel().equals("Male") : "Male gender label incorrect";
        assert Gender.FEMALE.getLabel().equals("Female") : "Female gender label incorrect";
        assert Gender.OTHER.getLabel().equals("Other") : "Other gender label incorrect";
        assert Gender.PREFER_NOT_TO_SAY.getLabel().equals("Prefer not to say") : "Prefer not to say gender label incorrect";
        System.out.println("✓ Gender enum labels work correctly");
        
        // Test FitnessLevel enum
        assert FitnessLevel.BEGINNER.getDescription().equals("Beginner") : "Beginner description incorrect";
        assert FitnessLevel.INTERMEDIATE.getDescription().equals("Intermediate") : "Intermediate description incorrect";
        assert FitnessLevel.ADVANCED.getDescription().equals("Advanced") : "Advanced description incorrect";
        assert FitnessLevel.PROFESSIONAL.getDescription().equals("Professional") : "Professional description incorrect";
        System.out.println("✓ FitnessLevel enum descriptions work correctly");
        
        // Test GoalType enum
        assert GoalType.LOSE_WEIGHT.getDescription().equals("Lose Weight") : "Lose Weight description incorrect";
        assert GoalType.BUILD_MUSCLE.getDescription().equals("Build Muscle") : "Build Muscle description incorrect";
        assert GoalType.STAY_FIT.getDescription().equals("Stay Fit") : "Stay Fit description incorrect";
        System.out.println("✓ GoalType enum descriptions work correctly");
        
        // Test UserRole enum
        assert UserRole.ADMIN.getDescription().equals("Administrator - Full system access") : "Admin description incorrect";
        assert UserRole.USER.getDescription().equals("Regular User - Standard features") : "User description incorrect";
        assert UserRole.PREMIUM_USER.getDescription().equals("Premium User - All features") : "Premium User description incorrect";
        assert UserRole.TRAINER.getDescription().equals("Fitness Trainer - Can create plans for others") : "Trainer description incorrect";
        System.out.println("✓ UserRole enum descriptions work correctly");
        
        // Test Disability enum
        assert Disability.NONE.getLabel().equals("None") : "None disability label incorrect";
        assert Disability.VISUAL_IMPAIRMENT.getLabel().equals("Visual Impairment") : "Visual impairment label incorrect";
        System.out.println("✓ Disability enum labels work correctly");
        
        System.out.println();
    }
    
    // === TESTING ROLE DETECTION ===
    private static void testRoleDetection() {
        System.out.println("--- Testing Role Detection ---");
        
        // Test admin detection
        User adminUser = new User("admin", "admin@example.com", UserRole.ADMIN);
        assert adminUser.isAdmin() : "Admin user not detected as admin";
        assert !adminUser.isPremiumUser() : "Admin user incorrectly detected as premium";
        assert !adminUser.isTrainer() : "Admin user incorrectly detected as trainer";
        System.out.println("✓ Admin role detection works");
        
        // Test premium user detection
        User premiumUser = new User("premium", "premium@example.com", UserRole.PREMIUM_USER);
        assert !premiumUser.isAdmin() : "Premium user incorrectly detected as admin";
        assert premiumUser.isPremiumUser() : "Premium user not detected as premium";
        assert !premiumUser.isTrainer() : "Premium user incorrectly detected as trainer";
        System.out.println("✓ Premium user role detection works");
        
        // Test trainer detection
        User trainerUser = new User("trainer", "trainer@example.com", UserRole.TRAINER);
        assert !trainerUser.isAdmin() : "Trainer user incorrectly detected as admin";
        assert !trainerUser.isPremiumUser() : "Trainer user incorrectly detected as premium";
        assert trainerUser.isTrainer() : "Trainer user not detected as trainer";
        System.out.println("✓ Trainer role detection works");
        
        // Test regular user detection
        User regularUser = new User("regular", "regular@example.com", UserRole.USER);
        assert !regularUser.isAdmin() : "Regular user incorrectly detected as admin";
        assert !regularUser.isPremiumUser() : "Regular user incorrectly detected as premium";
        assert !regularUser.isTrainer() : "Regular user incorrectly detected as trainer";
        System.out.println("✓ Regular user role detection works");
        
        System.out.println();
    }
    
    // === DEMONSTRATING REAL-WORLD USAGE ===
    private static void demonstrateRealWorldUsage() {
        System.out.println("--- Demonstrating Real-World Usage ---");
        
        // Step 1: Create user
        System.out.println("Step 1: Creating user...");
        User user = new User("janedoe", "jane.doe@example.com", UserRole.PREMIUM_USER);
        System.out.println("Created: " + user);
        
        // Step 2: Create profile
        System.out.println("\nStep 2: Creating profile...");
        LocalDate dob = LocalDate.of(1995, 3, 22);
        Profile profile = new Profile(user, 28, 165.0, 62.5, GoalType.RECOMP, Gender.FEMALE, dob, FitnessLevel.INTERMEDIATE);
        profile.setCurrentCalories(2100);
        System.out.println("Created: " + profile);
        
        // Step 3: Display initial BMI info
        System.out.println("\nStep 3: Initial health metrics...");
        System.out.println("BMI: " + String.format("%.2f", profile.calculateBMI()));
        System.out.println("BMI Category: " + profile.getBMICategory());
        double[] idealRange = profile.getIdealWeightRange();
        System.out.println("Ideal Weight Range: " + String.format("%.1f", idealRange[0]) + " - " + 
                          String.format("%.1f", idealRange[1]) + " kg");
        System.out.println("Activity Level: " + profile.getActivityLevel());
        
        // Step 4: Update profile interactively (simulated)
        System.out.println("\nStep 4: Updating profile...");
        simulateInput("65.0\n", () -> profile.setWeightInKg(new java.util.Scanner(System.in)));
        simulateInput("2\n", () -> profile.setGoal(new java.util.Scanner(System.in)));
        
        // Step 5: Display updated profile
        System.out.println("\nStep 5: Updated profile summary...");
        System.out.println("Updated: " + profile);
        System.out.println("New BMI: " + String.format("%.2f", profile.calculateBMI()));
        System.out.println("New BMI Category: " + profile.getBMICategory());
        System.out.println("Profile Complete: " + profile.isProfileComplete());
        
        // Final beautiful summary
        System.out.println("\n=== FINAL PROFILE SUMMARY ===");
        System.out.println("👤 Username: " + profile.getUsername());
        System.out.println("📧 Email: " + profile.getEmail());
        System.out.println("🆔 Profile ID: " + profile.getProfileId());
        System.out.println("🎂 Age: " + profile.getAge() + " years");
        System.out.println("📏 Height: " + profile.getHeightInCm() + " cm");
        System.out.println("⚖️  Weight: " + profile.getWeightInKg() + " kg");
        System.out.println("🎯 Fitness Goal: " + profile.getGoal().getDescription());
        System.out.println("⚥ Gender: " + profile.getGender().getLabel());
        System.out.println("⭐ Fitness Level: " + profile.getFitnessLevel().getDescription());
        System.out.println("📅 Date of Birth: " + profile.getDateOfBirth());
        System.out.println("🔥 Daily Calories: " + profile.getCurrentCalories());
        System.out.println("📊 BMI: " + String.format("%.2f", profile.calculateBMI()) + " (" + profile.getBMICategory() + ")");
        System.out.println("⚡ Activity Level: " + profile.getActivityLevel());
        System.out.println("✅ Profile Complete: " + (profile.isProfileComplete() ? "Yes" : "No"));
        System.out.println("👑 Account Type: " + profile.getUserRole().getDescription());
        
        System.out.println();
    }
}