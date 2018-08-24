package cn.nineox.robot.utils;

import android.util.Log;

/**
 * Created by Administrator on 2018/8/10.
 */

public class LogUtil {

    private final static String TAG = "yhkapp_";

    private final static String DEBUG_TAG = "debug_test";

    private static boolean IS_DEBUG = true;
    public static boolean show_DEBUG = true;
    public static void setIsDebug(boolean debug) {
        IS_DEBUG = debug;
    }

    public static void i(String tag, String text) {
        if (IS_DEBUG) {
            Log.i(TAG + tag, text);
        }
    }

    public static void d(String tag, String text) {
        if (IS_DEBUG) {
            Log.d(TAG + tag, text);
        }
    }

    public static void w(String tag, String text) {
        if (IS_DEBUG) {
            Log.w(TAG + tag, text);
        }
    }

    public static void e(String tag, String text) {
        if (IS_DEBUG) {
            Log.e(TAG + tag, text);
        }
    }

    public static void debug(String text) {
        if (show_DEBUG) {
            Log.i(DEBUG_TAG, text);
        }
    }
}
