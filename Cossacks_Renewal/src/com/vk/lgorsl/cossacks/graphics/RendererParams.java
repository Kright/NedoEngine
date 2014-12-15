package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;

/**
 * Created by lgor on 15.12.2014.
 */
public class RendererParams {

    public final WorldInstance world;
    public final iMapView mapView;

    public RendererParams(WorldInstance world, iMapView mapView) {
        this.world = world;
        this.mapView = mapView;
    }
}
