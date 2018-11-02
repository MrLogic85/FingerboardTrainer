
package se.sleepyduckstudio.logger;

public class Log {
    public static final String TAG = "fingerboardtrainer";

    public static void d(String text) {
        android.util.Log.d(TAG, text);
    }
}
