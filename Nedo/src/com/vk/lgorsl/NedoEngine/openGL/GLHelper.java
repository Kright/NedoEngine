package com.vk.lgorsl.NedoEngine.openGL;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;
import com.vk.lgorsl.NedoEngine.utils.NedoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * вспомогательные функции дял openGL и не только
 *
 * Created by lgor on 18.11.2014.
 */
public class GLHelper {

    public static String LOG_STRING = "Nedo Engine";
    public static boolean showLog = true;

    public static void log(String s){
        if (showLog) {
            Log.d(LOG_STRING, s);
        }
    }

    public static void logError(String s){
        Log.e(LOG_STRING, s);
    }

    public static FloatBuffer make(float[] arr){
        FloatBuffer fb = ByteBuffer.allocateDirect(arr.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.position(0);
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    public static ShortBuffer make(short[] arr) {
        ShortBuffer sb = ByteBuffer.allocateDirect(arr.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        sb.position(0);
        sb.put(arr);
        sb.position(0);
        return sb;
    }

    /**
     * possible values :
     * NO_ERROR
     * INVALID_ENUM
     * INVALID_FRAMEBUFFER_OPERATION
     * INVALID_VALUE
     * INVALID_OPERATION
     * OUT_OF_MEMORY
     * @return them
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static int getError(){
        return GLES20.glGetError();
    }

    /**
     * @return glGetError != GL_NO_ERROR
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean hasError(){
        return GLES20.glGetError() != GLES20.GL_NO_ERROR;
    }

    public static String loadRawFileAsOneString(Resources res, int id){
        return loadRawFileAsOneString(res, id, "");
    }

    public static String loadRawFileAsOneString(Resources res, int id, String append) {
        StringBuilder result = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(res.openRawResource(id)));
        String line;
        try {
            while ((line = bf.readLine()) != null) {
                result.append(line).append(append);
            }
        }
        catch (IOException e) {
            throw new NedoException("can't load raw resource, IOException :(");
        }
        return result.toString();
    }

    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    static {
        options.inScaled = false;
    }

    /**
     * loads not scaled bitmap
     */
    public static Bitmap loadBitmap(Resources res, int id) {
        return BitmapFactory.decodeResource(res, id, options);
    }
}
