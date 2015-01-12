package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.interfaces.iUnit;

/**
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

    boolean alive = true;
    int x, y;
    int direction;
    int hp;

    private SimpleUnit(UnitType type, int id, int x, int y){
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
        this.hp = type.maxHp;
        this.direction = 0;
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

    @Override
    public int hitPoints() {
        return hp;
    }

    @Override
    public int maxHitPoints() {
        return type.maxHp;
    }

    @Override
    public void heal(int heal) {
        hp += heal;
        if (hp > type.maxHp){
            hp = type.maxHp;
        }
    }

    @Override
    public boolean damage(int damage) {
        hp -= damage;
        if (hp<=0){
            alive = false;
            return false;
        }
        return true;
    }
}
