package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Utils {

    /**
     * Creates a new instance of the button with specified parameters.
     *
     * @param text The content of the button.
     * @return The new instance of button.
     */
    public static Button createButton(String text) {
        Button btn = new Button(text);
        setBtnBackground(btn, Color.LIGHTBLUE);
        // Allows to resize the button.
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return btn;
    }

    /**
     * It sets the button's background. It helps to have the similar
     * values of corner radius and insets.
     *
     * @param btn   The instance of button for changing.
     * @param color The required color of background.
     */
    public static void setBtnBackground(Button btn, Color color) {
        btn.setBackground(new Background(new BackgroundFill(color, new CornerRadii(3), new Insets(0.5))));
    }
}
