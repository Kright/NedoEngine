package com.vk.lgorsl.cossacks.world;

import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;
import com.vk.lgorsl.cossacks.world.interfaces.iMap;
import com.vk.lgorsl.cossacks.world.interfaces.iTree;
import com.vk.lgorsl.cossacks.world.realizations.NaiveLandscape;
import com.vk.lgorsl.cossacks.world.realizations.NaiveMap;

/**
 * world instance
 *
 * Created by lgor on 13.12.2014.
 */
public class WorldInstance {

    iLandscapeMap map;
    iMap<iTree> trees;
    WorldMetrics metrics;

    public void load(){
        metrics = WorldMetrics.sizeInMeters(512, 5);
        map = new NaiveLandscape(metrics.mapSize());
        trees = new NaiveMap<>(metrics.mapSize());
    }

    public void tick(){
        trees.update();
        //nothing yet
    }

    public void save(){
        //nothing yet
    }
}
