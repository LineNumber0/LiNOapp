package ma.oprojet.ln0wp.utils;

import ma.oprojet.ln0wp.BuildConfig;

public class Logger {

    private static final String DEFAULT_TAG = "LINOAPP";

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    public static void d(String message) {
        d(DEFAULT_TAG, message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        android.util.Log.e(tag, message, throwable);
    }

    public static void e(String tag, String message) {
        android.util.Log.e(tag, message);
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, message);
        }
    }
}
