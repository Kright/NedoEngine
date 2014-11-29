package com.vk.lgorsl.NedoEngine.openGL;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Build;
import com.vk.lgorsl.NedoEngine.utils.NedoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.*;

import static android.opengl.GLES20.*;

/**
 * вспомогательные функции дял openGL и не только
 *
 * Created by lgor on 18.11.2014.
 */
public class GLHelper {

    public static FloatBuffer make(float[] arr){
        FloatBuffer fb = ByteBuffer.allocateDirect(arr.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    public static ShortBuffer make(short[] arr) {
        ShortBuffer sb = ByteBuffer.allocateDirect(arr.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        sb.put(arr);
        sb.position(0);
        return sb;
    }

    public static IntBuffer make(int[] arr) {
        IntBuffer sb = ByteBuffer.allocateDirect(arr.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
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

    public static void checkError(){
        int code = getError();
        if (code == GL_NO_ERROR) {
            return;
        }
        throw new NedoException(getErrorString(code));
    }

    public static String getErrorString(int code){
        switch (code){
            case GL_NO_ERROR : return "GL no error";
            case GL_INVALID_ENUM: return "GL invalid enum";
            case GL_INVALID_FRAMEBUFFER_OPERATION: return "GL framebuffer operation";
            case GL_INVALID_VALUE: return "GL invalid operation";
            case GL_INVALID_OPERATION: return "GL invalid operation";
            case GL_OUT_OF_MEMORY: return "GL out ou memory";
            default: return "GL unknown error code!!";
        }
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

    /**
     *
     * @param res
     * @param id
     * @return loaded bitmap or red bitmap 16*16 px if errors
     */
    public static Bitmap loadBitmap2(Resources res, int id){
        InputStream is = res.openRawResource(id);
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException ex){
                bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(0xFFFF0000);
            }
        }
        return bitmap;
    }

    public static String getExtensions(){
        return glGetString(GL_EXTENSIONS);
    }

    //никто же не будет вызывать из разных потоков getIntegerv() и прочие?
    private final static int[] intv = new int[4];
    private final static float[] floatv = new float[4];
    private final static boolean[] boolv = new boolean[4];

    /**
     * https://www.khronos.org/opengles/sdk/docs/man/xhtml/glGet.xml
     *
     * @param pname
     * GL_ACTIVE_TEXTURE
     * GL_CURRENT_PROGRAM
     *
     * GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
     * GL_MAX_CUBE_MAP_TEXTURE_SIZE
     * GL_MAX_TEXTURE_SIZE
     *
     * GL_MAX_TEXTURE_IMAGE_UNITS (>=8)
     * GL_MAX_VARYING_VECTORS
     * GL_MAX_VERTEX_ATTRIBS
     * and etc.
     * @return integer value[]
     */
    public static int[] getIntegerv(int pname){
        glGetIntegerv(pname, intv, 0);
        return intv;
    }

    /**
     * https://www.khronos.org/opengles/sdk/docs/man/xhtml/glGet.xml
     *
     * @param pname
     * @return array
     */
    public static float[] getFloatv(int pname){
        glGetFloatv(pname, floatv, 0);
        return floatv;
    }

    /**
     * https://www.khronos.org/opengles/sdk/docs/man/xhtml/glGet.xml
     *
     * @param pname
     * for example, GL_CULL_FACE
     * @return array of boolean values
     */
    public static boolean[] getBooleanv(int pname){
        glGetBooleanv(pname, boolv, 0);
        return boolv;
    }
}
