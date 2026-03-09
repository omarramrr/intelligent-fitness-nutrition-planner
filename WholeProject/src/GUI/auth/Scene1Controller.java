package view.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.shared.SceneNavigator;

public class Scene1Controller {

    private Stage stage;
    private Scene scene;

    // --- Method to switch scenes ---
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            System.out.println("Attempting to load: " + fxmlFile);

            // Get the resource URL - critical null check
            java.net.URL resourceUrl = getClass().getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("CRITICAL ERROR: Resource not found: " + fxmlFile);
                System.err.println("Searched from class: " + getClass().getName());
                System.err.println("Class location: " + getClass().getProtectionDomain().getCodeSource().getLocation());
                return;
            }

            System.out.println("Resource URL found: " + resourceUrl);
            Parent root = FXMLLoader.load(resourceUrl);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);

            // Use SceneNavigator to push current scene to history
            SceneNavigator.navigateTo(stage, scene);

            System.out.println("Successfully loaded: " + fxmlFile);
        } catch (Exception e) {
            System.err.println("ERROR loading " + fxmlFile);
            e.printStackTrace();
        }
    }

    // --- Sign In button clicked ---
    @FXML
    private void onSignInClicked(ActionEvent event) {
        switchScene(event, "/view/auth/Scene2.fxml");
    }

    // --- Sign Up button clicked ---
    @FXML
    private void onSignUpClicked(ActionEvent event) {
        switchScene(event, "/view/auth/SignupIdentity.fxml");
    }
}
