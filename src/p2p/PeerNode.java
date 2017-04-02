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
        Config.currentId = 2;
        int peer = Config.currentId;


        Config.DEFAULT_ROOT += String.valueOf(peer);
        PeerNode node = new PeerNode(Config.DEFAULT_PORT + peer);
        node.start();
    }
}
