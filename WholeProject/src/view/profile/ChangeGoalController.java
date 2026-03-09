package view.profile;

import app.UserSession;
import javafx.collections.FXCollections;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import model.user.domain.Profile;
import model.user.enums.GoalType;
import model.nutrition.logic.NutritionalPlanGenerator;
import model.nutrition.domain.IDietPlan;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeGoalController implements Initializable {

    @FXML
    private Label currentGoalLabel;

    @FXML
    private ChoiceBox<String> goalChoiceBox;

    @FXML
    private Label messageLabel;

    private Profile currentProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            messageLabel.setText("ERROR: No user profile available!");
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Display current goal
        currentGoalLabel.setText(currentProfile.getGoal().getDescription());

        // Populate ChoiceBox with goal options
        goalChoiceBox.setItems(FXCollections.observableArrayList(
                GoalType.LOSE_WEIGHT.getDescription(),
                GoalType.GAIN_WEIGHT.getDescription(),
                GoalType.MAINTENANCE.getDescription()));

        // Pre-select current goal
        goalChoiceBox.setValue(currentProfile.getGoal().getDescription());

        // Clear message label
        messageLabel.setText("");
    }

    /**
     * Save the selected goal and regenerate nutrition plan
     */
    @FXML
    public void saveGoal(ActionEvent event) {
        if (currentProfile == null) {
            messageLabel.setText("❌ Error: No profile available!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        String selectedGoal = goalChoiceBox.getValue();

        if (selectedGoal == null) {
            messageLabel.setText("⚠️ Please select a goal!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Convert selected string to GoalType enum
            GoalType newGoal = null;
            for (GoalType goal : GoalType.values()) {
                if (goal.getDescription().equals(selectedGoal)) {
                    newGoal = goal;
                    break;
                }
            }

            if (newGoal == null) {
                messageLabel.setText("❌ Invalid goal selection!");
                messageLabel.getStyleClass().clear();
                messageLabel.getStyleClass().add("error-message");
                return;
            }

            // Check if goal actually changed
            if (newGoal == currentProfile.getGoal()) {
                messageLabel.setText("ℹ️ Goal unchanged. No update needed.");
                messageLabel.getStyleClass().clear();
                messageLabel.getStyleClass().add("success-message");
                return;
            }

            // Update profile goal
            currentProfile.setGoal(newGoal);

            // Regenerate nutrition plan based on new goal
            IDietPlan newPlan = NutritionalPlanGenerator.generatePlan(currentProfile);
            currentProfile.setCurrentDietPlan(newPlan);
            currentProfile.initializeDietManager();

            // Persist changes to Database
            app.Main.getNutritionRepository().savePlan(newPlan);
            app.Main.getUserRepository().updateProfile(currentProfile);

            // Show success message
            messageLabel.setText("✅ Goal updated successfully! Nutrition plan regenerated.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("success-message");

            // Navigate back to profile after 1.5 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> goBack(event));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            messageLabel.setText("❌ Error updating goal: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
        }
    }

    /**
     * Cancel and return to profile
     */
    @FXML
    public void cancel(ActionEvent event) {
        goBack(event);
    }

    /**
     * Navigate back to profile page
     */
    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/profile/SceneProfile.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
