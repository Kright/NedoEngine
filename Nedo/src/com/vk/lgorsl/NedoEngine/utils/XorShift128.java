package com.vk.lgorsl.NedoEngine.utils;

/**
 * copy-paste from
 * http://en.wikipedia.org/wiki/Xorshift
 * <p/>
 * Created by lgor on 18.11.2014.
 */
public class XorShift128 {

    public int x, y, z, w;

    public XorShift128(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public int getNext() {
        int t = x ^ (x << 11);
        x = y;
        y = z;
        z = w;
        return w = w ^ (w >>> 19) ^ t ^ (t >> 8);
    }
}
