package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.interfaces.iMapObject;

/**
 * simple realization of map object
 *
 * Created by lgor on 13.12.2014.
 */
public class MapObject implements iMapObject{

    protected int x,y;
    protected int id;
    protected boolean alive = true;

    @Override
    public int id() {
        return id;
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
}
