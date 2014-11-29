package com.vk.lgorsl.NedoEngine.openGL;

import static android.opengl.GLES20.*;

/**
 * simple texture wrapper
 *
 * Created by lgor on 20.11.2014.
 */
public class Texture2D {

    private final int[] id;

    public Texture2D(int[] id){
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

    /**
     * @param uniformLocation - location of uniform for this matrix
     * @param slotNumber - number of used texture unit
     */
    public void useAndSetUniform1i(int uniformLocation, int slotNumber){
        active(slotNumber);
        bind();
        glUniform1i(uniformLocation, slotNumber);
    }

    /**
     * calls glActiveTexture(GL_TEXTURE0 + num)
     * @param num number if texture unit
     */
    public static void active(int num){
        glActiveTexture(GL_TEXTURE0 + num);
    }
}
