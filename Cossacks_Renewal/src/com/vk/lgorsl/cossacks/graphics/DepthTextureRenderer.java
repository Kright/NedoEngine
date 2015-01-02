package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.openGL.Quad;

import static android.opengl.GLES20.*;

/**
 * Renders depth texture to screen
 *
 * Created by lgor on 25.12.2014.
 */
public class DepthTextureRenderer implements GameRenderable{

    private Quad quad;
    private final Matrix4_4f matrix = new Matrix4_4f();

    @Override
    public boolean load(RendererParams params) {
        quad = new Quad(params.depthTexture);
        matrix.getArray()[5]=-1;
        return true;
    }

    @Override
    public void render(RendererParams params) {
        glClearColor(0, 0, 0, 0);
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        quad.render(matrix);
    }

    @Override
    public void renderShadows(RendererParams params) {
        //nothing
    }
}
