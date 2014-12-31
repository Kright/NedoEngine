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
     * 0 is default for objects who don't owned by countries (trees, rocks);
     */
    int getCountryId();

    /**
     * dead gameObjects will be removed from game world
     */
    boolean alive();
}
