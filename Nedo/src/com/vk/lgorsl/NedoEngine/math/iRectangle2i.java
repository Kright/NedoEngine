package com.vk.lgorsl.NedoEngine.math;

/**
 * interface of rectangle with integer bounds
 *
 * Created by lgor on 13.12.2014.
 */
public interface iRectangle2i {

    int xMin();

    int xMax();

    int yMin();

    int yMax();

    int width();

    int height();

    boolean contains(iPoint2i point2i);

    boolean intersects(iRectangle2i another);
}