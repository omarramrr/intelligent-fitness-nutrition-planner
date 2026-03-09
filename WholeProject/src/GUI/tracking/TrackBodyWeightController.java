package view.tracking;

import app.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import model.user.domain.Profile;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackBodyWeightController implements Initializable {

    @FXML
    private TextField weightInput;

    @FXML
    private Label successLabel;

    private Profile currentProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile from UserSession
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            if (successLabel != null) {
                successLabel.setText("ERROR: No user logged in! Please login first.");
                successLabel.setStyle("-fx-text-fill: #ff6b6b;");
            }
            return;
        }

        // Clear success label initially
        if (successLabel != null) {
            successLabel.setText("");
        }

        // Restrict weight input to numbers only (max 3 digits for kg)
        weightInput.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow empty or numbers with optional decimal point
            if (newText.isEmpty() || newText.matches("\\d{0,3}(\\.\\d{0,2})?")) {
                return change;
            }
            return null; // Reject the change
        }));
    }

    /**
     * Add today's weight to the tracking system
     */
    @FXML
    public void addTodaysWeight(ActionEvent event) {
        if (currentProfile == null) {
            successLabel.setText("ERROR: No user profile available!");
            successLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        String weight = weightInput.getText();

        if (weight.isEmpty()) {
            successLabel.setText("⚠️ Please enter your weight!");
            successLabel.setStyle("-fx-text-fill: #ffaa00;");
            return;
        }

        try {
            double weightValue = Double.parseDouble(weight);

            if (weightValue < 30.0 || weightValue > 350.0) {
                successLabel.setText("⚠️ Weight must be between 30 and 350 kg!");
                successLabel.setStyle("-fx-text-fill: #ffaa00;");
                return;
            }

            // 1. Add weight to the user's profile
            currentProfile.addDailyWeight(weightValue);

            // 2. Persist Weight Change
            app.Main.getUserRepository().updateProfile(currentProfile);

            String feedback = "";

            // 3. Adaptive Update Check (Mirroring Main logic)
            if (currentProfile.updateNutritionalPlan()) {
                // If plan changed, save it
                model.nutrition.domain.IDietPlan newPlan = currentProfile.getCurrentDietPlan();
                if (newPlan != null) {
                    app.Main.getNutritionRepository().savePlan(newPlan);
                }
                // Save profile again (last_adjusted_week updated)
                app.Main.getUserRepository().updateProfile(currentProfile);

                feedback += " ✨ Adaptive Diet Update Applied!";
                System.out.println("✅ Adaptive diet update saved to database.");
            }

            // Check if a full week has been completed
            if (currentProfile.getWeightTracker().hasFullWeek()) {
                successLabel.setText("🎉 Week complete! Weight saved." + feedback);
                successLabel.setStyle("-fx-text-fill: #5fcf80;");
            } else {
                int daysRecorded = currentProfile.getWeightTracker().getDailyWeights().size();
                int daysRemaining = 7 - daysRecorded;
                successLabel.setText("✅ Weight saved! " + daysRecorded + "/7 recorded." + feedback);
                successLabel.setStyle("-fx-text-fill: #5fcf80;");
            }

            // Clear the input field after successful addition
            weightInput.clear();

        } catch (NumberFormatException e) {
            successLabel.setText("❌ Invalid weight format!");
            successLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    /**
     * Show progress/history of weight tracking - Navigate to progress screen
     */
    @FXML
    public void showProgress(ActionEvent event) {
        if (currentProfile == null) {
            successLabel.setText("ERROR: No user profile available!");
            successLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        try {
            // Navigate to weight progress display screen
            Parent root = FXMLLoader.load(getClass().getResource("SceneWeightProgress.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            successLabel.setText("❌ Error loading progress screen!");
            successLabel.setStyle("-fx-text-fill: #ff6b6b;");
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
