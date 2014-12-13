package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.iPoint2i;

/**
 * base interface for game objects which can be placed on map (people, buildings, trees, etc.)
 *
 * Created by lgor on 13.12.2014.
 */
public interface iMapObject extends iPoint2i {

    /**
     * @return unique identifier.
     */
    int id();

    /**
     * dead gameObjects will be removed
     */
    boolean alive();
}
