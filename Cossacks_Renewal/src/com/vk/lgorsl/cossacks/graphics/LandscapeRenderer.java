package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.interfaces.ViewBounds;

import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * new landscape renderer
 * <p>
 * Created by lgor on 03.01.2015.
 */
public class LandscapeRenderer implements GameRenderSystem {

    private Texture2D grass;
    private CleverShader shader, shaderDepth;

    private LandscapeChunk[][] land;
    private int width, height;

    private final int cellSize, chunkSize;

    public LandscapeRenderer(int cellSize, int chunkSize) {
        this.cellSize = cellSize;
        this.chunkSize = chunkSize;
    }

    @Override
    public boolean load(RendererParams params) {
        iRectangle2i mapSize = params.world.metrics.mapSize();

        width = mapSize.width() / chunkSize;
        if ((mapSize.width()) % chunkSize != 0) width++;
        height = mapSize.height() / chunkSize;
        if ((mapSize.height()) % chunkSize != 0) height++;

        land = new LandscapeChunk[width][height];

        ShortBuffer indices = null;
        Rectangle2i rectangle = new Rectangle2i();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                land[i][j] = new LandscapeChunk(cellSize);
                int x = mapSize.xMin() + chunkSize * i;
                int y = mapSize.yMin() + chunkSize * j;
                if (indices == null) {
                    land[i][j].set(rectangle.set(x, y, x + chunkSize, y + chunkSize), params.world.map, true);
                    indices = land[i][j].indices;
                } else {
                    land[i][j].set(rectangle.set(x, y, x + chunkSize, y + chunkSize), params.world.map, false);
                    land[i][j].indices = indices;
                }
            }
        }

        grass = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.grass),
                params.settings.landFilterMin, params.settings.landFilterMag, GL_REPEAT, GL_REPEAT, true);

        shader = params.loadShader(R.raw.shader_land_tex_depth_render);
        shaderDepth = params.loadShader(R.raw.shader_light_depth);

        params.landscapeRenderer = this;
        return true;
    }

    @Override
    public void render(RendererParams params) {
        shader.useProgram();

        glUniformMatrix4fv(shader.get("uMatrix"), 1, false, params.mapView.projection().getArray(), 0);
        glUniformMatrix4fv(shader.get("uMatrixShadow"), 1, false, params.lightView.anotherProjection().getArray(), 0);

        float amb = 0.5f;
        glUniform3f(shader.get("uAmbient"), 0.5f * amb, amb, 1.5f * amb);
        float dif = 0.6f;
        glUniform3f(shader.get("uDiffuse"), 2.0f * dif, 1.4f * dif, 0.6f * dif);

        grass.use(0);
        glUniform1i(shader.get("uTexture"), 0);

        glUniform1f(shader.get("uTextureScale"), 0.001f);

        glUniform1f(shader.get("uEps"), params.settings.shadowsEps);

        params.depthTexture.use(1);
        glUniform1i(shader.get("uDepthMap"), 1);

        Vect3f dir = params.lightView.viewDirection();
        glUniform3f(shader.get("uLightDirection"), dir.x, dir.y, dir.z);

        shader.enableAllVertexAttribArray();

        renderQuads(shader, params.mapView.viewBounds(), true);

        shader.disableAllVertexAttribArray();
    }

    private void renderQuads(CleverShader shader, ViewBounds bounds, boolean useNormals) {
        LandscapeChunk chunk;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                chunk = land[i][j];
                if (bounds.intersects(chunk.area)) {
                    glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false, 0, chunk.vertices);
                    if (useNormals) {
                        glVertexAttribPointer(shader.get("aNormal"), 3, GL_FLOAT, false, 0, chunk.normals);
                    }
                    glDrawElements(GL_TRIANGLES, chunk.indices.capacity(), GL_UNSIGNED_SHORT, chunk.indices);
                }
            }
        }
    }

    public int getUsedMemory() {
        ShortBuffer indices = land[0][0].indices;
        int memory = 2 * indices.capacity();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                LandscapeChunk chunk = land[i][j];
                memory += 4 * chunk.vertices.capacity();
                memory += 4 * chunk.vertices.capacity();
                if (chunk.indices != indices) {
                    memory += 2 * chunk.indices.capacity();
                }
            }
        }
        return memory;
    }

    @Override
    public void renderShadows(RendererParams params) {
        shaderDepth.useProgram();
        shaderDepth.enableAllVertexAttribArray();

        glUniformMatrix4fv(shaderDepth.get("uMatrix"), 1, false,
                params.lightView.projection().getArray(), 0);

        //bounds from mapView is a feature;
        renderQuads(shaderDepth, params.mapView.viewBounds(), false);

        shaderDepth.disableAllVertexAttribArray();
    }
}
