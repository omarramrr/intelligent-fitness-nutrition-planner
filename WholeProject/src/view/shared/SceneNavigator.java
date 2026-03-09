package view.shared;

import java.util.Stack;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for managing scene navigation and history.
 */
public class SceneNavigator {

    private static final Stack<Scene> history = new Stack<>();

    /**
     * Navigates to a new scene and pushes the current scene onto the history stack.
     * Automatically applies the global stylesheet to ensure consistent theming.
     * 
     * @param stage The primary stage
     * @param scene The new scene to display
     */
    public static void navigateTo(Stage stage, Scene scene) {
        if (stage.getScene() != null) {
            history.push(stage.getScene());
        }

        // Apply global stylesheet for consistent dark theme across all scenes
        String globalStylesheet = SceneNavigator.class.getResource("/view/shared/Style.css").toExternalForm();
        if (!scene.getStylesheets().contains(globalStylesheet)) {
            scene.getStylesheets().add(globalStylesheet);
        }

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Navigates back to the previous scene in the history stack.
     * If history is empty, does nothing (or could fallback).
     * 
     * @param stage The primary stage
     */
    public static void goBack(Stage stage) {
        if (!history.isEmpty()) {
            stage.setScene(history.pop());
            stage.show();
        } else {
            System.out.println("SceneNavigator: No history to go back to.");
            // Fallback: Could load Scene1 if needed, but for now we log.
        }
    }

    /**
     * Clears the navigation history. Useful when logging out or going to Home.
     */
    public static void clearHistory() {
        history.clear();
    }
}
