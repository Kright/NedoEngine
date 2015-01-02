package com.vk.lgorsl.cossacks.graphics;

/**
 * classes which render trees, land, buildings, units, etc
 *
 * Created by lgor on 03.01.2015.
 */
public interface GameRenderSystem extends GameRenderable {

    void renderShadows(RendererParams params);
}
