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

    /**
     * It start's a new thread with connection.
     */
    public void startConnection() {
        connectionThread.start();
    }

    /**
     * Sends some data to client (you're server) or to server (you're client).
     *
     * @param data Sending data.
     * @throws IOException
     */
    public void send(Serializable data) throws IOException {
        try {
            connectionThread.out.writeObject(data);
        } catch (SocketException e) {
            System.out.println("Opponent's socket was closed! Buy");
            System.exit(0);
        }
    }

    /**
     * Closes connection through socket.
     *
     * @throws IOException
     */
    public void closeConnection() throws IOException {
        if (connectionThread.socket != null) {
            connectionThread.socket.close();
        }
    }

    /**
     * @return true - thread with connection was created, false - otherwise
     */
    public boolean isCreated() {
        return isCreated;
    }

    /**
     * @return true - server, false - client
     */
    protected abstract boolean isServer();

    /**
     * @return String with the value of the host.
     */
    protected abstract String getIP();

    /**
     * @return The number of the port.
     */
    protected abstract int getPort();


    /**
     * A thread for creating connection.
     */
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
            }
        }
    }
}
