package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;

/**
 * get height for every map positions
 *
 * Created by lgor on 13.12.2014.
 */
public interface iLandscapeMap {

    int getHeight(iPoint2i position);

    void getNormal(iPoint2i position, Vect3f result);

    iRectangle2i bounds();

    public interface Editable {

        //void addHeight(iPoint2i position, int radius, int dh, boolean smooth);

        void generateRandomHeight(float maxHeight, int levels, float persistence, boolean smooth);

        void addHeight(iRectangle2i rect, float dh1, float dh2, float dh3, float dh4, boolean smooth);
    }
}
