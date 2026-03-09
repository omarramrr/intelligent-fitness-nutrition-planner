package view.shared;

import javafx.scene.control.Button;

public class BackButton extends Button {

    public BackButton() {
        super("Back");
        init();
    }

    public BackButton(String text) {
        super(text);
        init();
    }

    private void init() {
        this.getStyleClass().add("back-button");
        this.setMnemonicParsing(false);
        // Unicode arrow: ←
        // Using graphic or text inclusion. Since requirements say "Icon... Left of
        // text",
        // we can prepend the arrow or use setGraphic.
        // Let's use text concatenation for simplicity as we are using CSS for styling.
        // However, user asked for "Left arrow icon... White... Left of text".
        // A simple way is to set the text to "← Back" or set a text-fill graphic.
        // Let's us "← " prefix in the text for now as it's the most robust without
        // extra image resources.
        this.setText("← " + this.getText());
    }
}
