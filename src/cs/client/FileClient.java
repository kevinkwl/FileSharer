package cs.client;

import java.net.Socket;

/**
 * Created by kevin on 01/04/2017.
 */
public class FileClient {
    private String host;
    private int port;
    private Socket clientSocket = null;

    public FileClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        return false;
    }
}
