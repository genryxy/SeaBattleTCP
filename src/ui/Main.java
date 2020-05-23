package ui;

//import com.connection.Client;
//import com.connection.Server;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


public class Main extends Application {

    private boolean isServer = true;

    private TextArea messages = new TextArea();
    private NetworkConnection connection = isServer ? createServer() : createClient();

    private Parent createContent() {
        messages.setPrefHeight(450);
        TextField input = new TextField();
        input.setOnAction(actionEvent -> {
            String msg = isServer ? "Server: " : "Client: ";
            msg += input.getText();
            input.clear();

            messages.appendText(msg + "\n");
            try {
                connection.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        VBox root = new VBox(20, messages, input);
        root.setPrefSize(500, 500);
        return root;
    }

    private Server createServer() {
        return new Server(4080, data -> {
            // Get control back to the ui thread
//            Platform.runLater(() -> {
                messages.appendText("Server: " + data.toString() + "\n");
//            });
        });
    }

    private Client createClient() {
        return new Client(4080, "localhost", data -> {
            // Get control back to the ui thread
//            Platform.runLater(() -> {
                messages.appendText("Client: " + data.toString() + "\n");
//            });
        });
    }

    @Override
    public void init() {
        connection.startConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        primaryStage.setScene(new Scene(createContent()));
//        primaryStage.show();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setMinHeight(600);
        primaryStage.setMaxHeight(750);
        primaryStage.setMinWidth(1000);
        primaryStage.setMaxWidth(1000);
        primaryStage.setTitle(isServer ? "Battleship_Server" : "Battleship_Client");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
        Controller controller = loader.getController();
        controller.initializeAll(connection);

//        List<String> params = getParameters().getRaw();
//        if (params.size() > 0 && params.get(0).substring(0, "Server".length()).equals("Server")) {
////            Server myServer = new Server(4080, getClass(), primaryStage);
//            Server myServer = new Server(4080);
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
//            Parent root = null;
//            try {
//                root = loader.load();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            primaryStage.setMinHeight(500);
//            primaryStage.setMinWidth(450);
//            primaryStage.setTitle("Battleship_Server");
//            primaryStage.setScene(new Scene(root, 600, 600));
//            primaryStage.show();
//            Controller controller = loader.getController();
//            controller.initializeAll();
//            myServer.createSocket();
//        } else if (params.size() > 0 && params.get(0).substring(0, "Client".length()).equals("Client")) {
//            Client myClient = new Client(4080, "localhost");
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
//            Parent root = loader.load();
//            primaryStage.setMinHeight(500);
//            primaryStage.setMinWidth(450);
//            primaryStage.setTitle("Battleship_Client");
//            primaryStage.setScene(new Scene(root, 600, 600));
//            primaryStage.show();
//            Controller controller = loader.getController();
//            controller.initializeAll();
//            myClient.createSocket();
//        } else {
//            System.out.println("Wrong params!");
//        }
    }
}