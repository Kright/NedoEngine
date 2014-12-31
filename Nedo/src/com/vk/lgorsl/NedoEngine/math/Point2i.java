package com.vk.lgorsl.NedoEngine.math;

/**
 * realization of iPoint2i interface
 * <p>
 * Created by lgor on 13.12.2014.
 */
public class Point2i implements iPoint2i {

    public Point2i() {
    }

    public Point2i(iPoint2i src){
        x = src.x();
        y = src.y();
    }

    public int x, y;

    public Point2i set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Point2i set(iPoint2i src) {
        x = src.x();
        y = src.y();
        return this;
    }

    public void add(iPoint2i add) {
        x += add.x();
        y += add.y();
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

    /*
     * I can't use Java 8 features and make this method as default or static in iPoint2i interface
     * because of android :(
     */

    public static boolean equals(iPoint2i f, iPoint2i s) {
        return f.x() == s.x() && f.y() == s.y();
    }

    @Override
    public String toString() {
        return toString(this);
    }

    public static String toString(iPoint2i point2i) {
        return "(" + point2i.x() + ", " + point2i.y() + ")";
    }
}
