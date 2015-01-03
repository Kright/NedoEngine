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

    public interface Editable {

        //void addHeight(iPoint2i position, int radius, int dh, boolean smooth);

        void generateRandomHeight(int maxHeight, int levels, int persistence, boolean smooth);

        void addHeight(iRectangle2i rect, int dh1, int dh2, int dh3, int dh4, boolean smooth);
    }
}
