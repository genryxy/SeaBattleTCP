package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class Dialogs {
    private static boolean isBadPort = true;
    private static boolean isBadHost = true;

    /**
     * User can choose how to place all ships (randomly or manually).
     *
     * @return true - randomly, false - manually placement
     */
    public static boolean createAlertChoicePlacement() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choice");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to put ships randomly or manually? ");

        ButtonType okButtonType = new ButtonType("Randomly", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Manually", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().clear();
        alert.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE;
    }

    /**
     * Creates an alert with specified title and content.
     *
     * @param title   The title of the alert.
     * @param content The content of the alert.
     */
    public static void createAlertInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Creates alert that confirms the end of the game. If user press 'ok',
     * he'll begin a new game. Otherwise nothing happens.
     *
     * @param isGameOver It defines the content of the alert (again or new game).
     */
    public static boolean createAlertPlayAgain(boolean isGameOver) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choice");
        alert.setHeaderText(null);
        if (isGameOver) {
            alert.setContentText("The game is over! Do you want to play again?");
        } else {
            alert.setContentText("Do you want to start a new game?");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static String showInputTextDialog(boolean isServer) {
        final StringBuilder resText = new StringBuilder();

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("You're " + (isServer ? "Server. " : "Client. ") + "Please, input some info");

        ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField port = new TextField();
        port.setPromptText("4080");
        TextField host = new TextField();
        host.setPromptText("localhost");

        grid.add(new Label("Port:"), 0, 0);
        grid.add(port, 1, 0);
        if (!isServer) {
            grid.add(new Label("Host:"), 0, 1);
            grid.add(host, 1, 1);
        } else {
            isBadHost = false;
        }

        // Enable/Disable login button depending on whether a port was entered.
        Node okBtn = dialog.getDialogPane().lookupButton(okButtonType);
        okBtn.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        port.textProperty().addListener((observablePort, oldPort, newPort) -> {
            try {
                var tmp = !newPort.trim().isEmpty() ? Integer.parseInt(newPort) : 0;
                isBadPort = tmp <= 1023 || tmp > 65535;
            } catch (NumberFormatException e) {
            }
            okBtn.setDisable(isBadHost || isBadPort);
        });
        host.textProperty().addListener((observableHost, oldHost, newHost) -> {
            String tmp = newHost.trim();
            if (!tmp.isEmpty()) {
                isBadHost = !(tmp.equals("127.0.0.1") || tmp.equals("localhost"));
            }
            okBtn.setDisable(isBadHost || isBadPort);
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(port::requestFocus);

        // Convert the result to a port-host-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(port.getText(), host.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(portHost -> {
            resText.append(portHost.getKey())
                    .append(",")
                    .append(portHost.getValue());
        });
        return resText.toString();
    }
}
