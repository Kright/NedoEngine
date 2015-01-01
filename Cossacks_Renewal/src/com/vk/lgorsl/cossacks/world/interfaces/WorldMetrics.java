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

    /**
     * map size isn't in meters!
     * @param mapXsize width
     * @param mapYsize width along another axis
     * @param maxHeight
     * @param meterOffset meter size = (1 << meterOffset)
     */
    public WorldMetrics(int mapXsize, int mapYsize, int maxHeight, int meterOffset) {
        boudns = new Rectangle2i(0, 0, mapXsize, mapYsize);
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
}
