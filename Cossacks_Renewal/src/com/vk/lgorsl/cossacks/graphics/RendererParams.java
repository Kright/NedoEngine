package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;

/**
 * params for rendering
 * uses as global for game renderers
 *
 * Created by lgor on 15.12.2014.
 */
public class RendererParams {

    public final WorldInstance world;
    public final iMapView mapView;

    //lightning
    public boolean lightningRendering = true;
    public final int depthTextureSize = 512;
    public final Matrix4_4f lightningMatrix = new Matrix4_4f();

    public RendererParams(WorldInstance world, iMapView mapView) {
        this.world = world;
        this.mapView = mapView;
    }
}
