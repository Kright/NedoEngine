package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.*;

/**
 * Country for updating trees and game objects, which war with nobody and nobody owned;
 *
 * Created by lgor on 31.12.2014.
 */
public class CalmCountry implements iCountry {

    private iMap<iUnit> units;
    private iMap<iBuilding> buildings;
    private iPathFinder pathFinder;
    private WorldInstance world;

    public CalmCountry(WorldInstance world){
        this.world = world;
        units = new NaiveMap<>(world.metrics);
        buildings = new NaiveMap<>(world.metrics);
        pathFinder = new NaivePathFinder();
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public iMap<iUnit> units() {
        return units;
    }

    @Override
    public iMap<iBuilding> buildings() {
        return buildings;
    }

    @Override
    public iPathFinder pathFinder() {
        return pathFinder;
    }

    @Override
    public void update() {
        world.trees.update(1);
        units.update(1);
        buildings.update(1);
    }
}
