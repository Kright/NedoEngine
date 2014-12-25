package com.vk.lgorsl.cossacks.graphics;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * params for rendering
 * uses as global for game renderers
 *
 * Created by lgor on 15.12.2014.
 */
public class RendererParams {

    public WorldInstance world;
    public iMapView mapView;

    //lightning
    public boolean lightningRendering = true;
    public final Point2i defaultViewPortSize = new Point2i();
    public Texture2D depthTexture;
    public iMapView lightningView;

    //temporal data
    public FloatBuffer meshVertices, meshNormals;
    public ShortBuffer meshIndices;

    public final Resources resources;

    public RendererParams(Resources resources) {
        this.resources = resources;
    }
}
