package com.vk.lgorsl.cossacks.world.realizations;

import android.graphics.Point;
import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.utils.NedoException;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

/**
 * Landscape class. Knows height of different places of map
 *
 * Created by lgor on 13.12.2014.
 */
public class GridLandscape implements iLandscapeMap {

    public final HeightGrid grid;

    private final iRectangle2i bounds;
    private final Point dPos;

    public GridLandscape(WorldMetrics metrics) {
        bounds = metrics.mapSize();
        grid = new HeightGrid(bounds.width() / metrics.meterSize() + 1,
                bounds.height() / metrics.meterSize() + 1,
                metrics.getRShiftToMeter());
        dPos = new Point(bounds.xMin(), bounds.yMin());
    }

    @Override
    public int getHeight(iPoint2i position) {
        try {
            return grid.getHeight(dPos.x + position.x(), dPos.y + position.y());
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new NedoException("out of boudns error, input position " + position);
        }
    }

    @Override
    public iRectangle2i bounds() {
        return bounds;
    }
}
