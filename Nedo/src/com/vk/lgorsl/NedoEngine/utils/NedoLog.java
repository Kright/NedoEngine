package com.vk.lgorsl.NedoEngine.utils;

import android.util.Log;

/**
 * used for log
 *
 * Created by lgor on 29.11.2014.
 */
public class NedoLog {

    private NedoLog(){}

    public static String log_string = "Nedo Engine";
    public static boolean showLog = true;
    public static boolean asserts = true;
    public static boolean assertsThrowsExceptions = true;

    public static void log(String s){
        if (showLog) {
            Log.d(log_string, s);
        }
    }

    public static void logError(String s){
        Log.e(log_string, s);
    }

    public static void Assert(boolean condition, String error){
        if (asserts & !condition){
            logError(error);
            if (assertsThrowsExceptions) {
                throw new NedoException(error);
            }
        }
    }
}
