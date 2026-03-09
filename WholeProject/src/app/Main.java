package app;

import app.util.InputUtils;

import model.user.domain.Profile;
import model.user.enums.Gender;
import model.user.enums.GoalType;
import model.user.enums.FitnessLevel;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.Map;
import model.workout.domain.PlanContext;
import model.workout.service.WorkoutPlanGenerator;
import model.workout.domain.WorkoutPlan;
import model.workout.enums.TrainingSplit;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.service.WorkoutService;
import model.nutrition.repository.NutritionRepository;
import model.nutrition.domain.IDietPlan;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static model.user.repository.UserRepository userRepository = new model.user.repository.UserRepository();
    private static Profile currentProfile; // Session for JavaFX

    public static model.user.repository.UserRepository getUserRepository() {
        return userRepository;
    }

    public static void setCurrentProfile(Profile profile) {
        currentProfile = profile;
        if (profile != null) {
            UserSession.getInstance().login(profile.getUser(), profile);
        }
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }

    // New Repositories
    private static NutritionRepository nutritionRepository = new NutritionRepository();

    public static NutritionRepository getNutritionRepository() {
        return nutritionRepository;
    }

    private static model.workout.repository.WorkoutRepository workoutRepository = new model.workout.repository.WorkoutRepository();
    private static model.workout.repository.PlanContextRepository planContextRepository = new model.workout.repository.PlanContextRepository();
    // private static model.trackbodyweight.tracking.TrackingRepository
    // trackingRepository = new model.trackbodyweight.tracking.TrackingRepository();

    // Date tracker for sequential weight logging (starts at Today)
    private static LocalDate currentTrackingDate = LocalDate.now();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n╔" + "═".repeat(70) + "╗");
            System.out.println("║" + InputUtils.centerText("FITNESS PLANNER - LOGIN PAGE", 70) + "║");
            System.out.println("╚" + "═".repeat(70) + "╝\n");

            System.out.println("1. Sign Up (Create New Account)");
            System.out.println("2. Sign In (Existing User)");
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                // Sign Up Flow
                Profile userProfile = signUp();
                if (userProfile != null) {
                    // SAVE TO DATABASE
                    boolean saved = userRepository.saveUser(userProfile);
                    if (saved) {
                        System.out.println("\n" + "═".repeat(72));
                        System.out.println("✅ Sign Up Successful!");
                        System.out.println("═".repeat(72));
                        // Auto-login after signup
                        homePage(userProfile);
                    } else {
                        System.out.println("\n❌ Error saving user to database. Please try again.");
                    }
                }

            } else if (choice.equals("2")) {
                // Sign In Flow
                Profile authenticatedUser = signIn(scanner);
                if (authenticatedUser != null) {
                    homePage(authenticatedUser);
                }

            } else if (choice.equals("3")) {
                // Exit
                System.out.println("\n👋 Thank you for using Fitness Planner!");
                break;

            } else {
                System.out.println("\n❌ Invalid choice. Please enter 1-3.\n");
            }
        }

        scanner.close();
    }

    // Replacing signIn signature and logic
    public static Profile signIn(Scanner scanner) {
        System.out.println("╔" + "═".repeat(70) + "╗");
        System.out.println("║" + InputUtils.centerText("FITNESS PLANNER - SIGN IN", 70) + "║");
        System.out.println("╚" + "═".repeat(70) + "╝\n");

        String email;
        Profile userProfile = null;

        while (true) {
            email = InputUtils.getValidEmail(scanner);

            // Check DB
            userProfile = userRepository.getUserByEmail(email);

            if (userProfile != null) {
                System.out.println("✅ Email found!");
                break;
            } else {
                System.out.println("❌ Email not found. Please try again.\n");
            }
        }

        while (true) {
            System.out.print("Enter your password: ");
            String enteredPassword = scanner.nextLine();
            String storedPassword = userProfile.getUser().getPassword();

            if (enteredPassword.equals(storedPassword)) {
                System.out.println("\n" + "═".repeat(72));
                System.out.println("✅ Login Successful!");
                System.out.println("   Welcome back, " + userProfile.getUsername() + "!");
                System.out.println("═".repeat(72));

                // Load detached data (Nutrition Plan, etc.)
                loadUserNutritionPlan(userProfile);

                // Load weight history
                // Weight history is auto-loaded by Profile/WeightTracker

                // NEW: Load Workout Plan from DB
                model.workout.repository.WorkoutRepository workoutRepo = new model.workout.repository.WorkoutRepository();
                model.workout.domain.WorkoutPlan savedPlan = workoutRepo.getWorkoutPlan(userProfile);
                if (savedPlan != null) {
                    userProfile.setCurrentWorkoutPlan(savedPlan);
                    System.out.println("✅ Loaded your existing workout plan.");
                }

                // --- TEMP DEBUG: Print DB Contents ---
                try (java.sql.Connection conn = config.DatabaseConnection.getInstance().getConnection();
                        java.sql.PreparedStatement stmt = conn
                                .prepareStatement("SELECT * FROM weekly_weight_averages WHERE profile_id = ?")) {
                    stmt.setString(1, userProfile.getProfileId());
                    java.sql.ResultSet rs = stmt.executeQuery();
                    System.out.println("\n[DEBUG] DB Table Content for this user:");
                    boolean hasRows = false;
                    while (rs.next()) {
                        hasRows = true;
                        System.out.println(" - Week " + rs.getInt("week_number") + ": "
                                + rs.getDouble("average_weight_kg") + "kg (" + rs.getTimestamp("recorded_at") + ")");
                    }
                    if (!hasRows)
                        System.out.println(" [EMPTY] No rows found in DB for this profile.");
                    System.out.println("---------------------------------------\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // -------------------------------------

                // Initialize Diet Manager and check for adaptive updates based on history
                userProfile.initializeDietManager();
                if (userProfile.updateNutritionalPlan()) {
                    saveNutritionPlanToDatabase(userProfile);
                    userRepository.updateProfile(userProfile); // Persist the new last_adjusted_week
                }

                return userProfile;
            } else {
                System.out.println("❌ Incorrect password. Please try again.\n");
            }
        }
    }

    /**
     * The main dashboard after login.
     * Contains options to navigate to different flows.
     *
     * @param profile The currently logged-in user's profile.
     */
    public static void homePage(Profile profile) {
        while (true) {
            System.out.println("\n╔" + "═".repeat(70) + "╗");
            System.out.println("║" + InputUtils.centerText("HOME PAGE - DASHBOARD", 70) + "║");
            System.out.println("╚" + "═".repeat(70) + "╝\n");

            System.out.println("Welcome back, " + profile.getUsername() + "!");

            double lastWeeklyAvg = profile.getLastCompletedWeekAvg();
            if (lastWeeklyAvg > 0) {
                System.out.println("Last Weekly Average Weight: " + String.format("%.2f", lastWeeklyAvg) + " kg");
            } else {
                System.out.println("You have no weekly average yet. Start tracking your bodyweight.");
            }
            System.out.println("─".repeat(70));

            System.out.println("1. Generate/View Workout Plan");
            System.out.println("2. Generate/View Nutritional Plan");
            System.out.println("3. Track Your Body Weight");
            System.out.println("4. View Profile (Profile Icon)");
            System.out.print("\nEnter your choice (1-4): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                generateWorkoutPlanFlow(profile);
            } else if (choice.equals("2")) {
                generateNutritionalPlanFlow(profile);
            } else if (choice.equals("3")) {
                trackYourBodyWeightFlow(profile);
            } else if (choice.equals("4")) {
                boolean loggedOut = viewProfileFlow(profile);
                if (loggedOut) {
                    break; // Return to Login Page
                }
            } else {
                System.out.println("\n❌ Invalid choice. Please enter 1-4.\n");
            }
        }
    }

    /**
     * Loads the user's nutrition plan from the database if it exists.
     * Reconstructs the logic object (IDietPlan) from the DTO (NutritionPlan).
     * 
     * @param profile The user's profile
     */
    private static void loadUserNutritionPlan(Profile profile) {
        model.nutrition.domain.IDietPlan dbPlan = nutritionRepository.getLatestPlanByProfileId(profile.getProfileId());

        if (dbPlan != null && dbPlan.isActive()) {
            System.out.println("🔄 Loading your nutrition plan...");

            profile.setCurrentDietPlan(dbPlan);
            profile.initializeDietManager();
            System.out.println("✅ Nutrition plan loaded successfully!");
        }
    }

    /**
     * Displays profile details and allows profile management actions.
     * 
     * @param profile The user's profile
     * @return true if user chose to logout, false otherwise
     */
    private static boolean viewProfileFlow(Profile profile) {
        while (true) {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("👤 PROFILE DETAILS & ACTIONS");
            System.out.println("═".repeat(70));
            profile.displayProfile();
            System.out.println("═".repeat(70));

            System.out.println("1. Change Password");
            System.out.println("2. Change Goal");
            System.out.println("3. Change Fitness Level");
            System.out.println("4. Logout");
            System.out.println("5. Back to Home Page");
            System.out.print("\nEnter your choice (1-5): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                // Change Password
                System.out.println("\n🔐 CHANGE PASSWORD");
                System.out.println("─".repeat(70));

                // 1. Verify Old Password
                System.out.print("Enter current password: ");
                String currentPassInput = scanner.nextLine();

                if (!currentPassInput.equals(profile.getUser().getPassword())) {
                    System.out.println("❌ Incorrect current password. Access denied.");
                } else {
                    // 2. Enter New Password
                    System.out.println("Enter new password:");
                    String newPassword = InputUtils.getValidPassword(scanner);

                    // 3. Confirm New Password
                    System.out.print("Confirm new password: ");
                    String confirmPassword = scanner.nextLine();

                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println("❌ Passwords do not match. Password change cancelled.");
                    } else {
                        // 4. Update
                        profile.getUser().setPassword(newPassword);
                        if (userRepository.updatePassword(profile.getUser().getUserId(), newPassword)) {
                            System.out.println("✅ Password updated successfully in Database!");
                        } else {
                            System.out.println("⚠️ Password updated locally, but failed to save to Database.");
                        }
                    }
                }

            } else if (choice.equals("2")) {
                // Change Goal
                changeGoalInsideProfile(profile);

            } else if (choice.equals("3")) {
                // Change Fitness Level
                changeFitnessLevelInsideProfile(profile);

            } else if (choice.equals("4")) {
                // Logout
                System.out.println("\n🔒 Logging out...");
                return true; // Signal to homePage to logout

            } else if (choice.equals("5")) {
                // Back to Home Page
                return false;

            } else {
                System.out.println("\n❌ Invalid choice. Please enter 1-5.\n");
            }
        }
    }

    /**
     * Sign-up method that collects user information and creates a Profile.
     * 
     * @return Profile object if successful, null if sign-up fails
     */
    public static Profile signUp() {
        try {
            System.out.println("📝 STEP 1: Account Information");
            System.out.println("─".repeat(70));

            // 1. Username
            // ✅ Valid: "john_doe", "Jane Smith", "user-123", "My Username"
            // ❌ Invalid: "ab" (too short), "a".repeat(21) (too long), "user@name" (special
            // chars)
            String username = InputUtils.getValidInput(scanner,
                    "Enter username (3-20 chars, letters/digits/_/-/spaces): ",
                    input -> input.length() >= 3 && input.length() <= 20 && input.matches("^[a-zA-Z0-9_ -]+$"),
                    "Invalid username format!");

            // 2. Email
            // ✅ Valid: "user@example.com", "john.doe@company.co.uk", "test+tag@domain.org"
            // ❌ Invalid: "plaintext", "missing@domain", "double@@at.com", "no-tld@domain"
            String email;
            while (true) {
                email = InputUtils.getValidEmail(scanner);
                // Ideally check DB for duplication here:
                if (userRepository.getUserByEmail(email) != null) {
                    System.out.println("   ⚠️  Email already registered! Please use a different email.");
                } else {
                    break;
                }
            }

            // 3. Password
            // ✅ Valid: "SecureP@ss123", "MyP@ssw0rd!", "Str0ng#Pass"
            // ❌ Invalid: "short1A!" (too short), "nouppercase1!" (no uppercase),
            // "NOLOWERCASE1!" (no lowercase)
            // "NoDigit@Pass" (no digit), "NoSpecial1Aa" (no special char)
            // "NoDigit@Pass" (no digit), "NoSpecial1Aa" (no special char)
            String password;
            while (true) {
                password = InputUtils.getValidPassword(scanner);

                System.out.print("Confirm password: ");
                String confirm = scanner.nextLine();

                if (password.equals(confirm)) {
                    break;
                }
                System.out.println("❌ Passwords do not match! Please try again.");
            }

            System.out.println("\n📊 STEP 2: Physical Information");
            System.out.println("─".repeat(70));

            // 4. Height
            // ✅ Valid: 150.5, 175.0, 200.0
            // ❌ Invalid: 50.0 (too short), 300.0 (too tall)
            // ❌ Invalid: 50.0 (too short), 300.0 (too tall)
            double height = InputUtils.getValidDouble(scanner, "Enter height (cm, 100-250): ", 100.0, 250.0);

            // 5. Weight
            // ✅ Valid: 50.0, 70.5, 120.0
            // ❌ Invalid: 20.0 (too light), 350.0 (too heavy)
            // ❌ Invalid: 20.0 (too light), 350.0 (too heavy)
            double weight = InputUtils.getValidDouble(scanner, "Enter weight (kg, 30-300): ", 30.0, 300.0);

            // 6. Date of Birth
            // ✅ Valid: "1996-03-15", "2000-12-31", "1985-01-01"
            // ❌ Invalid: "2030-01-01" (future), "15-03-1996" (wrong format), "1900-01-01"
            // (too old)
            // (too old)
            LocalDate dateOfBirth = InputUtils.getValidDateOfBirth(scanner);
            System.out.println("   Age (calculated): " + InputUtils.calculateAge(dateOfBirth) + " years");

            System.out.println("\n🎯 STEP 3: Fitness Profile");
            System.out.println("─".repeat(70));

            // 7. Gender
            // ✅ Valid: 1 (MALE), 2 (FEMALE)
            // ❌ Invalid: 0, 3, "male" (must be number)
            // ❌ Invalid: 0, 3, "male" (must be number)
            Gender gender = InputUtils.selectEnum("Select Gender:", Gender.values(), scanner);

            // 8. Goal
            // ✅ Valid: 1-10 (any number from the displayed list)
            // ❌ Invalid: 0, 11, "lose weight" (must be number)
            // ❌ Invalid: 0, 11, "lose weight" (must be number)
            GoalType goal = InputUtils.selectEnum("Select Fitness Goal:", GoalType.values(), scanner);

            // 9. Fitness Level
            // ✅ Valid: 1 (BEGINNER), 2 (INTERMEDIATE), 3 (ADVANCED)
            // ❌ Invalid: 0, 4, "beginner" (must be number)
            // ❌ Invalid: 0, 4, "beginner" (must be number)
            FitnessLevel fitnessLevel = InputUtils.selectEnum("Select Fitness Level:", FitnessLevel.values(), scanner);

            // Create and return profile
            return new Profile(username, email, password, height, weight, goal, gender, dateOfBirth, fitnessLevel);

        } catch (Exception e) {
            System.err.println("\n❌ Sign-up failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Orchestrates the full workout plan generation flow.
     * Collects user inputs and uses the generator to build a plan.
     *
     * @param userProfile The user's profile.
     */
    private static void generateWorkoutPlanFlow(Profile userProfile) {
        while (true) {
            System.out.println("\n╔" + "═".repeat(70) + "╗");
            System.out.println("║" + InputUtils.centerText("WORKOUT PLAN MANAGER", 70) + "║");
            System.out.println("╚" + "═".repeat(70) + "╝\n");

            System.out.println("1. Generate New Workout Plan");
            System.out.println("2. View Current Workout Plan");
            System.out.println("3. Back to Home Page");
            System.out.print("\nEnter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println("\nLet's build your personalized plan, " + userProfile.getUsername() + "!\n");

                // 1. Training Days (2-6)
                int days = InputUtils.selectTrainingDays(scanner);

                // 2. Training Split
                TrainingSplit split = InputUtils.selectTrainingSplit(days, scanner);

                // 3. Weak Points (Multi-selection allowed)
                Set<MuscleGroup> weakPoints = InputUtils.selectWeakPoints(scanner);

                // 4. Equipment (Multi-selection allowed)
                Map<MuscleGroup, List<PreferredEquipment>> equipmentMap = InputUtils.selectEquipment(scanner);

                // 5. Generate Plan
                System.out.println("\n⚙️  Generating your plan based on science-based principles...");

                try {
                    // Build Context
                    PlanContext context = PlanContext.builder()
                            .profile(userProfile)
                            .split(split)
                            .trainingDays(days)
                            .weakPoints(weakPoints)
                            .equipmentPreferences(equipmentMap)
                            .build();

                    // Generate
                    WorkoutService workoutService = new WorkoutService();
                    WorkoutPlanGenerator generator = new WorkoutPlanGenerator(context, workoutService);
                    WorkoutPlan plan = generator.generate();

                    // Save to Profile (automatically overwrites existing plan if regenerating)
                    userProfile.setCurrentWorkoutPlan(plan);
                    userProfile.setLastWorkoutContext(context);

                    // Output
                    System.out.println("\n✅ Plan Generated Successfully!");
                    System.out.println("💾 Plan saved to your profile!");

                    // Save to Database (Workout Plan)
                    String planId = workoutRepository.saveWorkoutPlan(plan, userProfile);
                    if (planId != null) {
                        System.out.println("💾 Plan saved to Database (ID: " + planId + ")");
                    } else {
                        System.out.println("⚠️  Could not save plan to Database.");
                    }

                    // Save to Database (Context/Settings)
                    String contextId = planContextRepository.saveContext(context);
                    if (contextId != null) {
                        System.out.println("💾 Plan Settings (Split/Days/WeakPoints) saved to Database");
                    }

                    plan.displayWorkout();

                } catch (Exception e) {
                    System.err.println("\n❌ Error generating plan: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (choice.equals("2")) {
                WorkoutPlan currentPlan = userProfile.getCurrentWorkoutPlan();
                if (currentPlan != null) {
                    currentPlan.displayWorkout();
                } else {
                    System.out.println("\n⚠️  You haven't generated a workout plan yet.");
                }
            } else if (choice.equals("3")) {
                break; // Back to Home Page
            } else {
                System.out.println("\n❌ Invalid choice. Please enter 1-3.\n");
            }
        }
    }

    /**
     * Orchestrates the full nutritional plan generation flow.
     * Guides user through goal changes, health restrictions, and plan generation.
     * 
     * @param profile The user's profile
     */
    private static void generateNutritionalPlanFlow(Profile profile) {
        if (profile == null) {
            System.out.println("❌ Error: Profile cannot be null");
            return;
        }

        while (true) {
            System.out.println("\n╔" + "═".repeat(70) + "╗");
            System.out.println("║" + InputUtils.centerText("NUTRITIONAL PLAN MANAGER", 70) + "║");
            System.out.println("╚" + "═".repeat(70) + "╝\n");

            System.out.println("1. Generate New Nutritional Plan");
            System.out.println("2. View Current Nutritional Plan");
            System.out.println("3. Back to Home Page");
            System.out.print("\nEnter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                // ========== STEP 1 & 2 & 3: Goal Change Logic ==========
                System.out.println("\n🎯 NUTRITIONAL PLAN SETUP");
                System.out.println("─".repeat(70));
                System.out.print("Do you want to change your fitness goal? (yes/no): ");
                String changeGoalResponse = scanner.nextLine().trim().toLowerCase();

                if (changeGoalResponse.equals("yes") || changeGoalResponse.equals("y")) {
                    // Display goal options and get valid choice
                    GoalType newGoal = InputUtils.selectEnum("Select your new goal:", GoalType.values(), scanner);

                    // Update goal
                    profile.setGoal(newGoal);

                    // SAVE TO DATABASE immediately so it's not lost if app crashes
                    if (userRepository.updateProfile(profile)) {
                        System.out.println("💾 Profile goal updated in Database");
                    } else {
                        System.out.println("⚠️ Failed to update goal in Database");
                    }

                    System.out.println("✓ Goal updated to: " + newGoal.getDescription());

                } else {
                    System.out.println("✓ Keeping your current goal: " + profile.getGoal().getDescription());
                }

                // ========== STEP 4: Generate Nutritional Plan ==========
                System.out.println("\n⚙️  Generating your personalized nutritional plan...");

                model.nutrition.domain.IDietPlan generatedPlan = model.nutrition.logic.NutritionalPlanGenerator
                        .generatePlan(profile);

                if (generatedPlan == null) {
                    System.out.println("❌ Error: Could not generate nutritional plan");
                    continue;
                }

                System.out.println("✓ Nutritional plan generated successfully!");

                // ========== STEP 5: Initialize AdaptiveDietManager ==========
                profile.initializeDietManager();
                System.out.println("✓ Adaptive diet management system activated");

                // ========== STEP 6: Store Plan in Profile ==========
                profile.setCurrentDietPlan(generatedPlan);
                System.out.println("✓ Nutritional plan saved to your profile");

                // Save to Database
                saveNutritionPlanToDatabase(profile);

                // ========== STEP 7: Display Final Plan ==========
                System.out.println("\n" + "═".repeat(70));
                System.out.println("📊 YOUR PERSONALIZED NUTRITIONAL PLAN");
                System.out.println("═".repeat(70));

                System.out.println(generatedPlan.getPlanSummary());

                System.out.println("═".repeat(70));
                System.out.println("\n💡 TIP: Your plan will automatically adjust based on your weekly progress!");
                System.out.println("📈 Track your weight daily for best results.");
                System.out.println("═".repeat(70));

            } else if (choice.equals("2")) {
                model.nutrition.domain.IDietPlan currentPlan = profile.getCurrentDietPlan();
                if (currentPlan != null) {
                    System.out.println("\n" + "═".repeat(70));
                    System.out.println("📊 YOUR CURRENT NUTRITIONAL PLAN");
                    System.out.println("═".repeat(70));
                    System.out.println(currentPlan.getPlanSummary());
                    System.out.println("═".repeat(70));
                } else {
                    System.out.println("\n⚠️  You haven't generated a nutritional plan yet.");
                }
            } else if (choice.equals("3")) {
                break; // Back to Home Page
            } else {
                System.out.println("\n❌ Invalid choice. Please enter 1-3.\n");
            }
        }
    }

    /**
     * Orchestrates the body weight tracking flow.
     * Allows users to add daily weight and view progress history.
     * 
     * @param profile The user's profile
     */
    private static void trackYourBodyWeightFlow(Profile profile) {
        while (true) {
            System.out.println("\n╔" + "═".repeat(70) + "╗");
            System.out.println("║" + InputUtils.centerText("TRACK YOUR BODY WEIGHT", 70) + "║");
            System.out.println("╚" + "═".repeat(70) + "╝\n");

            System.out.println("1. Add Weight (Date: " + currentTrackingDate + ")");
            System.out.println("2. Show Progress History");
            System.out.println("3. Back to Home Page");
            System.out.print("\nEnter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println("\n📅 Date: " + currentTrackingDate);
                double weight = InputUtils.getValidDouble(scanner, "Enter weight (kg, 30-350): ", 30.0, 350.0);

                Double weeklyAvg = profile.addDailyWeight(weight);
                System.out.println("✅ Weight recorded: " + weight + " kg for " + currentTrackingDate);

                // Save Weekly Average if a week was just completed
                if (weeklyAvg != null) {
                    System.out.printf("📊 Weekly Goal Reached! Average: %.2f kg%n", weeklyAvg);
                    System.out.println("💾 Weekly average saved to Database (Auto-handled)");
                }

                System.out.println("💾 Weight logged to Database (Auto-handled)");
                // Increment date for next entry
                currentTrackingDate = currentTrackingDate.plusDays(1);

                // Update current weight in Profile table too
                if (userRepository.updateProfile(profile)) {
                    System.out.println("💾 Profile weight updated in Database");
                } else {
                    System.out.println("⚠️ Could not update profile weight in Database");
                }

                // Trigger adaptive diet manager
                if (profile.updateNutritionalPlan()) {
                    saveNutritionPlanToDatabase(profile);
                    userRepository.updateProfile(profile); // Persist last_adjusted_week update
                }

            } else if (choice.equals("2")) {
                // Show Progress History
                profile.printWeeklyAverages();

            } else if (choice.equals("3")) {
                break;
            } else {
                System.out.println("\n❌ Invalid choice. Please enter 1-3.\n");
            }
        }
    }

    /**
     * Handles changing the fitness goal inside the profile page.
     * Automatically regenerates the nutritional plan.
     */
    private static void changeGoalInsideProfile(Profile profile) {
        System.out.println("\n🎯 CHANGE GOAL");
        System.out.println("─".repeat(70));
        GoalType newGoal = InputUtils.selectEnum("Select New Fitness Goal:", GoalType.values(), scanner);

        if (newGoal == profile.getGoal()) {
            System.out.println("⚠️  Goal is already set to " + newGoal.getDescription());
            return;
        }

        profile.setGoal(newGoal);
        System.out.println("✅ Goal updated to: " + newGoal.getDescription());

        if (userRepository.updateProfile(profile)) {
            System.out.println("💾 Profile goal updated in Database");
        } else {
            System.out.println("⚠️ Failed to update goal in Database");
        }

        // Auto-regenerate nutritional plan
        System.out.println("🔄 Auto-regenerating nutritional plan...");
        try {
            model.nutrition.domain.IDietPlan newPlan = model.nutrition.logic.NutritionalPlanGenerator
                    .generatePlan(profile);
            profile.setCurrentDietPlan(newPlan);
            profile.initializeDietManager(); // Reset diet manager with new plan
            System.out.println("✅ Nutritional plan updated automatically!");

            // SAVE TO DATABASE
            saveNutritionPlanToDatabase(profile);

        } catch (Exception e) {
            System.err.println("❌ Error updating nutritional plan: " + e.getMessage());
        }
    }

    /**
     * Handles changing the fitness level inside the profile page.
     * Automatically regenerates the workout plan if a previous plan exists.
     */
    private static void changeFitnessLevelInsideProfile(Profile profile) {
        System.out.println("\n💪 CHANGE FITNESS LEVEL");
        System.out.println("─".repeat(70));
        FitnessLevel newLevel = InputUtils.selectEnum("Select New Fitness Level:", FitnessLevel.values(), scanner);

        if (newLevel == profile.getFitnessLevel()) {
            System.out.println("⚠️  Fitness Level is already set to " + newLevel);
            return;
        }

        // Ensure DietManager is active if a plan exists, so it catches the fitness
        // level change
        if (profile.getCurrentDietPlan() != null) {
            profile.initializeDietManager();
        }

        double oldCalories = profile.getCurrentDietPlan() != null
                ? profile.getCurrentDietPlan().getTargetDailyCalories()
                : 0;

        profile.setFitnessLevel(newLevel);
        System.out.println("✅ Fitness Level updated to: " + newLevel);

        if (profile.getCurrentDietPlan() != null) {
            double newCalories = profile.getCurrentDietPlan().getTargetDailyCalories();
            if (Math.abs(newCalories - oldCalories) > 1) {
                System.out.println(
                        "🔄 Calories adjusted: " + (int) oldCalories + " -> " + (int) newCalories + " kcal/day");
            }
        }

        if (userRepository.updateProfile(profile)) {
            System.out.println("💾 Profile fitness level updated in Database");
        } else {
            System.out.println("⚠️ Failed to update fitness level in Database");
        }

        // Auto-regenerate workout plan if context exists
        PlanContext lastContext = profile.getLastWorkoutContext();
        if (lastContext != null) {
            System.out.println("🔄 Auto-regenerating workout plan...");
            try {
                // Create new context with updated profile (which has new fitness level)
                // We reuse the old split, days, etc.
                PlanContext newContext = PlanContext.builder()
                        .profile(profile)
                        .split(lastContext.getSplit())
                        .trainingDays(lastContext.getTrainingDays())
                        .weakPoints(lastContext.getWeakPoints())
                        .equipmentPreferences(lastContext.getEquipmentPreferences())
                        .build();

                WorkoutService workoutService = new WorkoutService();
                WorkoutPlanGenerator generator = new WorkoutPlanGenerator(newContext, workoutService);
                WorkoutPlan newPlan = generator.generate();

                profile.setCurrentWorkoutPlan(newPlan);
                profile.setLastWorkoutContext(newContext); // Update context
                System.out.println("✅ Workout plan adapted automatically!");

                // SAVE TO DATABASE
                String planId = workoutRepository.saveWorkoutPlan(newPlan, profile);
                if (planId != null) {
                    System.out.println("💾 New workout plan saved to Database (ID: " + planId + ")");
                } else {
                    System.out.println("⚠️ Failed to save new workout plan to Database");
                }

                String contextId = planContextRepository.saveContext(newContext);
                if (contextId != null) {
                    System.out.println("💾 New plan settings saved to Database");
                }

            } catch (Exception e) {
                System.err.println("❌ Error updating workout plan: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️  No existing workout plan found. Generate one from the Home Page to see changes.");
        }

        // Save updated nutrition plan if it exists (fitness level affects TDEE)
        if (profile.getCurrentDietPlan() != null) {
            System.out.println("🔄 Updating nutritional plan for new fitness level...");
            // Note: DietManager automatically updates internal state on setFitnessLevel,
            // but we save to DB here
            saveNutritionPlanToDatabase(profile);
        }
    }

    /**
     * Helper to save the current nutrition plan to the database.
     */
    private static void saveNutritionPlanToDatabase(Profile profile) {
        IDietPlan currentPlan = profile.getCurrentDietPlan();
        if (currentPlan == null)
            return;

        if (nutritionRepository.savePlan(currentPlan)) {
            System.out.println("💾 Nutritional plan saved to Database");
        } else {
            System.out.println("⚠️ Failed to save nutritional plan to Database");
        }
    }
}
