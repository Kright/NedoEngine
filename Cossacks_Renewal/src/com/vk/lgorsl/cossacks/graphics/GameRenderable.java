package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.openGL.Loadable;
import com.vk.lgorsl.NedoEngine.openGL.Renderable;

/**
 * I'm lazy and don't want to write (Renderable<LoadedData>, Loadable<RendererParams>) many times
 *
 * Created by lgor on 16.12.2014.
 */
public interface GameRenderable extends Renderable<RendererParams>, Loadable<RendererParams>{

    //nothing
}
