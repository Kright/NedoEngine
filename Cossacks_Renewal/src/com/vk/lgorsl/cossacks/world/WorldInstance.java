package com.vk.lgorsl.cossacks.world;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;
import com.vk.lgorsl.cossacks.world.interfaces.iMap;
import com.vk.lgorsl.cossacks.world.interfaces.iTree;
import com.vk.lgorsl.cossacks.world.realizations.GridLandscape;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;
import com.vk.lgorsl.cossacks.world.realizations.NaiveMap;

/**
 * world instance
 *
 * Created by lgor on 13.12.2014.
 */
public class WorldInstance {

    iLandscapeMap map;
    iMap<iTree> trees;
    public WorldMetrics metrics;

    public HeightGrid heightGrid;

    public void load(){
        metrics = WorldMetrics.sizeInMeters(255, 5);
        GridLandscape land = new GridLandscape(metrics);
        heightGrid = land.grid;
        heightGrid.randomHeight(7, 10*metrics.meterSize(), 0.8f);
        map = land;
        trees = new NaiveMap<>(metrics);
    }

    public void tick(){
        trees.update();
        //nothing yet
    }

    public void save(){
        //nothing yet
    }
}
