package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;

/**
 * get height for every map positions
 *
 * Created by lgor on 13.12.2014.
 */
public interface iLandscapeMap {

    int getHeight(iPoint2i position);

    iRectangle2i bounds();
}
