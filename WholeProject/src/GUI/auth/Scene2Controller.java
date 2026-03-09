package view.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import view.shared.SceneNavigator;

import app.UserManager;
import app.UserSession;
import model.user.domain.User;
import model.user.domain.Profile;

public class Scene2Controller {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private Button togglePasswordBtn;
    @FXML
    private Label errorLabel;

    // Toggle password visibility
    @FXML
    private void togglePasswordVisibility() {
        if (visiblePasswordField.isVisible()) {
            // Hide text, show password field
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);

            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            togglePasswordBtn.setText("👁");
        } else {
            // Show text field
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            togglePasswordBtn.setText("👁‍🗨");
        }
    }

    // Back button
    @FXML
    private void onBackClicked(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            SceneNavigator.goBack(stage);
        } catch (Exception e) {
            // Fallback
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Scene1.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Sign in logic - Authenticate and navigate to Scene4
    @FXML
    private void onSignInClicked(ActionEvent event) {
        String usernameOrEmail = usernameField != null ? usernameField.getText().trim() : "";
        String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();

        // Clear previous error
        if (errorLabel != null) {
            errorLabel.setText("");
        }

        // Validate input
        if (usernameOrEmail.isEmpty()) {
            showError("Please enter email");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            return;
        }

        // Authenticate logic mirroring Main.signIn

        // 1. Get User/Profile from DB
        Profile userProfile = app.Main.getUserRepository().getUserByEmail(usernameOrEmail);

        if (userProfile == null) {
            showError("Email not found.");
            return;
        }

        // 2. Check Password
        String storedPassword = userProfile.getUser().getPassword();
        if (!password.equals(storedPassword)) {
            showError("Incorrect password.");
            return;
        }

        System.out.println("✅ Login Successful for: " + userProfile.getUsername());

        // 3. Post-Login Initialization (Mirroring Main.signIn)
        try {
            // A. Load Nutrition Plan
            model.nutrition.domain.IDietPlan dbPlan = app.Main.getNutritionRepository()
                    .getLatestPlanByProfileId(userProfile.getProfileId());
            if (dbPlan != null && dbPlan.isActive()) {
                userProfile.setCurrentDietPlan(dbPlan);
                userProfile.initializeDietManager(); // internal init
                System.out.println("✅ Nutrition plan loaded.");
            }

            // B. Load Workout Plan
            // Main.signIn instantiates a new repo for this, so we do too.
            model.workout.repository.WorkoutRepository workoutRepo = new model.workout.repository.WorkoutRepository();
            model.workout.domain.WorkoutPlan savedPlan = workoutRepo.getWorkoutPlan(userProfile);
            if (savedPlan != null) {
                userProfile.setCurrentWorkoutPlan(savedPlan);
                System.out.println("✅ Workout plan loaded.");
            }

            // C. Initialize Diet Manager & Adaptive Updates
            userProfile.initializeDietManager(); // Re-ensure manager is active
            if (userProfile.updateNutritionalPlan()) {
                // Persist updates
                if (userProfile.getCurrentDietPlan() != null) {
                    app.Main.getNutritionRepository().savePlan(userProfile.getCurrentDietPlan());
                }
                app.Main.getUserRepository().updateProfile(userProfile); // Persist last_adjusted_week
                System.out.println("✅ Adaptive nutrition update applied and saved.");
            }

            // 4. Set Session in Main (this also auto-syncs UserSession due to previous
            // changes)
            app.Main.setCurrentProfile(userProfile);

            // 5. Navigate to Home
            Parent root = FXMLLoader.load(getClass().getResource("/view/home/Scene4.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error during login initialization: " + e.getMessage());
        }
    }

    /**
     * Display error message to user
     */
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        }
        System.err.println("LOGIN ERROR: " + message);
    }
}
