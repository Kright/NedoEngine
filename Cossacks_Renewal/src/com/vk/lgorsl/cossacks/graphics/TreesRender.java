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
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;
import com.vk.lgorsl.cossacks.world.interfaces.iTree;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glDrawElements;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * it renders trees :)
 *
 * Created by lgor on 27.12.2014.
 */
public class TreesRender implements GameRenderable {

    private Texture2D texture;

    private FloatBuffer fb;
    private int maxTreesCount = 1024;
    private ShortBuffer sb;

    private CleverShader shader;

    private class TreesParams {
        float txLeft, txRigth, tyUp, tyDown;
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
        for(int i=0; i<treesParams.length; i++){
            treesParams[i] = new TreesParams();
            treesParams[i].txLeft = 1f/8*i;
            treesParams[i].txRigth = 1f/8 + 1f/8*i;
            treesParams[i].tyDown = 1/4f;
            treesParams[i].tyUp = 0f;
        }

        shader = new CleverShader(params.resources, R.raw.shader_simple);

        params.treesRender = this;

        return true;
    }

    @Override
    public void render(RendererParams params) {
        render(params, params.mapView, shader);
    }

    public void render(RendererParams params, iMapView view, CleverShader shader){
        float[] arr = view.projection().getArray();
        Vect3f dx = new Vect3f().set(-arr[5], arr[1], 0);
        dx.setLength(0.2f);
        Vect3f dh = new Vect3f().set(0, 0, 1);

        int count = putData(fb, dx, dh, params.world, view.viewBounds());

        shader.useProgram();
        GLHelper.checkError("start");

        texture.use(0);
        glUniform1i(shader.get("uTexture"), 0);

        glUniformMatrix4fv(shader.get("uMatrix"), 1, false, view.projection().getArray(), 0);

        shader.enableAllVertexAttribArray();

        fb.position(0);
        glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false, 20, fb);

        fb.position(3);
        glVertexAttribPointer(shader.get("aTexCoord"), 2, GL_FLOAT, false, 20, fb);

        GLHelper.checkError("before rendering");
        glDrawElements(GL_TRIANGLES, count*6, GL_UNSIGNED_SHORT, sb);

        shader.disableAllVertexAttribArray();
        GLHelper.checkError("end");
    }

    private final Rectangle2i aabb = new Rectangle2i(0,0,0,0);

    private int putData(FloatBuffer fb, Vect3f dx, Vect3f dh, WorldInstance world, ViewBounds boundingBox) {
        fb.position(0);

        Vect3f pos = new Vect3f();
        int count = 0;

        boundingBox.getAABB(aabb);

        for (iTree tree : world.trees.objects(aabb)) {
            if (!tree.alive()) {
                continue;
            }
            int xi = tree.x();
            int yi = tree.y();
            int zi = world.heightGrid.getHeight(xi, yi);

            float x = xi;
            float y = yi;
            float z = zi;

            /*
            {   //test!!
                Vect2f v;
                switch (tree.type()){
                    case 0: v = boundingBox.leftDown; break;
                    case 1: v = boundingBox.leftUp; break;
                    case 2: v = boundingBox.rightDown; break;
                    default: v = boundingBox.rightUp; break;
                }
                x = v.x;
                y = v.y;
                z = 0;
            }
            */

            TreesParams treesP = treesParams[tree.type()];
            float treeSize= tree.size();

            pos.set(x, y, z);
            pos.madd(dx, -treeSize);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txLeft);
            fb.put(treesP.tyDown);

            pos.set(x, y, z);
            pos.madd(dx, -treeSize);
            pos.madd(dh, treeSize);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txLeft);
            fb.put(treesP.tyUp);

            pos.set(x, y, z);
            pos.madd(dx, treeSize);
            pos.madd(dh, treeSize);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txRigth);
            fb.put(treesP.tyUp);

            pos.set(x, y, z);
            pos.madd(dx, treeSize);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txRigth);
            fb.put(treesP.tyDown);

            count++;
        }
        return count;
    }
}
