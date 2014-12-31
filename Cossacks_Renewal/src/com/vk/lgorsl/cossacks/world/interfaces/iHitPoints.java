package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * something with hitpoints
 *
 * Created by lgor on 31.12.2014.
 */
public interface iHitPoints {

    int hitPoints();

    int maxHitPoints();

    void heal(int hp);

    /**
     * @return true if die
     */
    boolean damage(int hp);
}
