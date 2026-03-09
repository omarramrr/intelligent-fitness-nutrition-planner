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
import model.nutrition.domain.IDietPlan;

import java.net.URL;
import java.util.ResourceBundle;

public class MealPlanDisplayController implements Initializable {

    @FXML
    private Label dailyTargetLabel;

    @FXML
    private Label breakfastCalories;

    @FXML
    private Label breakfastMacros;

    @FXML
    private Label lunchCalories;

    @FXML
    private Label lunchMacros;

    @FXML
    private Label dinnerCalories;

    @FXML
    private Label dinnerMacros;

    @FXML
    private Label snacksCalories;

    @FXML
    private Label snacksMacros;

    private Profile currentProfile;
    private IDietPlan dietPlan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile from UserSession
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            dailyTargetLabel.setText("ERROR: No user profile available!");
            return;
        }

        // Get diet plan from profile
        dietPlan = currentProfile.getCurrentDietPlan();

        if (dietPlan == null) {
            dailyTargetLabel.setText("ERROR: No nutrition plan found!");
            return;
        }

        // Display the meal plan
        displayMealPlan();
    }

    /**
     * Display the 1-day meal plan breakdown
     */
    private void displayMealPlan() {
        double dailyCalories = dietPlan.getTargetDailyCalories();
        double protein = dietPlan.getTargetProteinGrams();
        double carbs = dietPlan.getTargetCarbsGrams();
        double fats = dietPlan.getTargetFatsGrams();

        // Set daily target
        dailyTargetLabel.setText("📊 Daily Target: " + (int) dailyCalories + " calories");

        // Breakfast (25%)
        setMealData(breakfastCalories, breakfastMacros, dailyCalories, protein, carbs, fats, 0.25);

        // Lunch (35%)
        setMealData(lunchCalories, lunchMacros, dailyCalories, protein, carbs, fats, 0.35);

        // Dinner (30%)
        setMealData(dinnerCalories, dinnerMacros, dailyCalories, protein, carbs, fats, 0.30);

        // Snacks (10%)
        setMealData(snacksCalories, snacksMacros, dailyCalories, protein, carbs, fats, 0.10);
    }

    /**
     * Helper method to set meal data for a specific meal
     */
    private void setMealData(Label caloriesLabel, Label macrosLabel,
            double dailyCalories, double protein, double carbs, double fats,
            double percentage) {
        int mealCalories = (int) (dailyCalories * percentage);
        double mealProtein = protein * percentage;
        double mealCarbs = carbs * percentage;
        double mealFats = fats * percentage;

        caloriesLabel.setText("Calories: " + mealCalories + " kcal");
        macrosLabel.setText(String.format("Protein: %.0fg | Carbs: %.0fg | Fats: %.0fg",
                mealProtein, mealCarbs, mealFats));
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
