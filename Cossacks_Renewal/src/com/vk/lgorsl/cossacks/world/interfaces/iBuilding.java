package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * game buildings
 *
 * Created by lgor on 31.12.2014.
 */
public interface iBuilding extends iMapObject{

    /**
     * @return type of building
     */
    int getType();

    /**
     * orientation on map;
     */
    int getDirection();

    int radius();

    boolean needsInUpdates();

    void update();

    iHitPoints hitPoints();
}
