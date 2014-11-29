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
    public final static String RUSSIAN_LOWERCASE = "йцукеёнгшщзхъфывапролджэячсмитьбю";
    public final static String RUSSIAN_UPPERCASE = "ЙЦУКЕЁНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
    public final static String SYMBOLS = " <>(){}[]+-+_*/\\\n|,.:;\'\"!?&@#$%^&№`~";

    protected char[] chars;
    protected float startX, startY, letterWidth, letterHeight, dX, dY;
    protected int countInRow;
    protected int unknownSymbol;

    protected FontTexture(int[] id, char[] chars) {
        super(id);
        this.chars = chars;
        unknownSymbol = position('?');
        if (unknownSymbol==-1){
            //if we go out of font texture, we will take fully transparent space. like ' ' symbol
            unknownSymbol = chars.length;
        }
    }

    protected int position(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) {
                return i;
            }
        }
        return unknownSymbol;
    }

    protected float left(int position){
        return startX + dX * (position % countInRow);
    }

    protected float up(int position){
        return startY + dY * ( position / countInRow);
    }

    protected float bottom(int position){
        return up(position)+letterHeight;
    }

    protected float right(int position){
        return left(position)+ letterWidth;
    }

    public static FontTexture load(Typeface typeface, int textureSize, float fontSize, String symbols) {
        char[] cc = symbols.toCharArray();
        int[] texId = new int[1];
        FontTexture result = new FontTexture(texId, cc);

        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setTypeface(typeface);
        paint.setTextSize(fontSize);

        Rect bounds = new Rect();

        paint.getTextBounds(cc, 0, cc.length, bounds);

        final int h = bounds.height();
        final float d = 8;
        float x = d;
        float y = d - bounds.top;

        int wd = (int) (d + bounds.width() / cc.length);
        int n = textureSize / wd;

        float mm = 1f/ textureSize;
        result.dX = wd * mm;
        result.dY = (h+d) * mm;
        result.letterWidth = mm * bounds.width() / cc.length ;
        result.letterHeight = bounds.height() * mm;
        result.countInRow = n;
        result.startX = x * mm;
        result.startY = (d) * mm;

        for (int i = 0, pos = 0; pos < cc.length; pos++, i++) {
            if (i == n) {
                i = 0;
                y += h + d;
                x = d;
            }
            canvas.drawText(cc, pos, 1, x, y, paint);
            x += wd;
        }

        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);

        TextureLoader.setParameters(GL_NEAREST_MIPMAP_LINEAR, GL_LINEAR,
                GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);

        int format = GLUtils.getInternalFormat(bitmap);
        int type = GLUtils.getType(bitmap);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, format, bitmap, type, 0);

        glGenerateMipmap(GL_TEXTURE_2D);

        return result;
    }
}
