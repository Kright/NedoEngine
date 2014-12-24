package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.*;

/**
 * game camera;
 *
 * Created by lgor on 13.12.2014.
 */
public interface iMapView {

    /**
     * @return center of view;
     */
    iPoint2i center();

    void setCenterPosition(iPoint2i newCenter);

    /**
     * @param scale - will be applied to all axises
     */
    void setScale(float scale);

    /**
     * @param scale only for vertical axis.
     */
    void setVerticalScale(float scale);

    void setDirectionOfView(float dx, float dy);

    public Vect3f getXProjection();

    public Vect3f getYProjection();

    public Vect3f getUpProjection();

    public void getViewDirection(Vect3f result);

    /**
     * @param angle in degrees
     * 90 is vertical, 0 is horizontal field of view;
     */
    void setInclination(float angle);

    /**
     * @return projection matrix for openGL
     */
    Matrix4_4f projection();

    /**
     * то, что снаружи bounding box, заведомо не рисуется.
     * @return bounds
     */
    iRectangle2i boundingBox();
}
