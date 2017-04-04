package p2p;

import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

/**
 * Created by kevin on 02/04/2017.
 */
public class PeerNode {
    private int serverPort;
    private String serverHost;

    public PeerNode(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {
        // start server
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PeerServer myServer = new PeerServer(serverPort);
                try {
                    myServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();


        // start client
        String[] args = new String[]{};
        PeerMain.main(args);
        try {
            new PeerMain().start(new Stage());
        } catch (Exception e) {
            System.err.println("Cannot start application.");
        }
    }

    public static void main(String args[]) {
        int peer;
        String path;
        if (args.length != 2) {
            System.out.println("Usage1: java PeerNode <peerId:int> <peerFile_root:String>");
            System.exit(-1);
        }
        peer = Integer.parseInt(args[0]);

        Config.DEFAULT_ROOT = args[1];

        Config.currentId = peer;

        PeerNode node = new PeerNode(Config.DEFAULT_PORT + peer);
        node.start();
    }
}
