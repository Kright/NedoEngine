package com.vk.lgorsl.cossacks.graphics;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iLightView;
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;

import java.util.HashMap;

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

        //landscape rendering settings
        int cellSize = 32;
        int chunkSize = cellSize*64;

        /*
        float shadowsEps = 0.002f;
        int depthTextureSize = 1024;
        */
    }

    public Settings settings = new Settings();
    public WorldInstance world;

    public iMapView mapView;

    public final FPSCounter clock;
    public final Point2i defaultViewportSize = new Point2i();   //viewport of surface

    //lightning
    public Texture2D depthTexture;
    public iLightView lightView;

    public LightRenderer lightRenderer;
    public TreesRender treesRender;
    public LandMeshRenderer landMeshRenderer;
    public BuildingsRenderer buildingsRenderer;

    public LandscapeRenderer landscapeRenderer;

    public final Resources resources;

    public CleverShader loadShader(int id){
        CleverShader cs = loadedShaders.get(id);
        if (cs == null){
            cs = new CleverShader(resources, id);
            loadedShaders.put(id, cs);
        }
        return cs;
    }

    private HashMap<Integer, CleverShader> loadedShaders = new HashMap<>(16);

    public RendererParams(Resources resources) {
        this.resources = resources;
        clock = new FPSCounter();
    }
}
