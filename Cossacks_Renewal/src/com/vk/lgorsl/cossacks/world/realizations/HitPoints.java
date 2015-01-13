package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.interfaces.iHitPoints;

/**
 * simple hit points realization
 *
 * Created by lgor on 13.01.2015.
 */
public final class HitPoints implements iHitPoints {

    private final int maxHitPoints;
    private int hitPoints;

    public HitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
        hitPoints = maxHitPoints;
    }

    @Override
    public boolean alive() {
        return hitPoints>0;
    }

    @Override
    public int hitPoints() {
        return hitPoints;
    }

    @Override
    public int maxHitPoints() {
        return maxHitPoints;
    }

    @Override
    public void heal(int hp) {
        if (!alive()) return;
        hitPoints += hp;
        if (hitPoints > maxHitPoints){
            hitPoints = maxHitPoints;
        }
    }

    @Override
    public boolean damage(int damage) {
        hitPoints -= damage;
        return hitPoints<=0;
    }
}
