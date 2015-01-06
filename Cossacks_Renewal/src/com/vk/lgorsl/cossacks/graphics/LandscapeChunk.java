package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.interfaces.iProjection;

/**
 * chunk of landscape
 *
 * Created by lgor on 06.01.2015.
 */
public class LandscapeChunk implements GameRenderSystem{

    private CleverShader shader, shadowShader;

    public final Rectangle2i area = new Rectangle2i();
    private int width, height;

    public LandscapeChunk(){
    }

    @Override
    public boolean load(RendererParams params) {
        shader = params.loadShader(R.raw.shader_land_tex_depth_render);
        shadowShader = params.loadShader(R.raw.shader_light_depth);
        return true;
    }

    @Override
    public void renderShadows(RendererParams params) {

    }

    @Override
    public void render(RendererParams params) {

    }

    private void render(iProjection projection) {
        if (projection.viewBounds().intersects(area)){

        }
    }
}
