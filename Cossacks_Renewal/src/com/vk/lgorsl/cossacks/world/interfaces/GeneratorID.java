package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * generator for unique ID numbers
 *
 * Created by lgor on 06.01.2015.
 */
public class GeneratorID {

    private int id;

    public int getNextID(){
        return ++id;
    }
}
