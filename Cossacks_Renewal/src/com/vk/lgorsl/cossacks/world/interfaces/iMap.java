package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.iRectangle2i;

/**
 * class of abstract map
 *
 * Created by lgor on 13.12.2014.
 */
public interface iMap<T extends iMapObject>{

    /**
     * @param obj - map object which will be added to map
     */
    void add(T obj);

    boolean remove(T obj);

    /**
     * @return size of map
     */
    iRectangle2i bounds();

    /**
     * game objects may to change their positions or to die
     * to fix it, call update() method;
     */
    void update();

    /**
     * @return iterable for objects from area
     */
    Iterable<T> objects(iRectangle2i area);
}