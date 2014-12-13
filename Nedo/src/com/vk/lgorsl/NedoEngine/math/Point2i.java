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

    @Override
    public boolean equals(Object o) {
        return (o instanceof Point2i) && equals(this, (Point2i) o);
    }

    public boolean equals(iPoint2i point) {
        return equals(this, point);
    }

    public static boolean equals(iPoint2i f, iPoint2i s) {
        return f.x() == s.x() && f.y() == s.y();
    }
}
