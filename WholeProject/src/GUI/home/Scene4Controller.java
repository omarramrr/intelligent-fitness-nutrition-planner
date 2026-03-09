package view.home;

import java.net.URL;
import java.util.ResourceBundle;

import app.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.user.domain.User;
import model.user.domain.Profile;
import view.shared.SceneNavigator;

public class Scene4Controller implements Initializable {

    @FXML
    private VBox contentArea;

    @FXML
    private Label contentLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            contentLabel.setText("Welcome, " + currentUser.getPassword() + "!"); // NOTE: Using getPassword()
                                                                                 // temporarily as username getter might
                                                                                 // be different or getUsername() logic
                                                                                 // needs check. Re-checking User model.
            // Wait, standard getUsername() check.
            contentLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        } else {
            contentLabel.setText("Welcome!");
        }
    }

    /**
     * Navigate to Profile Page section
     * Alias for showProfilePage
     */
    @FXML
    public void handleProfileClick(ActionEvent event) {
        showProfilePage(event);
    }

    /**
     * Navigate to Profile Page section
     */
    @FXML
    public void showProfilePage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/profile/SceneProfile.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
            System.out.println("Navigated to: Profile Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Track Body Weight - Main action button
     */
    @FXML
    public void trackBodyWeight(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/tracking/SceneTrackBodyWeight.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
            System.out.println("Navigated to: Track Body Weight");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate Workout - Main action button
     */
    @FXML
    public void generateWorkout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/workout/SceneGenerateWorkout.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
            System.out.println("Navigated to: Generate Workout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate Nutrition Plan - Main action button
     */
    @FXML
    public void generateNutritionPlan(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/nutrition/SceneGenerateNutration.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
            System.out.println("Navigated to: Generate Nutrition Plan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate back to Scene1
     */
    @FXML
    public void goToScene1(ActionEvent event) {
        try {
            // Explicit logout
            UserSession.getInstance().logout();
            System.out.println("LOGOUT: Session cleared.");

            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Scene1.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
