package com.vk.lgorsl.cossacks.world;

import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.*;
import com.vk.lgorsl.cossacks.world.realizations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * world instance
 * <p>
 * Created by lgor on 13.12.2014.
 */
public class WorldInstance {

    public iMap<iTree> trees;
    public WorldMetrics metrics;
    public List<iCountry> countries = new ArrayList<>();

    public iLandscapeMap map;

    public GeneratorID generatorID;

    private iTree.Factory treeFactory;

    public void load() {
        generatorID = new GeneratorID();

        metrics = new WorldMetrics(1024 << 5, 1024 << 5, 30 << 5, 5);

        countries.add(new CalmCountry(this));

        RectGridLandscape land = new RectGridLandscape(metrics, metrics.meterSize());
        map = land;

        land.generateRandomHeight(10 * metrics.meterSize(), 8, 0.8f, true);
        land.scaleHeight(metrics.maxHeight() * 0.5f);

        trees = new QuadsMap<>(metrics, metrics.meterSize() * 64);

        treeFactory = new Tree.Factory(4, metrics.meterSize() * 8, metrics.meterSize() * 3);

        generateTrees(1024 * 16);
        generateBuildings(1024, 3, 0);
    }

    private void generateTrees(int count) {
        Random rnd = new Random();
        for (int i = 0; i < count; i++) {
            int x = rnd.nextInt(map.bounds().width() - metrics.meterSize() * 2) + map.bounds().xMin() + metrics.meterSize();
            int y = rnd.nextInt(map.bounds().height() - metrics.meterSize() * 2) + map.bounds().yMin() + metrics.meterSize();
            addTree(treeFactory.makeTree(x, y));
        }
    }

    private void generateBuildings(int count, int radius, int countryID) {
        BuildingType type = new BuildingType(generatorID);
        type.radius = radius * metrics.meterSize();
        type.countryId = countryID;
        type.type = 0;

        Random rnd = new Random();

        for (int i = 0; i < count; i++) {
            int delta = metrics.meterSize() * 16;
            int x = rnd.nextInt(map.bounds().width() - 2 * delta) + map.bounds().xMin() + delta;
            int y = rnd.nextInt(map.bounds().height() - 2 * delta) + map.bounds().yMin() + delta;
            addBuilding(type.makeBuilding(x, y, rnd.nextInt(360)));
        }
    }

    private final Rectangle2i tempRect = new Rectangle2i();

    public boolean addBuilding(iBuilding building) {
        int r = building.radius() * 2;
        tempRect.set(building.x() - r, building.y() - r, building.x() + r, building.y() + r);

        if (countries.get(0).buildings().containsObjects(tempRect)) {
            return false;
        }

        countries.get(0).buildings().add(building);
        r = building.radius();
        tempRect.set(building.x() - r, building.y() - r, building.x() + r, building.y() + r);
        for (iTree tree : trees.objects(tempRect)) {
            tree.cutDown();
        }
        return true;
    }

    public boolean addTree(iTree tree) {
        final int r = 32;
        tempRect.set(tree.x() - r, tree.y() - r, tree.x() + r, tree.y() + r);
        if (trees.containsObjects(tempRect)) {
            return false;
        }
        trees.add(tree);
        return true;
    }

    public boolean isEnemies(int countryId1, int countryId2) {
        if (countryId1 == 0 || countryId2 == 0) {
            return false;
        }
        //naive realization
        return true;
    }

    public void tick() {
        for (iCountry c : countries) {
            c.update();
        }
    }

    public void save() {
        //nothing yet
    }
}
