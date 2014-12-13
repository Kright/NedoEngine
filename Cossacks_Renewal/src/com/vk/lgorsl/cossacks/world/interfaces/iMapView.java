package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;

/**
 * game camera;
 *
 * Created by lgor on 13.12.2014.
 */
public interface iMapView {

    /**
     * @return center of view;
     */
    public iPoint2i center();

    /**
     * @return projection matrix for openGL
     */
    public Matrix4_4f projection();

    /**
     * то, что снаружи bounding box, заведомо не рисуется.
     * @return bounds
     */
    public iRectangle2i boundingBox();
}
