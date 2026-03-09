package view.workout;

import app.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import view.shared.SceneNavigator;

import model.user.domain.Profile;
import model.user.enums.FitnessLevel;
import model.workout.domain.PlanContext;
import model.workout.domain.WorkoutPlan;
import model.workout.service.WorkoutPlanGenerator;
import model.workout.service.WorkoutService;
import model.workout.enums.TrainingSplit;

import java.net.URL;
import java.util.ResourceBundle;

public class GenerateWorkoutController implements Initializable {

    @FXML
    private Label messageLabel;

    private Profile currentProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile from UserSession
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            messageLabel.setText("❌ ERROR: No user logged in!");
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Clear message label
        messageLabel.setText("");
    }

    /**
     * Generate a new workout plan
     */
    @FXML
    public void generateNewPlan(ActionEvent event) {
        if (currentProfile == null) {
            messageLabel.setText("❌ Error: No user profile available!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Navigate to the strict configuration scene
            Parent root = FXMLLoader.load(getClass().getResource("/view/workout/SceneWorkoutConfig.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
        } catch (Exception e) {
            messageLabel.setText("❌ Error loading configuration screen: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
        }
    }

    /**
     * View the current workout plan
     */
    @FXML
    public void viewCurrentPlan(ActionEvent event) {
        if (currentProfile == null) {
            messageLabel.setText("❌ Error: No user profile available!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        WorkoutPlan plan = currentProfile.getCurrentWorkoutPlan();

        if (plan == null) {
            messageLabel.setText("⚠️ No workout plan found! Click 'Generate New Plan' to create one.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Navigate to workout display screen
            Parent root = FXMLLoader.load(getClass().getResource("/view/workout/SceneWorkoutDisplay.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
        } catch (Exception e) {
            messageLabel.setText("❌ Error loading workout display screen: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
        }
    }

    /**
     * Determine the best training split based on fitness level
     */
    private TrainingSplit determineTrainingSplit() {
        FitnessLevel level = currentProfile.getFitnessLevel();

        if (level == null) {
            return TrainingSplit.FULL_BODY; // Default
        }

        switch (level) {
            case BEGINNER:
                return TrainingSplit.FULL_BODY; // Full body for beginners
            case INTERMEDIATE:
            case ADVANCED:
            case PROFESSIONAL:
                return TrainingSplit.UPPER_LOWER; // Upper/Lower split for intermediate+
            default:
                return TrainingSplit.FULL_BODY;
        }
    }

    /**
     * Determine training days per week based on fitness level
     */
    private int determineTrainingDays() {
        FitnessLevel level = currentProfile.getFitnessLevel();

        if (level == null) {
            return 3; // Default
        }

        switch (level) {
            case BEGINNER:
                return 3; // 3 days/week for beginners
            case INTERMEDIATE:
                return 4; // 4 days/week for intermediate
            case ADVANCED:
                return 5; // 5 days/week for advanced
            case PROFESSIONAL:
                return 6; // 6 days/week for professionals
            default:
                return 3;
        }
    }


    /**
     * Navigate back to Scene4 (main dashboard)
     */
    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/home/Scene4.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
