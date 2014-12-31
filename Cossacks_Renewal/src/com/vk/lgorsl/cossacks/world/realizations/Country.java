package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.*;

/**
 * Simple iCountry realization
 *
 * Created by lgor on 31.12.2014.
 */
public class Country implements iCountry {

    public WorldInstance world;
    public int id;
    public iMap<iUnit> units;
    public iMap<iBuilding> buildings;
    public iPathFinder pathFinder;

    public Country(WorldInstance world, int id){
        this.world = world;
        this.id = id;
        units = new NaiveMap<>(world.metrics);
        buildings = new NaiveMap<>(world.metrics);
        pathFinder = new NaivePathFinder();
    }

    @Override
    public int getId() {
        return id;
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
        units.update(1);
        buildings.update(1);
    }
}
