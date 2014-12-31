package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * game buildings
 *
 * Created by lgor on 31.12.2014.
 */
public interface iBuilding extends iMapObject, iHitPoints{

    /**
     * @return type of building
     */
    int getType();

    /**
     * orientation on map;
     */
    int getDirection();

    boolean needsInUpdates();

    void update();
}
