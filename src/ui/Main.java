package ui;


import connection.Client;
import connection.NetworkConnection;
import connection.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


public class Main extends Application {

    private boolean isServer = true;

    private TextArea messages = new TextArea();
    private Controller controller;
    private NetworkConnection connection;

    private Server createServer(int port) {
        return new Server(port, data -> {
            // Get control back to the ui thread
            Platform.runLater(() -> {
                if (data.equals("Client")) {
                    controller.setHasOpponent(true);
                    System.out.println("get msg from client, let's begin");
                } else if (data.equals("exit")) {
                    Dialogs.createAlertOpponentExit();
                    controller.setHasOpponent(false);
                    controller.reset();
                    controller.sendInfo("kill", null, false);
                } else {
                    String[] text = data.toString().split(",");
                    controller.setGotAnswer(true);
                    controller.markShotFromOpponent(Integer.parseInt(text[0]), Integer.parseInt(text[1]), text[2]);
                }
            });
        });
    }

    private Client createClient(int port, String host) {
        return new Client(port, host, data -> {
            // Get control back to the ui thread
            Platform.runLater(() -> {
                if (data.equals("Server")) {
                    controller.setHasOpponent(true);
                    System.out.println("get msg from server, let's begin");
                } else if (data.equals("exit")) {
                    Dialogs.createAlertOpponentExit();
                    controller.setHasOpponent(false);
                    controller.reset();
                    controller.sendInfo("kill", null, false);
                } else if (data.toString().contains(",")) {
                    String[] text = data.toString().split(",");
                    controller.setGotAnswer(true);
                    controller.markShotFromOpponent(Integer.parseInt(text[0]), Integer.parseInt(text[1]), text[2]);
                }
            });
        });
    }

//    @Override
//    public void init() {
//        connection.startConnection();
//    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        if (connection != null) {
            connection.closeConnection();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> params = getParameters().getRaw();
        if (params.size() > 0 && params.get(0).substring(0, "Server".length()).equals("Server")) {
            isServer = true;
        } else if (params.size() > 0 && params.get(0).substring(0, "Client".length()).equals("Client")) {
            isServer = false;
        } else {
            System.out.println("Wrong params!");
            return;
        }
        String vals = Dialogs.showInputTextDialog(isServer);
        if (vals.length() == 0) {
            return;
        }
        connection = isServer ? createServer(Integer.parseInt(vals.substring(0, vals.length() - 1))) :
                createClient(Integer.parseInt(vals.split(",")[0]), vals.split(",")[1]);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setMinHeight(600);
        primaryStage.setMaxHeight(750);
        primaryStage.setMinWidth(1000);
        primaryStage.setMaxWidth(1000);
        primaryStage.setTitle(isServer ? "Battleship_Server" : "Battleship_Client");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
        controller = loader.getController();
        controller.setGotAnswer(isServer);
        controller.setHasOpponent(!isServer);
        connection.startConnection();
        controller.initializeAll(connection);
    }
}