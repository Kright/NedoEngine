package com.vk.lgorsl.NedoEngine.math;

/**
 * realization of Axially Aligned Bounding Box for 3-dimensional vectors;
 * <p/>
 * Created by lgor on 04.11.2014.
 */
public class AABB3f extends AABB<Vect3f> {

    public AABB3f() {
        max = new Vect3f();
        min = new Vect3f();
        clear();
    }

    @Override
    public void clear() {
        float m = Float.POSITIVE_INFINITY;
        min.set(m, m, m);
        m = Float.NEGATIVE_INFINITY;
        max.set(m, m, m);
    }

    @Override
    public void add(Vect3f v) {
        if (v.x < min.x) min.x = v.x;
        if (v.x > max.x) max.x = v.x;
        if (v.y < min.y) min.y = v.y;
        if (v.y > max.y) max.y = v.y;
        if (v.z < min.z) min.z = v.z;
        if (v.z > max.z) max.z = v.z;
    }

    @Override
    public boolean isContains(Vect3f v) {
        return v.x >= min.x && v.x <= max.x &&
                v.y >= min.y && v.y <= max.y &&
                v.z >= min.z && v.z <= max.z;
    }

    @Override
    public boolean isIntersects(AABB<Vect3f> aabb) {
        return (!(max.x < aabb.min.x || min.x > aabb.max.x ||
                  max.y < aabb.min.y || min.y > aabb.max.y ||
                  max.z < aabb.min.z || min.z > aabb.max.z));
    }

    @Override
    public void getCenter(Vect3f v) {
        v.set(max.x + min.x, max.y + min.y, max.z + min.z).mul(0.5f);
    }

    @Override
    public void getSize(Vect3f result) {
        result.set(max.x - min.x, max.y - min.y, max.z - min.z);
    }
}
