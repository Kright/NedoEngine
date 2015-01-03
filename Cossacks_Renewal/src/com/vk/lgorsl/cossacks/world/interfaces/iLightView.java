package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;

/**
 * interface of view for calculation depth from light source
 *
 * Created by lgor on 03.01.2015.
 */
public interface iLightView extends iProjection{

    /**
     * @param angleInDegrees in degrees
     * 90 is vertical, 0 is horizontal field of view;
     */
    void setInclination(float angleInDegrees);

    void setViewDirection(float x, float y);

    void setToCover(ViewBounds bounds);

    Matrix4_4f anotherProjection();
}
