package view.auth;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.shared.SceneNavigator;

public class SignupIdentityController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Clear message on typing
        usernameField.setOnKeyTyped(e -> messageLabel.setText(""));
        emailField.setOnKeyTyped(e -> messageLabel.setText(""));
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            // Try to go back using history
            SceneNavigator.goBack(stage);
        } catch (Exception e) {
            // Fallback if history logic fails or is empty, load Scene1 explicitly
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Scene1.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @FXML
    public void goToNextStep(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add("error-message");

        // Validate Username
        if (username.isEmpty()) {
            messageLabel.setText("Username is required.");
            return;
        }
        if (username.length() < 3 || username.length() > 20) {
            messageLabel.setText("Username must be 3-20 characters.");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_ -]+$")) {
            messageLabel.setText("Username can only contain letters, numbers, spaces, _ or -");
            return;
        }

        // Validate Email
        // Strict regex matching InputUtils.java
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,7}$";

        if (email.isEmpty()) {
            messageLabel.setText("Email is required.");
            return;
        }
        if (!email.matches(emailRegex)) {
            messageLabel.setText("Invalid email format.");
            return;
        }

        // Check for duplicate email in DB
        if (app.Main.getUserRepository().getUserByEmail(email) != null) {
            messageLabel.setText("Email already registered. Please sign in.");
            return;
        }

        // Proceed to Step 2 (Password)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/auth/SignupPassword.fxml"));
            Parent root = loader.load();

            SignupPasswordController controller = loader.getController();
            controller.initData(username, email); // Pass data

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));

        } catch (IOException e) {
            messageLabel.setText("Error loading next step. Please try again.");
            e.printStackTrace();
        }
    }
}
