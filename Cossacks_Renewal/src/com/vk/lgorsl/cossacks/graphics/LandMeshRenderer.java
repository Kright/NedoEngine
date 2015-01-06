package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Renders land mesh
 * Created by lgor on 21.12.2014.
 */
public class LandMeshRenderer implements GameRenderSystem {

    private CleverShader shadowShader;

    private FloatBuffer fb;  //vertices
    private FloatBuffer fbn; //normals
    private ShortBuffer sb;

    private Texture2D grass;

    @Override
    public boolean load(RendererParams params) {
        shadowShader = params.loadShader(R.raw.shader_land_tex_depth_render);

        grass = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.grass),
                GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST, GL_REPEAT, GL_REPEAT, true);
        createGrid(params);

        params.landMeshRenderer = this;
        return true;
    }

    private void createGrid(RendererParams params) {
        HeightGrid grid = params.world.heightGrid;
        float meterSize = params.world.metrics.meterSize();

        fb = generateVertices(grid, meterSize);
        sb = generateIndices(grid);
        fbn = generateNormals(meterSize, grid);
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
        shadowShader.useProgram();

        glUniformMatrix4fv(shadowShader.get("uMatrix"), 1, false,
                params.mapView.projection().getArray(), 0);

        float amb = 0.5f;
        glUniform3f(shadowShader.get("uAmbient"), 0.5f * amb, amb, 1.5f * amb);
        float dif = 0.6f;
        glUniform3f(shadowShader.get("uDiffuse"), 2.0f * dif, 1.4f * dif, 0.6f*dif);

        grass.use(0);
        glUniform1i(shadowShader.get("uTexture"), 0);

        glUniform1f(shadowShader.get("uTextureScale"), 0.001f);

        glUniformMatrix4fv(shadowShader.get("uMatrixShadow"), 1, false, params.lightView.anotherProjection().getArray(), 0);

        glUniform1f(shadowShader.get("uEps"), params.settings.shadowsEps);

        params.depthTexture.use(1);
        glUniform1i(shadowShader.get("uDepthMap"), 1);

        Vect3f dir = params.lightView.viewDirection();
        glUniform3f(shadowShader.get("uLightDirection"), dir.x, dir.y, dir.z);

        shadowShader.enableAllVertexAttribArray();
        glVertexAttribPointer(shadowShader.get("aPosition"), 3, GL_FLOAT, false, 0, fb);
        glVertexAttribPointer(shadowShader.get("aNormal"), 3, GL_FLOAT, false, 0, fbn);

        glDrawElements(GL_TRIANGLES, sb.capacity(), GL_UNSIGNED_SHORT, sb);

        shadowShader.disableAllVertexAttribArray();
    }

    @Override
    public void renderShadows(RendererParams params) {
        CleverShader shaderDepthDraw = params.lightRenderer.shaderDepthDraw;

        shaderDepthDraw.useProgram();
        shaderDepthDraw.get("uMatrix");
        glUniformMatrix4fv(shaderDepthDraw.get("uMatrix"), 1, false,
                params.lightView.projection().getArray(), 0);

        shaderDepthDraw.enableAllVertexAttribArray();
        glVertexAttribPointer(shaderDepthDraw.get("aPosition"), 3, GL_FLOAT, false,
                0, fb);

        glDrawElements(GL_TRIANGLES, sb.capacity(), GL_UNSIGNED_SHORT, sb);
        shaderDepthDraw.disableAllVertexAttribArray();
    }
}