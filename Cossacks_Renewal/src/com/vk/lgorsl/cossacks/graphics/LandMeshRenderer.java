package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Renders land mesh
 * Created by lgor on 21.12.2014.
 */
public class LandMeshRenderer implements GameRenderable {

    private CleverShader shadowShader;

    private FloatBuffer fb;  //vertices
    private FloatBuffer fbn; //normals
    private ShortBuffer sb;

    private float uEps = 0.001f;

    @Override
    public boolean load(RendererParams params) {
        shadowShader = new CleverShader(params.resources, R.raw.shader_land_renderer);
        //shadowShader = new CleverShader(params.resources, R.raw.shader_land_debug);
        createGrid(params);
        return true;
    }

    private void createGrid(RendererParams params) {
        HeightGrid grid = params.world.heightGrid;
        float meterSize = params.world.metrics.meterSize();

        fb = generateVertices(grid, meterSize);
        sb = generateIndices(grid);
        fbn = generateNormals(meterSize, grid);

        params.meshVertices = fb;
        params.meshIndices = sb;
    }

    private FloatBuffer generateVertices(HeightGrid grid, float scale){
        float[] f = new float[grid.data.length * 3];
        for (int i = 0; i < grid.data.length; i++) {
            int x = i % grid.width;
            int y = i / grid.width;
            f[3 * i] = scale * (x + (y % 2 == 1 ? 0.5f : 0f));
            f[3 * i + 1] = scale * y;
            f[3 * i + 2] = grid.data[i];
        }
        return  GLHelper.make(f);
    }

    private ShortBuffer generateIndices(HeightGrid grid) {
        short[] s = new short[(grid.height - 1) * (grid.width - 1) * 2 * 3];
        int pos = 0;
        for (int y = 0; y < grid.height - 1; y++) {
            for (int x = 0; x < grid.width - 1; x++) {
                int num = x + y * grid.width;
                if (y % 2 == 0) {
                    s[pos++] = (short) (num);
                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width);

                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width + 1);
                    s[pos++] = (short) (num + grid.width);
                } else {
                    s[pos++] = (short) num;
                    s[pos++] = (short) (num + grid.width + 1);
                    s[pos++] = (short) (num + grid.width);

                    s[pos++] = (short) (num);
                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width + 1);
                }
            }
        }
        return GLHelper.make(s);
    }

    private FloatBuffer generateNormals(float meterSize, HeightGrid grid) {
        float[] fn = new float[grid.data.length * 3];
        Vect3f normal = new Vect3f();
        Vect3f add = new Vect3f();
        for (int i = 0; i < grid.data.length; i++) {
            int x = i % grid.width;
            int y = i / grid.width;
            normal.set(0, 0, 0);

            int d = get(grid, x - 1, y) - get(grid, x + 1, y);
            add.set(d, 0, meterSize).normalize();
            normal.add(add);

            if (y % 2 == 0) {
                d = get(grid, x - 1, y - 1) - get(grid, x, y + 1) + get(grid, x, y - 1) - get(grid, x - 1, y + 1);
            } else {
                d = get(grid, x, y - 1) - get(grid, x + 1, y + 1) + get(grid, x + 1, y - 1) - get(grid, x, y + 1);
            }
            add.set(0, d, meterSize * 2).normalize();
            normal.add(add);
            normal.normalize();
            normal.putIntoArray(fn, i * 3);
        }
        return GLHelper.make(fn);
    }

    private int get(HeightGrid grid, int x, int y) {
        if (x < 0) x++;
        if (y < 0) y++;
        if (x >= grid.width) x = grid.width - 1;
        if (y >= grid.height) y = grid.height - 1;
        return grid.data[x + y * grid.width];
    }

    @Override
    public void render(RendererParams params) {
        glClearColor(0, 0, 0, 0);
        glClearDepthf(1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shadowShader.useProgram();

        glUniformMatrix4fv(shadowShader.get("uMatrix"), 1, false,
                params.mapView.projection().getArray(), 0);
        glUniformMatrix4fv(shadowShader.get("uMatrixShadow"), 1, false,
                params.lightningView.projection().getArray(), 0);

        glUniform4f(shadowShader.get("uColorAmbient"), 36/255f, 74/255f, 30/255f, 1f);
        glUniform4f(shadowShader.get("uColorDiffuse"), 30/255f, 90/255f, 5/255f, 1f);
        glUniform1f(shadowShader.get("uEps"), uEps);

        params.depthTexture.use(0);
        glUniform1i(shadowShader.get("uDepthMap"), 0);

        Vect3f dir = new Vect3f();
        params.lightningView.getViewDirection(dir);
        glUniform3f(shadowShader.get("uLightDirection"), dir.x, dir.y, dir.z);

        GLHelper.checkError();
        shadowShader.enableAllVertexAttribArray();
        glVertexAttribPointer(shadowShader.get("aPosition"), 3, GL_FLOAT, false, 0, fb);
        glVertexAttribPointer(shadowShader.get("aNormal"), 3, GL_FLOAT, false, 0, fbn);

        glDrawElements(GL_TRIANGLES, sb.capacity(), GL_UNSIGNED_SHORT, sb);

        shadowShader.disableAllVertexAttribArray();
    }
}