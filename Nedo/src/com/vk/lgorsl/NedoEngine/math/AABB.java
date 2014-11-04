package com.vk.lgorsl.NedoEngine.math;

/**
 * abstract axially aligned bounding box
 *
 * Created by lgor on 04.11.2014.
 */
abstract class AABB<T> {

    public T max, min;

    public abstract void clear();

    public abstract void add(T v);

    public void add(AABB<T> another){
        add(another.max);
        add(another.min);
    }

    public abstract boolean isContains(T v);

    public boolean isContains(AABB<T> aabb){
        return isContains(aabb.max) && isContains(aabb.min);
    }

    public abstract boolean isIntersects(AABB<T> aabb);

    public abstract void getCenter(T v);

    /**
     * @param result = max-min;
     */
    public abstract void getSize(T result);
}
