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
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import model.user.domain.Profile;
import model.nutrition.domain.IDietPlan;

import java.net.URL;
import java.util.ResourceBundle;

public class NutritionDisplayController implements Initializable {

    @FXML
    private Label goalLabel;

    @FXML
    private Label caloriesLabel;

    @FXML
    private Label proteinLabel;

    @FXML
    private Label carbsLabel;

    @FXML
    private Label fatsLabel;

    @FXML
    private ProgressBar proteinBar;

    @FXML
    private ProgressBar carbsBar;

    @FXML
    private ProgressBar fatsBar;

    @FXML
    private Label tipLabel;

    private Profile currentProfile;
    private IDietPlan dietPlan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile from UserSession
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            goalLabel.setText("ERROR: No user profile available!");
            return;
        }

        // Get diet plan from profile
        dietPlan = currentProfile.getCurrentDietPlan();

        if (dietPlan == null) {
            goalLabel.setText("ERROR: No nutrition plan found!");
            caloriesLabel.setText("N/A");
            tipLabel.setText("Please generate a nutrition plan first.");
            return;
        }

        // Display the nutrition plan
        displayPlan();
    }

    /**
     * Display the nutrition plan data in the UI
     */
    private void displayPlan() {
        // Set goal
        goalLabel.setText(currentProfile.getGoal().getDescription());

        // Set calories (large, prominent)
        int calories = (int) dietPlan.getTargetDailyCalories();
        caloriesLabel.setText(calories + " kcal");

        // Get macro values
        double protein = dietPlan.getTargetProteinGrams();
        double carbs = dietPlan.getTargetCarbsGrams();
        double fats = dietPlan.getTargetFatsGrams();

        // Calculate percentages for display
        double totalCalories = dietPlan.getTargetDailyCalories();
        double proteinPercent = (protein * 4 / totalCalories) * 100;
        double carbsPercent = (carbs * 4 / totalCalories) * 100;
        double fatsPercent = (fats * 9 / totalCalories) * 100;

        // Set macro labels with grams and percentages
        proteinLabel.setText(String.format("%.1f g (%.0f%%)", protein, proteinPercent));
        carbsLabel.setText(String.format("%.1f g (%.0f%%)", carbs, carbsPercent));
        fatsLabel.setText(String.format("%.1f g (%.0f%%)", fats, fatsPercent));

        // Set progress bars (normalized to 0-1 range)
        // Use percentage of total calories as the progress value
        proteinBar.setProgress(proteinPercent / 100.0);
        carbsBar.setProgress(carbsPercent / 100.0);
        fatsBar.setProgress(fatsPercent / 100.0);

        // Set tip
        tipLabel.setText(
                "💡 Tip: Track your weight daily to enable adaptive calorie adjustments based on your progress!");
    }

    /**
     * Navigate back to Generate Nutrition screen
     */
    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/nutrition/SceneGenerateNutration.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
