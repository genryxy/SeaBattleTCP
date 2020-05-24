package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

public abstract class NetworkConnection {

    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;
    private volatile boolean isCreated;

    public NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connectionThread.setDaemon(true);
    }

    public void startConnection() {
        connectionThread.start();
    }

    public void send(Serializable data) throws IOException {
        try {
            connectionThread.out.writeObject(data);
        } catch (SocketException e) {
            System.out.println("Opponent's socket was closed! Buy");
            System.exit(0);
        }
    }

    public void closeConnection() throws IOException {
        if (connectionThread.socket != null) {
            connectionThread.socket.close();
        }
    }

    public boolean isCreated() {
        return isCreated;
    }

    protected abstract boolean isServer();

    protected abstract String getIP();

    protected abstract int getPort();


    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try (ServerSocket serverSocket = isServer() ? new ServerSocket(getPort(), 0) : null;
                 Socket sock = isServer() ? serverSocket.accept() : new Socket(getIP(), getPort());
                 ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(sock.getInputStream())) {

                this.socket = sock;
                this.out = out;
                socket.setTcpNoDelay(true);
                isCreated = true;

                while (true) {
                    if (socket.isClosed()) {
                        onReceiveCallback.accept("Socket is closed");
                    } else {
                        Serializable data = (Serializable) in.readObject();
                        onReceiveCallback.accept(data);
                    }
                }
            } catch (ConnectException e) {
                System.out.println("Server doesn't exit");
                System.exit(0);
            } catch (Exception e) {
                System.out.println("exit");
                onReceiveCallback.accept("exit");
                // e.printStackTrace();
            }
        }
    }
}
