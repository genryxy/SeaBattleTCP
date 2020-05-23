package connection;

import ui.Controller;
import ui.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server extends NetworkConnection{

    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private int port;
//    private Class<? extends Main> aClass;
//    private Stage primaryStage;
//
//    public Server(int port, Class<? extends Main> aClass, Stage primaryStage) {
//        this.port = port;
//        this.aClass = aClass;
//        this.primaryStage = primaryStage;
//        createSocket();
//    }
//
//    public Server(int port) {
//        this.port = port;
//    }
//
//    public void createSocket() {
////        FXMLLoader loader = new FXMLLoader(aClass.getResource("MainWindow.fxml"));
////        Parent root = null;
////        try {
////            root = loader.load();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        primaryStage.setMinHeight(500);
////        primaryStage.setMinWidth(450);
////        primaryStage.setTitle("Battleship_Server");
////        primaryStage.setScene(new Scene(root, 600, 600));
////        primaryStage.show();
////        Controller controller = loader.getController();
////        controller.initializeAll();
//        try (ServerSocket server = new ServerSocket(4004)) {
//            System.out.println("Сервер запущен!");
//            // accept() ждёт подключения
//            try (Socket clientSocket = server.accept();
//                 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                while (true) {
//                    String word = in.readLine();
//                    if (word == null || word.equals("exit")) {
//                        in.close();
//                        out.close();
//                        clientSocket.close();
//                        reader.close();
//                        System.out.println("Сервер закрыт!");
//                        server.close();
//                        System.exit(0);
//                    }
//                    System.out.println("Сообщение пользователя: " + word);
//
//                    System.out.println("Напечатайте ответное сообщение:");
//                    word = reader.readLine();
//
//                    out.write(word + "\n");
//                    out.flush();
//                }
//            } finally {
//                in.close();
//                out.close();
//                System.out.println("Сервер закрыт!");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public Server(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
