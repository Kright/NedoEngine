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
     * game meter size in screen coordinates
     * @param scale - will be applied to all axises
     */
    void setScale(float scale);

    void setDirectionOfView(float dx, float dy);

    /**
     * @param ratio = screen width / height
     */
    void setAspectRatio(float ratio);

    /**
     * @param angleInDegrees in degrees
     * 90 is vertical, 0 is horizontal field of view;
     */
    void setInclination(float angleInDegrees);

    void getViewDirection(Vect3f result);

    /**
     * @return projection matrix for openGL
     */
    Matrix4_4f projection();

    /**
     * necessary for lightning
     * if standard projection gets results in [-1 .. 1], this in [0, 1]
     * @return modified projection
     */
    Matrix4_4f anotherProjection();

    /**
     * то, что снаружи bounding box, заведомо не рисуется.
     * @return bounds
     */
    ViewBounds viewBounds();
}
