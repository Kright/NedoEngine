package com.vk.lgorsl.NedoEngine.math;

/**
 * Rectangle.
 * I don't use standard Rect class because I don't agree with realization of some methods and can't to override it -
 * Rect class is final :(
 *
 * Created by lgor on 13.12.2014.
 */
public class Rectangle2i implements iRectangle2i {

    public int xMin, xMax, yMin, yMax;

    public Rectangle2i(int xMin, int yMin, int xMax, int yMax) {
        set(xMin, yMin, xMax, yMax);
    }

    public Rectangle2i set(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        return this;
    }

    public boolean contains(iPoint2i point) {
        return (point.x() >= xMin && point.x() <= xMax && point.y() >= yMin && point.y() <= yMax);
    }

    @Override
    public boolean intersects(iRectangle2i another) {
        return intersects(this, another);
    }

    @Override
    public int xMin() {
        return xMin;
    }

    @Override
    public int xMax() {
        return xMax;
    }

    @Override
    public int yMin() {
        return yMin;
    }

    @Override
    public int yMax() {
        return yMax;
    }

    public int width() {
        return xMax - xMin;
    }

    public int height() {
        return yMax - yMin;
    }

    public boolean intersects(iRectangle2i r1, iRectangle2i r2) {
        return !(r1.xMin() > r2.xMax() || r1.xMax() < r2.xMin() ||
                r1.yMin() > r2.yMax() || r1.yMax() < r2.xMin());
    }
}
