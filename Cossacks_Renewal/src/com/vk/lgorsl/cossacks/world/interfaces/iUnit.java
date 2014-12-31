package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * soldiers, peasants are units
 *
 * Created by lgor on 31.12.2014.
 */
public interface iUnit extends iMapObject{

    int getDirection();

    int getState();     //moving, reloading, attacking, etc

    int getCountryId();
}
