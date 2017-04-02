package cs.server;

import cs.Config;
import cs.Protocol;
import cs.Utils;

import java.io.*;
import java.net.Socket;

/**
 * Created by kevin on 01/04/2017.
 */

public class ServerThread extends Thread {
    private File root = new File(Config.SERVER_ROOT);
    private static final int  bufferSize = 4096;

    private int depth;  // the depth of directory from the root directory, just for convenience.
    private File cwd;  // current working directory
    private Socket connection = null;
    private int id;  // for debugging

    public ServerThread(Socket socket, int id) {
        super("ServerThread");
        this.connection = socket;
        this.cwd = root;
        this.depth = 0;
        this.id = id;
    }

    public ServerThread(Socket socket, int id, String root) {
        super("ServerThread");
        this.connection = socket;
        this.root = new File(root);
        this.id = id;
        this.depth = 0;
        this.cwd = this.root;
    }

    public void run() {
        System.out.println("Connected with client " + id + " successfully.");
        try (InputStream is = connection.getInputStream();
            OutputStream os = connection.getOutputStream();
        ) {
            DataInputStream input = new DataInputStream(is);
            DataOutputStream output = new DataOutputStream(os);

            int protocol;
            boolean listen = true;
            String line;
            while (listen) {
                protocol = input.read();
                System.out.println("Protocol: " + protocol);
                switch (protocol) {
                    case Protocol.ECHO:
                        line = input.readUTF(); // read message
                        //output.write(Protocol.ECHO);
                        output.writeUTF(line);
                        break;
                    case Protocol.CD:
                        line = input.readUTF(); // read new directory name
                        changeDirectory(line);
                        break;
                    case Protocol.GET:
                        line = input.readUTF(); // read filename
                        sendFile(output, line);
                        break;
                    case Protocol.LS:
                        sendFileList(output);
                        break;
                    case Protocol.SEND:
                        // upload is not implemented.
                        break;
                    case Protocol.BYE:
                        listen = false;
                        break;
                    case -1:
                        listen = false;
                        break;
                    default:
                        System.err.println("Error protocol: " + protocol);
                        break;
                }
            }
        } catch (IOException e) {
            if (e instanceof EOFException) {
                // pass
            } else {
                e.printStackTrace();
            }
        }
        System.out.println("Disconnected with client " + id + " successfully.");
    }

    private void changeDirectory(String directoryName) {

        // fall back to parent directory
        if (directoryName.equals("..")) {
            if (depth == 0) {// never beyond the root
                System.err.println("Client tries to get out, in vain.");
            } else {
                cwd = cwd.getParentFile();
                --depth;
            }
            return;
        }

        File directory = new File(cwd, directoryName);


        // sanity check
        // check if the named directory exists, should never err.
        if (!directory.exists()) {
            System.err.println("Client tries to enter a non-existing directory.");
            return;
        }
        if (!directory.isDirectory()) {
            System.err.println("Client tries to enter a non-directory file.");
            return;
        }

        ++depth;
        cwd = directory;
    }

    private void sendFile(DataOutputStream output, String filename) {
        System.out.println("Get file " + filename);
        File file = new File(cwd, filename);
        int found = 1;
        if (!file.exists()) {
            System.err.println("Client tries to get a non-existing file.");
            found = 0;
        }
        if (file.isDirectory()) {
            System.err.println("Client tries to get a directory.");
            found = 0;
        }
        long length = file.length(); // get file length in bytes
        try (FileInputStream fis = new FileInputStream(file.getPath())) {
            output.write(found);             // tell the client the file is found or not.
            if (found == 0) {
                return;
            }
            // file meta: length
            output.writeLong(length);
            output.flush();

            byte[] buffer = new byte[bufferSize];
            int read = 0;
            while ((read = fis.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendFileList(DataOutputStream output) {
        File[] files = listFiles();
        try {
            output.writeLong(files.length);
            for (File file : files) {
                if (file.isDirectory()) {
                    output.write(1);
                } else {
                    output.write(0);
                }
                output.writeUTF(file.getName());
            }
            output.flush();
        } catch (IOException e) {
            // damn
        }
    }

    private File[] listFiles() {
        return cwd.listFiles();
    }


}
