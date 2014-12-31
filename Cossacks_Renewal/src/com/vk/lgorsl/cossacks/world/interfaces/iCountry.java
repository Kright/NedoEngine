package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * country
 *
 * Created by lgor on 31.12.2014.
 */
public interface iCountry {

    int getId();

    iMap<iUnit> units();

    iMap<iBuilding> buildings();

    iPathFinder pathFinder();
}
