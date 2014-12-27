package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iTree;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glDrawElements;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
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
        render(params, params.mapView.projection(), shader);
    }

    public void render(RendererParams params, Matrix4_4f matrix4_4f, CleverShader shader){
        int meter = params.world.metrics.meterSize();
        float[] arr = matrix4_4f.getArray();
        Vect3f dx = new Vect3f().set(-arr[5], arr[1], 0);
        dx.setLength(meter*2);
        Vect3f dh = new Vect3f().set(0, 0, meter*10);
        int count = putData(fb, dx, dh, params.world, params.world.map.bounds());

        {    //temp!!
            glCullFace(GL_FRONT);
            glFrontFace(GL_CCW);
            glDisable(GL_CULL_FACE);
        }

        shader.useProgram();
        GLHelper.checkError("start");

        texture.use(0);
        glUniform1i(shader.get("uTexture"), 0);

        glUniformMatrix4fv(shader.get("uMatrix"), 1, false, matrix4_4f.getArray(), 0);

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

    private int putData(FloatBuffer fb, Vect3f dx, Vect3f dh, WorldInstance world, iRectangle2i boundingBox) {
        fb.position(0);

        Vect3f pos = new Vect3f();
        int count = 0;

        for (iTree tree : world.trees.objects(boundingBox)) {
            if (!tree.alive()) {
                continue;
            }
            int xi = tree.x();
            int yi = tree.y();
            int zi = world.heightGrid.getHeight(xi, yi);
            //int zi = 10;

            float x = xi;
            float y = yi;
            float z = zi;

            TreesParams treesP = treesParams[tree.type()];

            pos.set(x, y, z);
            pos.madd(dx, -1f);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txLeft);
            fb.put(treesP.tyDown);

            pos.set(x, y, z);
            pos.add(dx);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txRigth);
            fb.put(treesP.tyDown);

            pos.set(x, y, z);
            pos.add(dx);
            pos.add(dh);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txRigth);
            fb.put(treesP.tyUp);

            pos.set(x, y, z);
            pos.madd(dx, -1f);
            pos.add(dh);
            pos.putIntoFloatBuffer(fb);
            fb.put(treesP.txLeft);
            fb.put(treesP.tyUp);

            count++;
        }
        return count;
    }
}
