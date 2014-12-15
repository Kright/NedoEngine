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

    void setScale(float scale);

    void setDirectionOfView(float dx, float dy);

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
