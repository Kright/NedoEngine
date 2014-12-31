package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.cossacks.world.interfaces.iPathFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * naive realization
 *
 * Created by lgor on 31.12.2014.
 */
public class NaivePathFinder implements iPathFinder {

    @Override
    public List<iPoint2i> findWay(iPoint2i start, iPoint2i destination) {
        ArrayList<iPoint2i> lst = new ArrayList<>();
        lst.add(nextWayPoint(start, destination));
        return lst;
    }

    @Override
    public iPoint2i nextWayPoint(iPoint2i start, iPoint2i destination) {
        return new Point2i().set(destination);
    }
}
