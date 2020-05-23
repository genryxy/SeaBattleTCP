package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class NetworkConnection {

    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;

    public NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connectionThread.setDaemon(true);
    }

    public void startConnection() {
        connectionThread.start();
    }

    public void send(Serializable data) throws IOException {
        connectionThread.out.writeObject(data);
    }

    public void closeConnection() throws IOException {
        if (connectionThread.socket != null) {
            connectionThread.socket.close();
        }
    }

    protected abstract boolean isServer();

    protected abstract String getIP();

    protected abstract int getPort();


    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try (ServerSocket serverSocket = isServer() ? new ServerSocket(getPort()) : null;
                 Socket socket = isServer() ? serverSocket.accept() : new Socket(getIP(), getPort());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (true) {
                    if (socket.isClosed()) {
                        onReceiveCallback.accept("Socket is closed");
                    } else {
                        Serializable data = (Serializable) in.readObject();
                        onReceiveCallback.accept(data);
                    }
                }
            } catch (Exception e) {
                System.out.println("exit");
//                onReceiveCallback.accept("Connection closed");
                // e.printStackTrace();
            }
        }
    }
}
