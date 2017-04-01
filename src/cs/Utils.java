package cs;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kevin on 01/04/2017.
 */
public class Utils {

    /**
     * write to DataOutputStream, with newline at the end.
     * @param output
     * @param str
     * @throws IOException
     */
    public static void writeln(DataOutputStream output, String str) throws IOException {
        output.writeBytes(str);
        output.writeByte('\n');
        output.flush();
    }

    public static void writeln(DataOutputStream output, int n) throws IOException {
        output.writeInt(n);
        output.writeByte('\n');
        output.flush();
    }

    public static void writeln(DataOutputStream output, long n) throws IOException {
        output.writeLong(n);
        output.writeByte('\n');
        output.flush();
    }
}
