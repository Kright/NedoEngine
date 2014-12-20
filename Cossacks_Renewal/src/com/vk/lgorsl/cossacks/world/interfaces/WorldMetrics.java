package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;

/**
 * metrics of world - size of map and size of 1 meter in game.
 *
 * Created by lgor on 14.12.2014.
 */
public class WorldMetrics {

    private final int maxHeight;
    private final int meterOffset;

    private final iRectangle2i boudns;

    private WorldMetrics(int mapWidth, int mapHeight, int maxHeight, int meterOffset) {
        boudns = new Rectangle2i(0, 0, mapWidth, mapHeight);
        this.meterOffset = meterOffset;
        this.maxHeight = maxHeight;
    }

    public iRectangle2i mapSize() {
        return boudns;
    }

    public int maxHeight(){
        return maxHeight;
    }

    public int meterSize() {
        return 1 << meterOffset;
    }

    public int getRShiftToMeter() {
        return meterOffset;
    }

    public int heightMeterSize(){
        return meterOffset;
    }

    public static WorldMetrics sizeInMeters(int size, int meterOffset) {
        size *= (1 << meterOffset);
        return new WorldMetrics(size, size, 30*(1<<meterOffset), meterOffset);
    }
}
