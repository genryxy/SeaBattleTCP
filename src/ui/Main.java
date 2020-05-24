package ui;


import battleship.Ocean;
import connection.Client;
import connection.NetworkConnection;
import connection.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;


public class Main extends Application {

    private boolean isServer = true;

    private Controller controller;
    private PlacementController placeController;
    private NetworkConnection connection;
    private Ocean createdOcean;

    private Server createServer(int port) {
        return new Server(port, data -> {
            // Get control back to the ui thread
            Platform.runLater(() -> {
                String str = data.toString();
                if (str.equals("Client")) {
                    controller.setHasOpponent(true);
                    controller.sendInfo("Server", null, true);
                    Dialogs.createAlertStartGame();
//                    System.out.println("get msg from client, let's begin");
                } else if (str.contains("Winner: Client")) {
                    controller.sendInfo(str + "\n\n" + controller.createInfoTextAboutGame(), null, true);
                    Dialogs.createAlertTotalResult(str + "\n\n" + controller.createInfoTextAboutGame());
                } else if (str.contains("Winner: Server")) {
                    Dialogs.createAlertTotalResult(str);
                } else {
                    processData(str);
                }
            });
        });
    }

    private Client createClient(int port, String host) {
        return new Client(port, host, data -> {
            // Get control back to the ui thread
            Platform.runLater(() -> {
                String str = data.toString();
                if (str.equals("Server")) {
                    controller.setHasOpponent(true);
                    Dialogs.createAlertStartGame();
//                    System.out.println("get msg from server, let's begin");
                } else if (str.contains("Winner: Server")) {
                    controller.sendInfo(str + "\n\n" + controller.createInfoTextAboutGame(), null, true);
                    Dialogs.createAlertTotalResult(str + "\n\n" + controller.createInfoTextAboutGame());
                } else if (str.contains("Winner: Client")) {
                    Dialogs.createAlertTotalResult(str);
                } else {
                    processData(str);
                }
            });
        });
    }

    private void processData(String str) {
        if (str.equals("exit")) {
            try {
                Dialogs.createAlertOpponentExit();
            } catch (IllegalStateException e) {
            }
            controller.setHasOpponent(false);
            controller.sendInfo("kill", null, true);
        } else if (checkString(str)) {
            String[] text = str.split(",");
            controller.setGotAnswer(true);
            controller.markShotFromOpponent(Integer.parseInt(text[0]), Integer.parseInt(text[1]), text[2]);
        } else {
            controller.setTxtLoggingOppMove(str);
        }
    }

    private boolean checkString(String str) {
        String digits = "0123456789";
        return str.contains(",") && str.split(",").length == 3
                && str.split(",")[0].length() == 1 && digits.contains(str.split(",")[0])
                && str.split(",")[1].length() == 1 && digits.contains(str.split(",")[1]);
    }

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
        Boolean isRandomly;
        List<String> params = getParameters().getRaw();
        if (params.size() > 0 && params.get(0).trim().equals("Server")) {
            isServer = true;
        } else if (params.size() > 0 && params.get(0).equals("Client")) {
            isServer = false;
        } else {
            Dialogs.createAlertWrongParams();
            System.out.println("Wrong params!");
            return;
        }
        String vals = Dialogs.showInputTextDialog(isServer);
        if (vals.length() == 0) {
            return;
        }
        connection = isServer ? createServer(Integer.parseInt(vals.substring(0, vals.length() - 1))) :
                createClient(Integer.parseInt(vals.split(",")[0]), vals.split(",")[1]);
        connection.startConnection();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setMinHeight(600);
        primaryStage.setMaxHeight(750);
        primaryStage.setMinWidth(1000);
        primaryStage.setMaxWidth(1000);
        primaryStage.setTitle(isServer ? "Battleship_Server" : "Battleship_Client");
        controller = loader.getController();
        controller.setConnection(connection);

        isRandomly = Dialogs.createAlertChoicePlacement();
        if (isRandomly) {
            PlacementStage placementStage = new PlacementStage();
            FXMLLoader loaderPlacement = new FXMLLoader(getClass().getResource("Placement.fxml"));

            Parent rootPlacement = loaderPlacement.load();
            placementStage.setMinHeight(500);
            placementStage.setMinWidth(700);
            placementStage.setTitle("Placement ships");
            placementStage.setScene(new Scene(rootPlacement, 700, 600));

            placeController = loaderPlacement.getController();
            placeController.initializeAll();
            createdOcean = placementStage.showAndReturn(placeController);
        }

        controller.setGotAnswer(isServer);
        controller.setIsServer(isServer);
//        controller.setHasOpponent(!isServer);
        controller.initializeOcean(createdOcean);
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }
}