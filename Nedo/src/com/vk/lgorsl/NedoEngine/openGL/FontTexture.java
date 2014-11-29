package com.vk.lgorsl.NedoEngine.openGL;

import android.graphics.*;
import android.opengl.GLUtils;

import static android.opengl.GLES20.*;

/**
 * for monospace fonts only
 * Created by lgor on 27.11.2014.
 */
public class FontTexture extends Texture2D {

    public final static String ENGLISH_LOWERCASE = "qwertyuiopasdfghjklzxcvbnm";
    public final static String ENGLISH_UPPERCASE = "QWERTYUIOPASDFGHJKLZXCVBNM";
    public final static String RUSSIAN_LOWERCASE = "йцукенгшщзхъфывапролджэячсмитьбю";
    public final static String RUSSIAN_UPPERCASE = "ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
    public final static String SYMBOLS = " <>(){}[]+-+_*/\\|,.:;\'\"!@#$%^&№";

    protected float[] coords;
    protected char[] chars;

    protected FontTexture(int[] id, char[] chars, float[] coords) {
        super(id);
        this.coords = coords;
    }

    public static FontTexture load(Typeface typeface, int textureSize, float fontSize, String symbols) {
        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setTypeface(typeface);
        paint.setTextSize(fontSize);

        Rect bounds = new Rect();
        char[] cc = symbols.toCharArray();
        paint.getTextBounds(cc, 0, cc.length, bounds);

        final int h = bounds.height();
        float d = 8;
        float x = d;
        float y = d - bounds.top;

        int wd = (int) (d + bounds.width() / cc.length);
        int n = textureSize / wd;

        float[] coord = new float[4*cc.length];
        int p=0;

        for (int i = 0, pos = 0; pos<cc.length; pos++, i++) {
            if (i == n) {
                i = 0;
                y += h + d;
                x = d;
            }
            canvas.drawText(cc, pos, 1, x, y, paint);
            x += wd;
        }

        int[] texId = new int[1];
        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);

        TextureLoader.setParameters(GL_NEAREST_MIPMAP_LINEAR, GL_LINEAR,
                GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);

        int format = GLUtils.getInternalFormat(bitmap);
        int type = GLUtils.getType(bitmap);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, format, bitmap, type, 0);

        glGenerateMipmap(GL_TEXTURE_2D);

        return new FontTexture(texId, cc, coord);
    }
}
