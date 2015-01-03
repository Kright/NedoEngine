package com.vk.lgorsl.cossacks.graphics;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;

/**
 * params for rendering
 * uses as global for game renderers
 *
 * Created by lgor on 15.12.2014.
 */
public class RendererParams {

    public static class Settings{

        //shadows settings
        boolean shadowsEnabled = true;
        float shadowsEps = 0.001f;
        int depthTextureSize = 2048;

    }

    public Settings settings = new Settings();
    public WorldInstance world;

    public iMapView mapView;

    public final FPSCounter clock;
    public final Point2i defaultViewportSize = new Point2i();   //viewport of surface

    //lightning
    public Texture2D depthTexture;
    public iMapView lightningView;


    public LightRenderer lightRenderer;
    public TreesRender treesRender;
    public LandMeshRenderer landMeshRenderer;
    public BuildingsRenderer buildingsRenderer;

    public final Resources resources;

    public RendererParams(Resources resources) {
        this.resources = resources;
        clock = new FPSCounter();
    }
}
