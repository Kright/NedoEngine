package com.vk.lgorsl.NedoEngine.openGL;

/**
 * something what may be loaded
 *
 * Created by lgor on 25.12.2014.
 */
public interface Loadable<T> {

    /**
     * @return success of loading
     */
    public boolean load(T params);
}
