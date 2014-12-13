package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

/**
 * Naive landscape realization with map of zero height
 *
 * Created by lgor on 13.12.2014.
 */
public class NaiveLandscape implements iLandscapeMap {

    private final iRectangle2i bounds;

    public NaiveLandscape(iRectangle2i bounds) {
        this.bounds = bounds;
    }

    @Override
    public int getHeight(iPoint2i position) {
        return 0;
    }

    @Override
    public iRectangle2i bounds() {
        return bounds;
    }
}
