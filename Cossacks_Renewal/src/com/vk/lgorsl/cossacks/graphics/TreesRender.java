package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.ViewBounds;
import com.vk.lgorsl.cossacks.world.interfaces.iProjection;
import com.vk.lgorsl.cossacks.world.interfaces.iTree;

import static android.opengl.GLES20.*;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * it renders trees :)
 * <p>
 * Created by lgor on 27.12.2014.
 */
public class TreesRender implements GameRenderSystem {

    private Texture2D texture;

    private CleverShader simpleShader, shadowShader;

    private FloatBuffer fb;
    private int maxTreesCount = 1024;
    private final float[] arr = new float[maxTreesCount * 20];
    private ShortBuffer sb;

    private class TreesParams {
        float txLeft, txRight, tyUp, tyDown;
    }

    private TreesParams[] treesParams;

    @Override
    public boolean load(RendererParams params) {
        fb = GLHelper.make(new float[maxTreesCount * 4 * (3 + 2)]);

        sb = GLHelper.make(new short[maxTreesCount * 6]);
        for (int i = 0; i < maxTreesCount; i++) {
            int n = 4 * i;
            sb.put((short) n);
            sb.put((short) (n + 1));
            sb.put((short) (n + 2));
            sb.put((short) n);
            sb.put((short) (n + 2));
            sb.put((short) (n + 3));
        }
        sb.position(0);

        texture = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.trees2),
                GL_NEAREST_MIPMAP_NEAREST, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, true);

        treesParams = new TreesParams[4];
        for (int i = 0; i < treesParams.length; i++) {
            treesParams[i] = new TreesParams();
            treesParams[i].txLeft = 1f / 8 * i;
            treesParams[i].txRight = 1f / 8 + 1f / 8 * i;
            treesParams[i].tyDown = 1 / 4f;
            treesParams[i].tyUp = 0f;
        }

        simpleShader = params.loadShader(R.raw.shader_simple);
        shadowShader = params.loadShader(R.raw.shader_depth_discard);

        params.treesRender = this;

        return true;
    }

    @Override
    public void render(RendererParams params) {
        render(params, params.mapView, simpleShader);
    }

    @Override
    public void renderShadows(RendererParams params) {
        render(params, params.lightView, shadowShader);
    }

    public void render(RendererParams params, iProjection view, CleverShader shader) {
        float[] arr = view.projection().getArray();
        Vect3f dx = new Vect3f().set(-arr[5], arr[1], 0);
        dx.setLength(0.2f);
        Vect3f dh = new Vect3f().set(0, 0, 1);

        shader.useProgram();

        texture.use(0);
        glUniform1i(shader.get("uTexture"), 0);
        glUniformMatrix4fv(shader.get("uMatrix"), 1, false, view.projection().getArray(), 0);

        shader.enableAllVertexAttribArray();

        renderTrees(dx, dh, params.world, view.viewBounds(), shader.get("aPosition"), shader.get("aTexCoord"));

        shader.disableAllVertexAttribArray();
    }

    private final Rectangle2i aabb = new Rectangle2i(0, 0, 0, 0);

    private void renderTrees(Vect3f dx, Vect3f dh, WorldInstance world, ViewBounds boundingBox, int aPosition, int aTexCoord){
        fb.position(0);
        int pos = 0;

        boundingBox.getAABB(aabb);
        for (iTree tree : world.trees.objects(aabb)) {
            if (!tree.alive()) {
                continue;
            }
            float x = tree.x();
            float y = tree.y();
            float z = world.map.getHeight(tree);

            TreesParams treesP = treesParams[tree.type()];
            float treeSize = tree.size();

            arr[pos++] = x - dx.x * treeSize;
            arr[pos++] = y - dx.y * treeSize;
            arr[pos++] = z - dx.z * treeSize;
            arr[pos++] = treesP.txLeft;
            arr[pos++] = treesP.tyDown;

            arr[pos++] = x + (dh.x - dx.x) * treeSize;
            arr[pos++] = y + (dh.y - dx.y) * treeSize;
            arr[pos++] = z + (dh.z - dx.z) * treeSize;
            arr[pos++] = treesP.txLeft;
            arr[pos++] = treesP.tyUp;

            arr[pos++] = x + (dh.x + dx.x) * treeSize;
            arr[pos++] = y + (dh.y + dx.y) * treeSize;
            arr[pos++] = z + (dh.z + dx.z) * treeSize;
            arr[pos++] = treesP.txRight;
            arr[pos++] = treesP.tyUp;

            arr[pos++] = x + dx.x * treeSize;
            arr[pos++] = y + dx.y * treeSize;
            arr[pos++] = z + dx.z * treeSize;
            arr[pos++] = treesP.txRight;
            arr[pos++] = treesP.tyDown;

            if (pos/20==maxTreesCount){
                fb.position(0);
                fb.put(arr);
                pos = 0;
                render(maxTreesCount, aPosition, aTexCoord);
            }
        }
        if (pos>0) {
            fb.position(0);
            fb.put(arr, 0, pos);
            render(pos/20, aPosition, aTexCoord);
        }
    }

    private void render(int count, int aPosition, int aTexPos){
        fb.position(0);
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false, 20, fb);
        fb.position(3);
        glVertexAttribPointer(aTexPos, 2, GL_FLOAT, false, 20, fb);

        glDrawElements(GL_TRIANGLES, count * 6, GL_UNSIGNED_SHORT, sb);
    }
}
