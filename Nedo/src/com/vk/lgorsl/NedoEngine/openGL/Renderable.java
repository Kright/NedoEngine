package com.vk.lgorsl.NedoEngine.openGL;

import android.content.res.Resources;

/**
 * something which can be rendered
 *
 * Created by lgor on 23.11.2014.
 */
public interface Renderable<LoadType, ParamType> {

    /**
     * loading raw resources like images
     */
    void load(LoadType loadType);

    /**
     * rendering something
     */
    void render(ParamType params);

}
