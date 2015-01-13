package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.*;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

import java.util.Random;

/**
 * realization of height grid
 * <p>
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

        if (px < 0 || py < 0 || px >= width || py >= height) return getSafe(px, py);

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

    @Override
    public void getNormal(iPoint2i position, Vect3f result) {
        int x = dx + position.x();
        int y = dy + position.y();

        int px = x / cellSize;
        int py = y / cellSize;

        int dx = getSafe(px - 1, py) - getSafe(px + 1, py);
        int dy = getSafe(px, py - 1) - getSafe(px, py + 1);

        result.set(dx, dy, cellSize * 2).normalize();
    }

    private int getSafe(int x, int y) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return data[x + y * width];
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
    public void generateRandomHeight(float maxHeight, int levels, float persistence, boolean smooth) {
        Random rnd = new Random();
        Rectangle2i rect = new Rectangle2i();
        int size = mapSize.width();
        int pow = 1;
        float dh = maxHeight;
        for (int level = 0; level < levels && size > 1 && dh > 1; level++) {
            float[][] h = new float[pow + 1][pow + 1];
            for (int dx = 0; dx < pow + 1; dx++) {
                for (int dy = 0; dy < pow + 1; dy++) {
                    h[dx][dy] = Math.abs(dh * (float) rnd.nextGaussian());
                }
            }
            for (int dx = 0; dx < pow; dx++) {
                for (int dy = 0; dy < pow; dy++) {
                    rect.set(dx * size, dy * size, (dx + 1) * size - 1, (dy + 1) * size - 1);
                    addHeight(rect, h[dx][dy], h[dx + 1][dy], h[dx][dy + 1], h[dx + 1][dy + 1], smooth);
                }
            }
            size /= 2;
            pow *= 2;
            dh *= persistence;
        }
    }

    public void scaleHeight(float maxAmplitude) {
        int max = data[0];
        int min = data[0];
        for (int i : data) {
            if (i < min) min = i;
            if (i > max) max = i;
        }
        final float k = maxAmplitude / (max - min);
        final float dh = -min * k;
        for (int i = 0; i < data.length; i++) {
            data[i] = (int) (data[i] * k + dh);
        }
    }

    @Override
    public void addHeight(iRectangle2i rect, float dh1, float dh2, float dh3, float dh4, boolean smooth) {
        final float pi = (float) Math.PI;

        int pxMin = Math.max(0, (rect.xMin() + dx + cellSize - 1) / cellSize);
        int pyMin = Math.max(0, (rect.yMin() + dy + cellSize - 1) / cellSize);

        int pxMax = Math.min(width - 1, (rect.xMax() + dx) / cellSize);
        int pyMax = Math.min(height - 1, (rect.yMax() + dy) / cellSize);

        for (int j = pyMin; j <= pyMax; j++) {
            float ky = (float) (j * cellSize - dy - rect.yMin()) / rect.height();
            if (smooth) {
                ky = 0.5f - 0.5f * FloatMath.cos(ky * pi);
            }
            for (int i = pxMin; i <= pxMax; i++) {
                float kx = (float) (i * cellSize - dx - rect.xMin()) / rect.width();
                if (smooth) kx = 0.5f - 0.5f * FloatMath.cos(kx * pi);
                data[i + j * width] += (int) ((1 - kx) * ((1 - ky) * dh1 + ky * dh3) + kx * ((1 - ky) * dh2 + ky * dh4));
            }
        }
    }
}
