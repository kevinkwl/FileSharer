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
    private static final File root = new File(Config.SERVER_ROOT);
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

    public void run() {
        System.out.println("Connected with client " + id + " successfully.");
        try (InputStream is = connection.getInputStream();
            OutputStream os = connection.getOutputStream();
        ) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            DataOutputStream output = new DataOutputStream(os);

            int protocol;
            boolean listen = true;
            String line;
            while (listen) {
                line = reader.readLine();
                if (line.length() == 0) {
                    continue;
                }
                protocol = Integer.parseInt(line);
                switch (protocol) {
                    case Protocol.ECHO:
                        line = reader.readLine(); // read message
                        Utils.writeln(output, Protocol.ECHO);
                        Utils.writeln(output, line);
                        break;
                    case Protocol.CD:
                        line = reader.readLine(); // read new directory name
                        changeDirectory(line);
                        sendFileList(output);
                        break;
                    case Protocol.GET:
                        line = reader.readLine(); // read filename
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
        File file = new File(cwd, filename);
        if (!file.exists()) {
            System.err.println("Client tries to get a non-existing file.");
            return;
        }
        if (file.isDirectory()) {
            System.err.println("Client tries to get a directory.");
            return;
        }

        long length = file.length(); // get file length in bytes
        try (FileInputStream fis = new FileInputStream(file.getPath())) {
            // protocol header
            Utils.writeln(output, Protocol.SEND);
            // file meta: name, length
            Utils.writeln(output, filename);
            Utils.writeln(output, length);
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
            Utils.writeln(output, files.length);
            for (File file : files) {
                if (file.isDirectory()) {
                    Utils.writeln(output, 0);       // 0 for directory, 1 for normal file
                } else {
                    Utils.writeln(output, 1);
                }
                Utils.writeln(output, file.getName());
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
