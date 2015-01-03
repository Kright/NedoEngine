package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.*;

/**
 * new landscape renderer
 *
 * Created by lgor on 03.01.2015.
 */
public class LandscapeRenderer implements GameRenderSystem {

    private Texture2D grass;
    private CleverShader shader;
    private ArrayList<Quad> cache = new ArrayList<>();
    private final int quadSize = 64 << 5;
    private final int maxCacheSize = 128;

    private ShortBuffer indices;

    private static class Quad {
        final Rectangle2i area = new Rectangle2i();
        FloatBuffer verticesWithNormals;
        int timesNotUsed;

        void set(iRectangle2i area, iLandscapeMap map, int parts) {
            parts++;
            this.area.set(area);
            float[] data = new float[parts * parts * 6];
            int pos = 0;
            Point2i p = new Point2i();
            for (int i = 0; i < parts; i++) {
                for (int j = 0; j < parts; j++) {
                    p.set(area.xMin() + i * area.width() / parts, area.yMin() + j * area.height() / parts);
                    data[pos++] = p.x;
                    data[pos++] = p.y;
                    data[pos++] = map.getHeight(p);
                    //TODO normals calculation
                    data[pos++] = 0;
                    data[pos++] = 0;
                    data[pos++] = 1;
                }
            }
            verticesWithNormals = GLHelper.make(data);
            timesNotUsed = 0;
        }
    }

    private ArrayList<iRectangle2i> areas = new ArrayList<>();

    @Override
    public boolean load(RendererParams params) {
        shader = new CleverShader(params.resources, R.raw.shader_land_tex_depth_render);
        indices = generateIndices(32);
        grass = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.grass),
                GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST, GL_REPEAT, GL_REPEAT, true);

        iRectangle2i big = params.world.map.bounds();
        for(int i=0; i<8; i++){
            for(int j=0; j<8 ; j++){
                Rectangle2i r = new Rectangle2i();
                int dx = i*big.width() / 8 + big.xMin();
                int dy = j*big.height() / 8 + big.yMin();
                r.set(dx, dy, dx+quadSize, dy+quadSize);
                areas.add(r);
            }
        }

        return true;
    }

    @Override
    public void render(RendererParams params) {
        if (params.clock.framesCount() % 50 == 0) {
            NedoLog.log("cache size == " + cache.size());
        }
        update();
        shader.useProgram();

        glUniformMatrix4fv(shader.get("uMatrix"), 1, false, params.mapView.projection().getArray(), 0);
        glUniformMatrix4fv(shader.get("uMatrixShadow"), 1, false, params.lightningView.anotherProjection().getArray(), 0);

        float amb = 0.5f;
        glUniform3f(shader.get("uAmbient"), 0.5f * amb, amb, 1.5f * amb);
        float dif = 0.6f;
        glUniform3f(shader.get("uDiffuse"), 2.0f * dif, 1.4f * dif, 0.6f*dif);

        grass.use(0);
        glUniform1i(shader.get("uTexture"), 0);

        glUniform1f(shader.get("uTextureScale"), 0.001f);

        glUniform1f(shader.get("uEps"), params.settings.shadowsEps);

        params.depthTexture.use(1);
        glUniform1i(shader.get("uDepthMap"), 1);

        Vect3f dir = new Vect3f();
        params.lightningView.getViewDirection(dir);
        glUniform3f(shader.get("uLightDirection"), dir.x, dir.y, dir.z);

        shader.enableAllVertexAttribArray();

        Rectangle2i rect = new Rectangle2i();
        params.mapView.viewBounds().getAABB(rect);
        renderQuads(params, rect, true);

        shader.disableAllVertexAttribArray();
    }

    @Override
    public void renderShadows(RendererParams params) {
        CleverShader shader = params.lightRenderer.shaderDepthDraw;
        shader.useProgram();
        shader.enableAllVertexAttribArray();

        glUniformMatrix4fv(shader.get("uMatrix"), 1, false,
                params.lightningView.projection().getArray(), 0);

        Rectangle2i rect = new Rectangle2i();
        params.lightningView.viewBounds().getAABB(rect);
        renderQuads(params, rect, false);

        shader.disableAllVertexAttribArray();
    }

    private void renderQuads(RendererParams params, iRectangle2i mapArea, boolean useNormals){
        int count = 0;
        for(iRectangle2i rect: areas){
            if (mapArea.intersects(rect)){
                Quad q = get(rect, params.world.map);
                if (q!=null) {
                    count++;
                    q.verticesWithNormals.position(0);
                    glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false, 6 * 4, q.verticesWithNormals);
                    if (useNormals) {
                        q.verticesWithNormals.position(3);
                        glVertexAttribPointer(shader.get("aNormal"), 3, GL_FLOAT, false, 6 * 4, q.verticesWithNormals);
                    }
                    glDrawElements(GL_TRIANGLES, indices.capacity(), GL_UNSIGNED_SHORT, indices);
                }
            }
        }
        //todo remove it
        if (params.clock.framesCount() % 234==0) {
            NedoLog.log("quads was drawed : " + count);
        }
    }

    private void update(){
        for(Quad q: cache){
            q.timesNotUsed++;
        }
    }

    private Quad get(iRectangle2i rect, iLandscapeMap map){
        for(Quad q: cache){
            if (q.area.equals(rect)){
                q.timesNotUsed = 0;
                return q;
            }
        }
        //if not found
        Quad q = getFreeQuad();
        if (q!=null) {
            q.set(rect, map, 32);
        }
        return q;
    }

    private Quad getFreeQuad(){
        for(Quad q: cache){
            if (q.timesNotUsed>3){
                return q;
            }
        }
        Quad q = null;
        if (cache.size()<maxCacheSize) {
            q = new Quad();
            q.timesNotUsed = 4;
            cache.add(q);
        }
        return q;
    }

    private ShortBuffer generateIndices(int size) {
        short[] indices = new short[size * size * 6];
        int pos = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                short p1 = (short) (i + j * (size + 1));
                short p2 = (short) (p1 + 1);
                short p3 = (short) (i + (j + 1) * (size + 1));
                short p4 = (short) (p3 + 1);
                indices[pos++] = p1;
                indices[pos++] = p3;
                indices[pos++] = p2;
                indices[pos++] = p2;
                indices[pos++] = p3;
                indices[pos++] = p4;
            }
        }
        return GLHelper.make(indices);
    }

}
