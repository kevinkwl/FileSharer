package cs.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;


/**
 * Created by kevin on 01/04/2017.
 */
public class FileServer {
    private static int counter = 0;

    private int portNumber;

    public FileServer(int port) {
        this.portNumber = port;
    }


    public void start() throws IOException {
        System.out.println("File Server is running.");
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                new ServerThread(serverSocket.accept(), counter++).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }


    public static void main(String args[]) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java FileServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        FileServer server = new FileServer(portNumber);
        server.start();
    }


}
