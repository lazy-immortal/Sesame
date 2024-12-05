package io.github.lazyimmortal.sesame.util;

import android.content.Context;

import org.json.JSONObject;

import io.github.lazyimmortal.sesame.BuildConfig;

public class LibraryUtil {
    private static final String TAG = LibraryUtil.class.getSimpleName();

    public static String getLibSesamePath(Context context) {
        String libSesamePath = null;
        try {
            libSesamePath = context.getPackageManager()
                    .getApplicationInfo(BuildConfig.APPLICATION_ID, 0)
                    .nativeLibraryDir + "/" + System.mapLibraryName("sesame");
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return libSesamePath;
    }

    public static Boolean loadLibrary(String libraryName) {
        try {
            System.loadLibrary(libraryName);
            return true;
        } catch (UnsatisfiedLinkError e) {
            return false;
        }
    }


    // native code
    private static native boolean libraryCheckFarmTaskStatus(JSONObject task);
    public static Boolean checkFarmTaskStatus(JSONObject task) {
        return libraryCheckFarmTaskStatus(task); // 注释此行，重写实现
    }

    private static native boolean libraryDoFarmTask(JSONObject task);
    public static Boolean doFarmTask(JSONObject task) {
        return libraryDoFarmTask(task);
    }

    private static native boolean libraryDoFarmDrawTimesTask(JSONObject task);
    public static Boolean doFarmDrawTimesTask(JSONObject task) {
        return libraryDoFarmDrawTimesTask(task);
    }
}
