package view.nutrition;

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

import model.user.domain.Profile;
import model.nutrition.logic.NutritionalPlanGenerator;
import model.nutrition.domain.IDietPlan;

import java.net.URL;
import java.util.ResourceBundle;

public class GenerateNutrationController implements Initializable {

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
     * Generate a new nutrition plan
     */
    /**
     * Generate a new nutrition plan
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
            // 1. Generate Plan (Directly using current Goal)
            IDietPlan plan = NutritionalPlanGenerator.generatePlan(currentProfile);
            if (plan == null) {
                throw new Exception("Plan generation returned null.");
            }

            // 2. Save to Profile
            currentProfile.setCurrentDietPlan(plan);
            currentProfile.initializeDietManager();

            // 3. Persist Plan to Database
            app.Main.getNutritionRepository().savePlan(plan);

            // 4. Persist Profile Changes
            app.Main.getUserRepository().updateProfile(currentProfile);

            // Show success message with nutrition targets
            String successMsg = String.format(
                    "✅ Plan Generated & Saved!\n" +
                            "📊 Daily Targets: %d kcal | Protein: %.1fg | Carbs: %.1fg | Fats: %.1fg",
                    (int) plan.getTargetDailyCalories(),
                    plan.getTargetProteinGrams(),
                    plan.getTargetCarbsGrams(),
                    plan.getTargetFatsGrams());
            messageLabel.setText(successMsg);
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("success-message");

        } catch (Exception e) {
            messageLabel.setText("❌ Error generating nutrition plan: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
        }
    }

    /**
     * View the current nutrition plan
     */
    @FXML
    public void viewCurrentPlan(ActionEvent event) {
        if (currentProfile == null) {
            messageLabel.setText("❌ Error: No user profile available!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        IDietPlan plan = currentProfile.getCurrentDietPlan();

        if (plan == null) {
            messageLabel.setText("⚠️ No nutrition plan found! Click 'Generate New Plan' to create one.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Navigate to nutrition display screen
            Parent root = FXMLLoader.load(getClass().getResource("/view/nutrition/SceneNutritionDisplay.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("❌ Error loading nutrition display screen: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
        }
    }

    /**
     * Generate a 1 day meal plan
     */
    @FXML
    public void oneDayMealPlan(ActionEvent event) {
        if (currentProfile == null) {
            messageLabel.setText("❌ Error: No user profile available!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        IDietPlan plan = currentProfile.getCurrentDietPlan();

        if (plan == null) {
            messageLabel.setText("⚠️ No nutrition plan found! Generate a nutrition plan first.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Navigate to meal plan display screen
            Parent root = FXMLLoader.load(getClass().getResource("/view/nutrition/SceneMealPlanDisplay.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("❌ Error loading meal plan display screen: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
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
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
