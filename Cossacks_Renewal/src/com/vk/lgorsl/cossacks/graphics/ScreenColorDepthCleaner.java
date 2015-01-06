package com.vk.lgorsl.cossacks.graphics;

import static android.opengl.GLES20.*;

/**
 * simple renderer
 * clears depth and color buffers
 *
 * Created by lgor on 01.01.2015.
 */
public class ScreenColorDepthCleaner implements GameRenderable{

    private final float cR, cG, cB, depth;

    public ScreenColorDepthCleaner(){
        this(0, 0, 0, 1);
    }

    public ScreenColorDepthCleaner(float red, float green, float blue, float defaultDepth){
        cR = red;
        cG = green;
        cB = blue;
        depth = defaultDepth;
    }

    @Override
    public boolean load(RendererParams params) {
        return true;
    }

    @Override
    public void render(RendererParams params) {
        glClearColor(cR, cG, cB, 1f);
        glClearDepthf(depth);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
