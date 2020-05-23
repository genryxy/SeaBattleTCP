package connection;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class Client extends NetworkConnection {

    private String host;
    private int port;

//    public Client(int port, String host) {
//        this.port = port;
//        this.host = host;
//    }
//
//    public void createSocket() {
//        try (Socket clientSocket = new Socket("localhost", 4004);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//             // писать туда же
//             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));) {
//
//            while (true) {
//                System.out.println("Клиент введите сообщение:");
//                String word = reader.readLine();
//
//                if (word.equals("exit")) {
//                    clientSocket.close();
//                    reader.close();
//                    in.close();
//                    out.close();
//                    System.out.println("Клиент был закрыт...");
//                    System.exit(0);
//                }
//
//                out.write(word + "\n");
//                out.flush();
//
//                String serverWord = in.readLine();
//                System.out.println("Ответ сервера: " + serverWord);
//            }
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public Client(int port, String host, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
        this.host = host;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return host;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
