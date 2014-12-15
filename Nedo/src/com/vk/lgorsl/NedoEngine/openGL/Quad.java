package com.vk.lgorsl.NedoEngine.openGL;

import android.annotation.TargetApi;

import static android.opengl.GLES20.*;

import android.content.res.Resources;
import android.os.Build;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;

import java.nio.FloatBuffer;

/**
 * Simple squad
 * Class for debugging
 *
 * Created by lgor on 29.11.2014.
 */
public class Quad implements Renderable<Matrix4_4f> {

    private static final Object monitor = new Object();

    protected static FloatBuffer fb;
    protected static CleverShader shader;

    private final String vertexCode =
            "uniform mat4 uCamera;\n" +
                    "attribute vec3 aScreenPos;\n" +
                    "attribute vec2 aTexturePos;\n" +
                    "varying vec2 vTexPos;\n" +
                    "void main(){\n" +
                    "    gl_Position = uCamera*vec4(aScreenPos.xyz, 1.0);\n" +
                    "    vTexPos = aTexturePos;\n" +
                    "}\n";

    private final String fragmentCode =
            "uniform sampler2D uTexture;\n" +
                    "varying vec2 vTexPos;\n" +
                    "void main(){\n" +
                    "    gl_FragColor = texture2D(uTexture, vTexPos);\n" +
                    "}\n";

    protected Texture2D texture;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public Quad(Texture2D texture) {
        this.texture = texture;
        load(null);
    }

    public void setTexture(Texture2D texture){
        this.texture = texture;
    }

    @Override
    public void load(Resources resources) {
        synchronized (monitor) {
            if (fb == null) {
                fb = GLHelper.make(new float[]{
                        -1, 1, 0, 0, 0,
                        1, 1, 0, 1, 0,
                        -1, -1, 0, 0, 1,
                        1, -1, 0, 1, 1});
                shader = new CleverShader(vertexCode, fragmentCode);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public void render(Matrix4_4f matrix4_4f) {
        shader.useProgram();

        texture.useAndSetUniform1i(shader.get("uTexture"), 0);
        glUniformMatrix4fv(shader.get("uCamera"), 1, false, matrix4_4f.getArray(), 0);

        shader.enableAllVertexAttribArray();

        fb.position(0);
        glVertexAttribPointer(shader.get("aScreenPos"), 3, GL_FLOAT, false, 4*5, fb);
        fb.position(3);
        glVertexAttribPointer(shader.get("aTexturePos"), 2, GL_FLOAT, false, 4*5, fb);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        shader.disableAllVertexAttribArray();
    }


}
