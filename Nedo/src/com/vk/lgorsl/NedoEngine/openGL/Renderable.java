package com.vk.lgorsl.NedoEngine.openGL;

/**
 * something which can be rendered
 *
 * Created by lgor on 23.11.2014.
 */
public interface Renderable<ParamType> {

    /**
     * rendering something
     */
    void render(ParamType params);

}
