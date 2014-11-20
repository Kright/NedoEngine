package com.vk.lgorsl.NedoEngine.openGL;

import static android.opengl.GLES20.*;

/**
 * simple texture wrapper
 *
 * Created by lgor on 20.11.2014.
 */
public class Texture2D {

    private final int[] id;

    protected Texture2D(int[] id){
        this.id = id;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, id[0]);
    }

    public int id(){
        return id[0];
    }

    public void delete(){
        glDeleteTextures(1, id, 0);
    }

    public void use(int texId){
        active(texId);
        bind();
    }

    public static void active(int num){
        glActiveTexture(GL_TEXTURE0 + num);
    }
}
