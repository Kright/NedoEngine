package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Draws HeightGrid on screen
 *
 * Created by lgor on 16.12.2014.
 */
public class LandMeshGridRenderer implements GameRenderable {

    private CleverShader shader;
    boolean loaded = false;
    public FloatBuffer fb;
    public ShortBuffer sb;

    @Override
    public void load(LoadedData loadedData) {
        if (!loaded) {

            shader = new CleverShader(
                    "uniform mat4 uMatrix;" +
                            "attribute vec3 aPosition;" +
                            "void main(){" +
                            "   gl_Position = uMatrix * vec4(aPosition.xyz, 1.0);" +
                            "}",
                            "uniform vec4 uColor;" +
                            "void main(){" +
                            "   gl_FragColor = uColor;" +
                            "}");

            loaded = true;
        }
    }

    private void createGrid(RendererParams params) {
        HeightGrid grid = params.world.heightGrid;
        float scale = params.world.metrics.meterSize();
        float meterSize = params.world.metrics.meterSize();

        float[] f = new float[grid.data.length * 3];
        for (int i = 0; i < grid.data.length; i++) {
            int x = i % grid.width;
            int y = i / grid.width;
            f[3 * i] = scale* (x + (y % 2 == 1 ? 0.5f : 0f));
            f[3 * i + 1] = scale * y;
            f[3 * i + 2] = scale * grid.data[i]/meterSize;
        }
        fb = GLHelper.make(f);

        short[] s = new short[(grid.height - 1) * (grid.width - 1) * 2 * 3];
        int pos = 0;
        for (int y = 0; y < grid.height - 1; y++) {
            for (int x = 0; x < grid.width - 1; x++) {
                int num = x + y * grid.width;
                s[pos++] = (short) num;
                s[pos++] = (short) (num + 1);

                s[pos++] = (short) num;
                s[pos++] = (short) (num + grid.width);

                if (y % 2 == 0) {
                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width);
                } else {
                    s[pos++] = (short) num;
                    s[pos++] = (short) (num + grid.width + 1);
                }
            }
        }
        sb = GLHelper.make(s);
    }

    @Override
    public void render(RendererParams params) {
        //crazy code, I will fix it
        load(null);
        if (sb == null) {
            createGrid(params);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.useProgram();
        shader.get("uMatrix");
        glUniformMatrix4fv(shader.get("uMatrix"), 1, false,
                params.mapView.projection().getArray(), 0);

        glUniform4f(shader.get("uColor"), 0.5f, 0, 0.5f, 0.4f);

        shader.enableAllVertexAttribArray();
        glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false,
                0, fb);

        glDrawElements(GL_LINES, sb.capacity(), GL_UNSIGNED_SHORT, sb);

        shader.disableAllVertexAttribArray();
    }
}
