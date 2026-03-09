
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
import model.user.enums.FitnessLevel;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeFitnessLevelController implements Initializable {

    @FXML
    private Label currentLevelLabel;

    @FXML
    private ChoiceBox<String> fitnessChoiceBox;

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

        // Display current fitness level
        currentLevelLabel.setText(currentProfile.getFitnessLevel().getDescription());

        // Populate ChoiceBox with fitness level options
        fitnessChoiceBox.setItems(FXCollections.observableArrayList(
                FitnessLevel.BEGINNER.getDescription(),
                FitnessLevel.INTERMEDIATE.getDescription(),
                FitnessLevel.ADVANCED.getDescription(),
                FitnessLevel.PROFESSIONAL.getDescription()));

        // Pre-select current fitness level
        fitnessChoiceBox.setValue(currentProfile.getFitnessLevel().getDescription());

        // Clear message label
        messageLabel.setText("");
    }

    /**
     * Save the selected fitness level and regenerate workout plan
     */
    @FXML
    public void saveFitnessLevel(ActionEvent event) {
        if (currentProfile == null) {
            messageLabel.setText("❌ Error: No profile available!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        String selectedLevel = fitnessChoiceBox.getValue();

        if (selectedLevel == null) {
            messageLabel.setText("⚠️ Please select a fitness level!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Convert selected string to FitnessLevel enum
            FitnessLevel newLevel = null;
            for (FitnessLevel level : FitnessLevel.values()) {
                if (level.getDescription().equals(selectedLevel)) {
                    newLevel = level;
                    break;
                }
            }

            if (newLevel == null) {
                messageLabel.setText("❌ Invalid fitness level selection!");
                messageLabel.getStyleClass().clear();
                messageLabel.getStyleClass().add("error-message");
                return;
            }

            // Check if fitness level actually changed
            if (newLevel == currentProfile.getFitnessLevel()) {
                messageLabel.setText("ℹ️ Fitness level unchanged. No update needed.");
                messageLabel.getStyleClass().clear();
                messageLabel.getStyleClass().add("success-message");
                return;
            }

            // Update profile fitness level
            currentProfile.setFitnessLevel(newLevel);

            // Persist Profile Change
            app.Main.getUserRepository().updateProfile(currentProfile);

            String additionalMsg = "";

            // Update nutrition plan if it exists (TDEE changed)
            if (currentProfile.getCurrentDietPlan() != null) {
                model.nutrition.domain.IDietPlan newPlan = model.nutrition.logic.NutritionalPlanGenerator.generatePlan(currentProfile);
                currentProfile.setCurrentDietPlan(newPlan);
                currentProfile.initializeDietManager();
                
                // Persist new Nutrition Plan
                app.Main.getNutritionRepository().savePlan(newPlan);
                additionalMsg = " Nutrition plan adjusted.";
            }

            // Show success message
            messageLabel.setText("✅ Fitness level updated successfully!" + additionalMsg);
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
            messageLabel.setText("❌ Error updating fitness level: " + e.getMessage());
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
