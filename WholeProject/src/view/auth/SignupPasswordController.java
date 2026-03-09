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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.shared.SceneNavigator;

public class SignupPasswordController implements Initializable {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField visibleConfirmPasswordField;

    @FXML
    private Label messageLabel;

    private String username;
    private String email;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind visible and hidden fields
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        // Clear message on typing
        passwordField.setOnKeyTyped(e -> messageLabel.setText(""));
        confirmPasswordField.setOnKeyTyped(e -> messageLabel.setText(""));
    }

    public void initData(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @FXML
    public void togglePassword() {
        if (passwordField.isVisible()) {
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
        } else {
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    public void toggleConfirmPassword() {
        if (confirmPasswordField.isVisible()) {
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            visibleConfirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setManaged(true);
        } else {
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            visibleConfirmPasswordField.setVisible(false);
            visibleConfirmPasswordField.setManaged(false);
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            // Try to go back using history (to SignupIdentity)
            SceneNavigator.goBack(stage);
        } catch (Exception e) {
            // Fallback load SignupIdentity explicitly (though data might be lost if not
            // passed back,
            // but SceneNavigator typically handles previously pushed scenes)
            try {
                // If we want to restore data, we'd need to load and initData back.
                // Assuming SceneNavigator pop works fine for now.
                Parent root = FXMLLoader.load(getClass().getResource("/view/auth/SignupIdentity.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @FXML
    public void goToNextStep(ActionEvent event) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add("error-message");

        // Validate Password
        if (password.isEmpty()) {
            messageLabel.setText("Password is required.");
            return;
        }
        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters.");
            return;
        }
        if (!password.matches(".*[A-Z].*")) {
            messageLabel.setText("Password must contain at least one uppercase letter.");
            return;
        }
        if (!password.matches(".*[a-z].*")) {
            messageLabel.setText("Password must contain at least one lowercase letter.");
            return;
        }
        if (!password.matches(".*\\d.*")) {
            messageLabel.setText("Password must contain at least one digit.");
            return;
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            messageLabel.setText("Password must contain at least one special character.");
            return;
        }

        // Validate Confirm Password
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        // Proceed to Step 2 (which is essentially Step 3 of total flow now: Profile
        // Setup)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/auth/Signup2.fxml"));
            Parent root = loader.load();

            Signup2Controller controller = loader.getController();
            // Pass all accumulated data
            controller.initData(username, email, password);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));

        } catch (IOException e) {
            messageLabel.setText("Error loading next step. Please try again.");
            e.printStackTrace();
        }
    }
}
