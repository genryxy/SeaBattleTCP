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
     * Creates alert that your opponent has finished game.
     */
    public static void createAlertStartGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Start game");
        alert.setHeaderText(null);
        alert.setContentText("The opponent appeared. Let's start game!");
        alert.showAndWait();
    }

    /**
     * Creates alert that you can't make move before your opponent's answer.
     */
    public static void createAlertOpponentMove() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Patience");
        alert.setHeaderText(null);
        alert.setContentText("You must wait for the opponent to make a move");
        alert.showAndWait();
    }

    /**
     * Creates alert that you can't make move before your opponent's answer.
     */
    public static void createAlertWrongParams() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong params");
        alert.setHeaderText(null);
        alert.setContentText("You should specify a parameter: Server or Client");
        alert.showAndWait();
    }

    /**
     * Creates alert that you don't have opponent.
     */
    public static void createAlertNotOpponent() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Patience");
        alert.setHeaderText(null);
        alert.setContentText("You don't have an opponent. Please, wait!");
        alert.showAndWait();
    }

    /**
     * Creates alert that your opponent has finished game.
     */
    public static void createAlertOpponentExit() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("End");
        alert.setHeaderText(null);
        alert.setContentText("Your opponent is out of the game, so the app will close.");
        alert.showAndWait();
    }

    /**
     * Creates alert with warning about repeating shot in the cell.
     */
    public static void createAlertRepeatedShot() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("You've already fired at this cell. Please, try to shoot at another cell.");
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
//        if (result.get() == ButtonType.OK) {
//            reset();
//        }
    }

    public static String showInputTextDialog(boolean isServer) {
        final StringBuilder resText = new StringBuilder();

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("You're " + (isServer ? "Server. " : "Client. ") + "Please, input some info");

        ButtonType okButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
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
