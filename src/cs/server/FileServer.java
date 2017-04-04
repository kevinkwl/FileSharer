package cs.server;

import cs.Config;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;


/**
 * Created by kevin on 01/04/2017.
 */
public class FileServer {
    protected int counter = 0;

    protected int portNumber;

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

        if (args.length != 2) {
            System.err.println("Usage: java FileServer <port number> <server_root>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        Config.SERVER_ROOT = args[1];

        FileServer server = new FileServer(portNumber);
        server.start();
    }


}
