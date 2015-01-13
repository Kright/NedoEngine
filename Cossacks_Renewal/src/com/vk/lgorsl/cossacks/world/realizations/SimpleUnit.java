package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iHitPoints;
import com.vk.lgorsl.cossacks.world.interfaces.iUnit;

/**
 * naive unit realization
 *
 * Created by lgor on 12.01.2015.
 */
public class SimpleUnit implements iUnit{

    public static class UnitType{

        final int type;
        final int maxHp;
        final int countryId;
        final WorldInstance worldInstance;

        public UnitType(int type, WorldInstance worldInstance, int maxHp, int countryId){
            this.type = type;
            this.maxHp = maxHp;
            this.countryId = countryId;
            this.worldInstance = worldInstance;
        }

        iUnit makeUnit(int x, int y) {
            return new SimpleUnit(this, worldInstance.generatorID.getNextID(), x, y);
        }
    }

    private final UnitType type;
    private final int id;
    private final HitPoints hp;

    int x, y;
    int direction;

    private SimpleUnit(UnitType type, int id, int x, int y){
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
        this.hp = new HitPoints(type.maxHp);
        this.direction = 0;
    }

    @Override
    public iHitPoints hitPoints() {
        return null;
    }

    @Override
    public int type() {
        return type.type;
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int getCountryId() {
        return type.countryId;
    }

    @Override
    public boolean alive() {
        return hp.alive();
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }
}
