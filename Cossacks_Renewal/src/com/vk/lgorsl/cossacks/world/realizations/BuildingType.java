package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.interfaces.GeneratorID;
import com.vk.lgorsl.cossacks.world.interfaces.iBuilding;
import com.vk.lgorsl.cossacks.world.interfaces.iHitPoints;

/**
 * заглушка для зданий
 *
 * Created by lgor on 03.01.2015.
 */
public class BuildingType {

    public int radius;
    public int countryId;
    public int type;
    public int maxHp = 1;

    private final GeneratorID generatorID;

    public BuildingType(GeneratorID generatorID){
        this.generatorID = generatorID;
    }

    public iBuilding makeBuilding(final int x, final int y, final int direction) {
        final int id = generatorID.getNextID();
        final HitPoints hitPoints = new HitPoints(maxHp);

        return new iBuilding() {
            boolean alive = true;

            @Override
            public int getType() {
                return type;
            }

            @Override
            public int getDirection() {
                return direction;
            }

            @Override
            public int radius() {
                return radius;
            }

            @Override
            public boolean needsInUpdates() {
                return false;
            }

            @Override
            public void update() {
                //nothing
            }

            @Override
            public iHitPoints hitPoints() {
                return hitPoints;
            }

            @Override
            public int id() {
                return id;
            }

            @Override
            public int getCountryId() {
                return countryId;
            }

            @Override
            public boolean alive() {
                return alive;
            }

            @Override
            public int x() {
                return x;
            }

            @Override
            public int y() {
                return y;
            }
        };
    }
}
