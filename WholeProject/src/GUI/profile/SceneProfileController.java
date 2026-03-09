package view.profile;

import app.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

import model.user.domain.User;
import model.user.domain.Profile;

public class SceneProfileController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField idField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField genderField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField fitnessLevelField;

    @FXML
    private TextField goalField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load user data from database (simulated with hardcoded values for now)
        // Load user data from database (simulated with hardcoded values for now)
        loadUserDataFromDatabase();

        // Fix focus issue - Request focus on the root pane
        Platform.runLater(() -> rootPane.requestFocus());
    }

    /**
     * Load user data from the current session
     * Uses the backend User and Profile objects
     * Made public so other controllers can refresh the profile page
     */
    public void loadUserDataFromDatabase() {
        UserSession session = UserSession.getInstance();

        if (!session.isLoggedIn()) {
            System.err.println("No user logged in!");
            usernameField.setText("Not logged in");
            idField.setText("Not logged in");
            emailField.setText("Not logged in");
            return;
        }

        User user = session.getCurrentUser();
        Profile profile = session.getCurrentProfile();

        // Load user basic info
        usernameField.setText(user.getUsername());
        idField.setText(profile != null ? profile.getProfileId() : "Not set");
        emailField.setText(user.getEmail());

        // Load profile data if available
        if (profile != null) {
            ageField.setText(profile.getAge() > 0 ? String.valueOf(profile.getAge()) : "Not set");
            genderField.setText(profile.getGender() != null ? profile.getGender().toString() : "Not set");
            weightField.setText(
                    profile.getWeightInKg() > 0 ? String.format("%.1f kg", profile.getWeightInKg()) : "Not set");
            heightField.setText(
                    profile.getHeightInCm() > 0 ? String.format("%.1f cm", profile.getHeightInCm()) : "Not set");
            fitnessLevelField
                    .setText(profile.getFitnessLevel() != null ? profile.getFitnessLevel().toString() : "Not set");
            goalField.setText(profile.getGoal() != null ? profile.getGoal().getDescription() : "Not set");
        } else {
            ageField.setText("Not set");
            genderField.setText("Not set");
            weightField.setText("Not set");
            heightField.setText("Not set");
            fitnessLevelField.setText("Not set");
            goalField.setText("Not set");
        }

        System.out.println("User profile data loaded: " + user.getUsername());
    }

    /**
     * Change Password - Navigate to password change screen
     */
    @FXML
    public void goToChangePassword(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/profile/SceneChangePassword.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToChangeGoal(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/profile/SceneChangeGoal.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToChangeFitnessLevel(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/profile/SceneChangeFitnessLevel.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    public void logout(ActionEvent event) {
        UserSession.getInstance().logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Scene1.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Logged out successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
