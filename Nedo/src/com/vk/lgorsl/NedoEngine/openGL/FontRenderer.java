package com.vk.lgorsl.NedoEngine.openGL;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * draws immutable text string on screen
 * Created by lgor on 29.11.2014.
 */
public class FontRenderer implements Renderable<Void, Matrix4_4f> {

    private static CleverShader shader;
    private static ShortBuffer table;   //таблица с индексами одна на всех
    private static final int maxLettersCount = 4096;

    private final FontTexture texture;
    private float r, g, b, a;

    private FloatBuffer fb;
    private int stringLength;

    public float letterW = 1.0f, letterH = 1.0f;

    public FontRenderer(FontTexture texture) {
        this(texture, 256);
    }

    public FontRenderer(FontTexture texture, int initialCapacity) {
        this.texture = texture;
        r = g = b = 0;
        a = 1f;
        fb = GLHelper.make(new float[4 * 4 * initialCapacity]);
        load(null);
    }

    public void setColor(float red, float green, float blue, float alpha, boolean normalized) {
        float m = normalized ? 1 : 1f / 256;
        r = red * m;
        g = green * m;
        b = blue * m;
        a = alpha * m;
    }

    public void setText(String text) {
        if (fb.capacity() * 4 * 4 < text.length()) {
            fb = ByteBuffer.allocateDirect(text.length() * 4 * 4).
                    order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        fb.position(0);
        char[] cc = text.toCharArray();

        int x = 0, y = -1;
        for (char c : cc) {
            if (c == '\n') {
                y--;
                x = 0;
                continue;
            }
            int pos = texture.position(c);

            fb.put(x);
            fb.put(y);
            fb.put(texture.left(pos));
            fb.put(texture.bottom(pos));

            fb.put(x);
            fb.put(y + letterH);
            fb.put(texture.left(pos));
            fb.put(texture.up(pos));

            fb.put(x + letterW);
            fb.put(y + letterH);
            fb.put(texture.right(pos));
            fb.put(texture.up(pos));

            fb.put(x + letterW);
            fb.put(y);
            fb.put(texture.right(pos));
            fb.put(texture.bottom(pos));

            x++;
        }
        fb.position(0);
        stringLength = text.length();
    }

    @Override
    public void load(Void nothing) {
        if (shader == null) {
            shader = new CleverShader(vertexShader, fragmentShader);
            short[] indices = new short[6 * maxLettersCount];
            for (int i = 0; i < maxLettersCount; i++) {
                indices[6 * i] = (short)(i*4);
                indices[6 * i + 1] = (short)(i*4+1);
                indices[6 * i + 2] = (short)(i*4+2);
                indices[6 * i + 3] = (short)(i*4);
                indices[6 * i + 4] = (short)(i*4+2);
                indices[6 * i + 5] = (short)(i*4+3);
            }
            table = GLHelper.make(indices);
        }
    }

    /**
     * about drawing space :
     * first row placed with y = 0 , second with y = 1, next = 2, etc;
     * width of any letter space is 1.0 too
     * @param matrix4_4f defines transformations to the screen-space coordinates
     */
    @Override
    public void render(Matrix4_4f matrix4_4f) {
        shader.useProgram();
        shader.enableAllVertexAttribArray();

        glUniform4f(shader.get("uColor"), r, g, b, a);
        texture.useAndSetUniform1i(shader.get("uTexture"), 0);
        glUniformMatrix4fv(shader.get("uMatrix"), 1, false, matrix4_4f.getArray(), 0);

        fb.position(0);
        glVertexAttribPointer(shader.get("aScreenTexPos"), 4, GL_FLOAT, false, 4 * 4, fb);

        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDrawElements(GL_TRIANGLES, stringLength * 6, GL_UNSIGNED_SHORT, table);

        shader.disableAllVertexAttribArray();
    }

    private final static String vertexShader = "uniform mat4 uMatrix;\n" +
            "attribute vec4 aScreenTexPos;\n" +
            "varying vec2 vTexPos;\n" +
            "void main(){\n" +
            "    gl_Position = uMatrix*vec4(aScreenTexPos.xy, 0.0, 1.0);\n" +
            "    vTexPos = aScreenTexPos.zw;\n" +
            "}\n";

    private final static String fragmentShader =
                    "uniform sampler2D uTexture;\n" +
                    "uniform vec4 uColor;\n" +
                    "varying vec2 vTexPos;\n" +
                    "void main(){\n" +
                    "    gl_FragColor = uColor * vec4(1.0, 1.0, 1.0, texture2D(uTexture, vTexPos).a);\n" +
                    "}";
}
