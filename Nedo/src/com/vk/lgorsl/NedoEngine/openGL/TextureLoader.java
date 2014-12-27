package com.vk.lgorsl.NedoEngine.openGL;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import static android.opengl.GLES20.*;

/**
 * has universal static method
 *
 * and factory for loading many textures
 *
 * Created by lgor on 20.11.2014.
 */
public class TextureLoader {

   /*
    *     Текстурные параметры можно изменить в любой момент времени, а не только при инициализации.
    *     По умолчанию параметры установлены в GL_REPEAT и GL_LINEAR.
    */

    public boolean generateMipmaps = false;

    /**
     * фильтр текстур при уменьшении размера текстуры от исходного
     */
    public int minFilter = GL_LINEAR;

    /**
     * фильтр при увеличении размера от исходного
     */
    public int magFilter = GL_LINEAR;

    /*
     * GL_CLAMP_TO_EDGE
     * GL_REPEAT
     */

    /**
     * поведение при текстурных координатах больших размера текстуры по ширине.
     */
    public int wrapS = GL_REPEAT;

    /**
     * поведение при текстурных координатах больших размера текстуры по высоте.
     */
    public int wrapT = GL_REPEAT;

    public TextureLoader(){
        //empty
    }

    /**
     * загружает текстуру
     * внутри вызывается Bitmap.recycle()!
     *
     * @param bm - Bitmap with picture
     * @return loaded Texture2D
     */
    public Texture2D loadtexture(Bitmap bm) {
        return loadTexture(bm, minFilter, magFilter, wrapS, wrapT, generateMipmaps);
    }

    public void setParameters(){
        setParameters(minFilter, magFilter, wrapS, wrapT);
    }

    public static Texture2D loadTexture(Bitmap bm, int minFilter, int magFilter, int wrapS, int wrapT, boolean genMipmaps){
        int[] texId = new int[1];
        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);

        int format = GLUtils.getInternalFormat(bm);
        int type = GLUtils.getType(bm);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, format, bm, type, 0);

        setParameters(minFilter, magFilter, wrapS, wrapT);

        if (genMipmaps) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        bm.recycle();

        return new Texture2D(texId);
    }

    public static Texture2D genAndBindTexture(){
        int[] texId = new int[1];
        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);
        return new Texture2D(texId);
    }

    /**
     * https://www.khronos.org/opengles/sdk/docs/man/xhtml/glTexParameter.xml
     *
     * @param minFilter :
     *                  GL_NEAREST,
     *                  GL_LINEAR,
     *                  GL_NEAREST_MIPMAP_NEAREST,
     *                  GL_LINEAR_MIPMAP_NEAREST,
     *                  GL_NEAREST_MIPMAP_LINEAR,
     *                  GL_LINEAR_MIPMAP_LINEAR
     * @param magFilter
     *                  GL_NEAREST,
     *                  GL_LINEAR
     * @param wrapS
     *                  GL_CLAMP_TO_EDGE,
     *                  GL_MIRRORED_REPEAT,
     *                  GL_REPEAT
     * @param wrapT
     *                  GL_CLAMP_TO_EDGE,
     *                  GL_MIRRORED_REPEAT,
     *                  GL_REPEAT
     */
    public static void setParameters(int minFilter, int magFilter, int wrapS, int wrapT){
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
    }


}
