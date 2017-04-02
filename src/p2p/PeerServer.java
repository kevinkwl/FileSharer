package p2p;

import cs.server.FileServer;
import cs.server.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by kevin on 02/04/2017.
 */
public class PeerServer extends FileServer {
    private int myPeerId;
    private String myPeerRoot;

    public PeerServer(int port) {
        super(port);
        myPeerId = Config.currentId;
        myPeerRoot = Config.DEFAULT_ROOT;
    }

    @Override
    public void start() throws IOException {
        System.out.println("File Server is running.");
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                new ServerThread(serverSocket.accept(), counter++, myPeerRoot).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
