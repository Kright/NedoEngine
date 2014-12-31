package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.iPoint2i;

import java.util.List;

/**
 * it try to find shortcut from A to B
 *
 * Created by lgor on 31.12.2014.
 */
public interface iPathFinder {

    /**
     * @return empty list if way doesn't exists
     */
    List<iPoint2i> findWay(iPoint2i start, iPoint2i destination);

    /**
     * @return next wayPoint or start point if way doesn't exists
     */
    iPoint2i nextWayPoint(iPoint2i start, iPoint2i destination);
}
