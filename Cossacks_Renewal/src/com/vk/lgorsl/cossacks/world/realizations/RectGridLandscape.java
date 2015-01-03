package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.iPoint2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

/**
 * realization of height grid
 *
 * Created by lgor on 03.01.2015.
 */
public class RectGridLandscape implements iLandscapeMap, iLandscapeMap.Editable {

    private final int cellSize;
    private final iRectangle2i mapSize;

    private final int[] data;
    private final int width, height;
    private final int dx, dy;

    public RectGridLandscape(WorldMetrics metrics, int gridCellSize) {
        cellSize = gridCellSize;

        mapSize = metrics.mapSize();

        width = mapSize.width() / gridCellSize + 1;
        height = mapSize.height() / gridCellSize + 1;
        data = new int[width * height];

        dx = mapSize.xMin();
        dy = mapSize.yMin();
    }

    @Override
    public int getHeight(iPoint2i position) {
        int x = dx + position.x();
        int y = dy + position.y();

        int px = x / cellSize;
        int py = y / cellSize;

        x = x % cellSize;
        y = y % cellSize;

        if (x != 0) {
            if (y != 0) {  //x!=0, y!=0
                int xx1 = av(get(px, py), get(px, py), x);
                int xx2 = av(get(px, py + 1), get(px, py + 1), x);
                return av(xx1, xx2, y);
            } else {    //x!=0, y==0
                return av(get(px, py), get(px + 1, py), x);
            }
        } else {
            if (y != 0) {   // x==0, y!=0
                return av(get(px, py), get(px, py + 1), y);
            } else {    // x == 0, y==0
                return get(px, py);
            }
        }
    }

    private int av(int v1, int v2, int t) {
        return (v1 * (cellSize - t) + v2 * t) / cellSize;
    }

    private int get(int x, int y) {
        return data[x + y * width];
    }

    @Override
    public iRectangle2i bounds() {
        return mapSize;
    }

    @Override
    public void generateRandomHeight(int maxHeight, int levels, int persistence, boolean smooth) {
    }

    @Override
    public void addHeight(iRectangle2i rect, int dh1, int dh2, int dh3, int dh4, boolean smooth) {
        int pxMin = (rect.xMin() + dx + cellSize - 1) / cellSize;
        int pyMin = (rect.yMin() + dy + cellSize - 1) / cellSize;

        int pxMax = (rect.xMax() + dx) / cellSize;
        int pyMax = (rect.yMax() + dy) / cellSize;

        for (int j = pyMin; j <= pyMax; j++){
            float ky = (float)(j*cellSize-dy - rect.yMin()) / rect.height();
            if (smooth) {
                ky = 0.5f - FloatMath.cos(ky);
            }
            for (int i = pxMin; i <= pxMax; i++) {
                float kx = (float)(i*cellSize - dx - rect.xMin()) / rect.width();
                if (smooth) kx = 0.5f - FloatMath.cos(kx);
                data[i + j* width] += (int) ((1-kx) * ((1-ky) * dh1 + ky*dh3) + kx * ((1-ky)*dh2 + ky * dh4));
            }
        }
    }
}
