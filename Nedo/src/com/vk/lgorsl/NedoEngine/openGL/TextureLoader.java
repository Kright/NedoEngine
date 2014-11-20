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
         *
         * GL_NEAREST – нет фильтрации, нет мипмапов
         * GL_LINEAR - фильтрация, нет мипмапов
         * GL_NEAREST_MIPMAP_NEAREST – нет фильтрации, выбор ближайшего уровня мипмап
         * GL_NEAREST_MIPMAP_LINEAR – нет фильтрации, фильтрация между уровнями мипмап
         * GL_LINEAR_MIPMAP_NEAREST - фильтрация, выбор ближайшего уровня мипмап
         * GL_LINEAR_MIPMAP_LINEAR - filtering, фильтрация между уровнями мипмап
         *
         * GL_LINEAR - билинейная
         * GL_LINEAR_MIPMAP_NEAREST - билинейная с мипмапами
         * GL_LINEAR_MIPMAP_LINEAR – трилинейная
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
     * @param Bitmap
     * @return loaded Texture2D
     */
    public Texture2D loadtexture(Bitmap bm) {
        return loadtexture(bm, minFilter, magFilter, wrapS, wrapT, generateMipmaps);
    }

    public void setParameters(){
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
    }

    public static Texture2D loadtexture(Bitmap bm, int minFilter, int magFilter, int wrapS, int wrapT, boolean genMipmaps){
        int[] texId = new int[1];
        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);

        int format = GLUtils.getInternalFormat(bm);
        int type = GLUtils.getType(bm);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, format, bm, type, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);

        if (genMipmaps) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        bm.recycle();

        return new Texture2D(texId);
    }
}
