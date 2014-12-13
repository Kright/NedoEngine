package com.vk.lgorsl.NedoEngine.math;

/**
 * realization of iPoint2i interface
 * <p/>
 * Created by lgor on 13.12.2014.
 */
public class Point2i implements iPoint2i {

    public Point2i() {
    }

    public int x, y;

    public Point2i set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
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
