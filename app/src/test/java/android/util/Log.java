package android.util;

// Credit to Paglian: https://stackoverflow.com/questions/36787449/how-to-mock-method-e-in-log
/**
 * Mock Log instance for use in unit testing.
 */
public class Log {

    public static int d(String tag, String msg) {
        return log("DEBUG", tag, msg);
    }

    public static int i(String tag, String msg) {
        return log("INFO", tag, msg);
    }

    public static int w(String tag, String msg) {
        return log("WARN", tag, msg);
    }

    public static int e(String tag, String msg) {
        return log("ERROR", tag, msg);
    }

    private static int log(String level, String tag, String msg) {
        System.out.println(String.format("%s: %s: %s", level, tag, msg));
        return 0;
    }

}
