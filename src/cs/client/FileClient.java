package cs.client;

import cs.Config;
import cs.Protocol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.text.Collator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kevin on 01/04/2017.
 */
public class FileClient {
    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private int port;
    private Socket clientSocket = null;

    private DataInputStream input;
    private DataOutputStream output;

    private LinkedList<String> directoryPath = new LinkedList<>();

    private ObservableList<String> files = FXCollections.observableArrayList();

    public FileClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            clientSocket = new Socket(host, port);
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            fetchFileList();
        } catch (IOException e) {
            System.err.println("Cannot connected to " + host + " port " + port);
            return false;
        }
        return true;
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    public ObservableList<String> getFiles() {
        assert isConnected();
        Collections.sort(files, Collator.getInstance());
        return files;
    }

    public void changeDirectory(String path) {
        assert isConnected();
        try {
            output.write(Protocol.CD);
            output.writeUTF(path);
            fetchFileList();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (path.equals("..") && !directoryPath.isEmpty()) {
            directoryPath.removeLast();
        } else if (!path.equals("..")) {
            directoryPath.addLast(path);
        }
    }

    public void fetchFileList() {
        try {
            output.write(Protocol.LS);
            long numberOfFiles = input.readLong();
            files = FXCollections.observableArrayList();
            for (long i = 0; i < numberOfFiles; i++) {
                int isDirectory = input.read();
                String file = input.readUTF();
                if (isDirectory == 1) {
                    file += "/";
                }
                files.add(file);
            }
        } catch (IOException e) {
            // do nothing
        }
    }

    public String getPath() {
        String path = "/" + String.join("/", directoryPath);
        return path;
    }

    public boolean downloadFile(String filename) {
        try {
            output.write(Protocol.GET);
            output.writeUTF(filename);

            int found = input.read();
            if (found == 0) {
                return false;
            }
            byte[] buffer = new byte[4096];

            long fileSize = input.readLong();
            long remaining = fileSize;
            int read = 0;
            FileOutputStream fos = new FileOutputStream(Config.CLIENT_ROOT + "/" + filename);
            while ((read = input.read(buffer, 0, (int)(Math.min(buffer.length, remaining)))) > 0) {
                remaining -= read;
                fos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            System.err.println("Error in downloading file: " + filename);
        }
        return true;
    }

    public void disconnect() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            System.exit(-1);
        }
    }

}
