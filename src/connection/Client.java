package connection;


import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends NetworkConnection {

    private String host;
    private int port;

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
