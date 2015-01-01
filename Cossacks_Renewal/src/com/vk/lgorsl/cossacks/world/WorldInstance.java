package com.vk.lgorsl.cossacks.world;
import com.vk.lgorsl.cossacks.world.interfaces.*;
import com.vk.lgorsl.cossacks.world.realizations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * world instance
 *
 * Created by lgor on 13.12.2014.
 */
public class WorldInstance {

    public iLandscapeMap map;
    public iMap<iTree> trees;
    public WorldMetrics metrics;
    public HeightGrid heightGrid;
    public List<iCountry> countries = new ArrayList<>();

    private iTree.Factory treeFactory;

    public void load(){
        metrics = new WorldMetrics(255 << 5, 255 << 5 , 30 << 5, 5);
        GridLandscape land = new GridLandscape(metrics);
        heightGrid = land.grid;

        heightGrid.randomHeight(8, 8*metrics.meterSize(), 0.6f, true);

        map = land;
        trees = new NaiveMap<>(metrics);

        treeFactory = new Tree.Factory(4, metrics.meterSize()*8, metrics.meterSize() * 3);
        generateTrees(1024);

        countries.add(new CalmCountry(this));
    }

    private void generateTrees(int count){
        Random rnd = new Random();
        for(int i=0; i<count; i++){
            int x = rnd.nextInt(map.bounds().width()-metrics.meterSize()*2) + map.bounds().xMin()+metrics.meterSize();
            int y = rnd.nextInt(map.bounds().height()-metrics.meterSize()*2) + map.bounds().yMin()+metrics.meterSize();
            trees.add(treeFactory.makeTree(x,y));
        }
    }

    public boolean isEnemies(int countryId1, int countryId2){
        if (countryId1 == 0 || countryId2 == 0){
            return false;
        }
        //naive realization
        return true;
    }

    public void tick(){
        for(iCountry c : countries){
            c.update();
        }
    }

    public void save(){
        //nothing yet
    }
}
