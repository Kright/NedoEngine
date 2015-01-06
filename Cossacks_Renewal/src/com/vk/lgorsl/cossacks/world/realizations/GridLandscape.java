package com.vk.lgorsl.cossacks.world.realizations;

import android.graphics.Point;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.utils.NedoException;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

/**
 * Landscape class. Knows height of different places of map
 * <p>
 * Created by lgor on 13.12.2014.
 */
public class GridLandscape implements iLandscapeMap {

    public final HeightGrid grid;

    private final iRectangle2i bounds;
    private final Point dPos;

    private final int meterSize;

    public GridLandscape(WorldMetrics metrics) {
        bounds = metrics.mapSize();
        meterSize = metrics.meterSize();
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
    public void getNormal(iPoint2i position, Vect3f result) {
        int x = position.x() / meterSize;
        int y = position.y() / meterSize;

        int d = get(grid, x - 1, y) - get(grid, x + 1, y);
        result.set(d, 0, meterSize).normalize();

        if (y % 2 == 0) {
            d = get(grid, x - 1, y - 1) - get(grid, x, y + 1) + get(grid, x, y - 1) - get(grid, x - 1, y + 1);
        } else {
            d = get(grid, x, y - 1) - get(grid, x + 1, y + 1) + get(grid, x + 1, y - 1) - get(grid, x, y + 1);
        }
        final float lenInv = 1/ FloatMath.sqrt(d*d + 4*meterSize*meterSize);
        result.y += lenInv*d;
        result.z += lenInv*meterSize*2;
        result.normalize();
    }


    private int get(HeightGrid grid, int x, int y) {
        if (x < 0) x++;
        if (y < 0) y++;
        if (x >= grid.width) x = grid.width - 1;
        if (y >= grid.height) y = grid.height - 1;
        return grid.data[x + y * grid.width];
    }

    @Override
    public iRectangle2i bounds() {
        return bounds;
    }
}
