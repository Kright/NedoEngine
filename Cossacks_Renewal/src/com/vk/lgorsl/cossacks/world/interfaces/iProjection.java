package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * cam with projection
 *
 * Created by lgor on 03.01.2015.
 */
public interface iProjection {

    /**
     * @return projection matrix for openGL
     */
    Matrix4_4f projection();

    /**
     * то, что снаружи, заведомо не рисуется.
     * @return bounds
     */
    ViewBounds viewBounds();

    /**
     * @return projection forward vector
     */
    Vect3f viewDirection();
}
