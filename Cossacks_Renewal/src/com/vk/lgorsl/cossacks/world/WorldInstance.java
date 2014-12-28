package com.vk.lgorsl.cossacks.world;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;
import com.vk.lgorsl.cossacks.world.interfaces.iMap;
import com.vk.lgorsl.cossacks.world.interfaces.iTree;
import com.vk.lgorsl.cossacks.world.realizations.GridLandscape;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;
import com.vk.lgorsl.cossacks.world.realizations.NaiveMap;
import com.vk.lgorsl.cossacks.world.realizations.Tree;

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

    private iTree.Factory treeFactory;

    public void load(){
        metrics = WorldMetrics.sizeInMeters(255, 5);
        GridLandscape land = new GridLandscape(metrics);
        heightGrid = land.grid;

        heightGrid.randomHeight(9, 12*metrics.meterSize(), 0.6f);

        /*
        final float h = 200;
        int size = 12;
        heightGrid.addHeight(new Rectangle2i(128-size, 128-size, 128+size, 128+size), h, h, h, h);
        size-=1;
        heightGrid.addHeight(new Rectangle2i(128-size, 128-size, 128+size, 128+size), h, h, h, h);
        size-=1;
        heightGrid.addHeight(new Rectangle2i(128-size, 128-size, 128+size, 128+size), h, h, h, h);
        heightGrid.addHeight(new Rectangle2i(177, 177, size+177, size+177), h, h, h, h);
        */

        map = land;
        trees = new NaiveMap<>(metrics);

        treeFactory = new Tree.Factory(4);
        Random rnd = new Random();
        for(int i=0; i<1024; i++){
            int x = rnd.nextInt(map.bounds().width()-metrics.meterSize()*2) + map.bounds().xMin()+metrics.meterSize();
            int y = rnd.nextInt(map.bounds().height()-metrics.meterSize()*2) + map.bounds().yMin()+metrics.meterSize();
            trees.add(treeFactory.makeTree(x,y));
        }
    }

    public void tick(){
        trees.update();
    }

    public void save(){
        //nothing yet
    }
}
