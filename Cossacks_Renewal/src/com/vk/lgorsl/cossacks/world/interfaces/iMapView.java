package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.*;

/**
 * game camera;
 *
 * Created by lgor on 13.12.2014.
 */
public interface iMapView extends iProjection{

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
}
