package com.vk.lgorsl.NedoEngine.utils;

/**
 * copy-paste from
 * http://en.wikipedia.org/wiki/Xorshift
 * <p/>
 * extremely simple algorithm
 * <p/>
 * Created by lgor on 18.11.2014.
 */
public class XorShift64Star {

    public long x;

    public XorShift64Star(long state) {
        x = state != 0 ? state : 12345;
    }

    public long get(){
        x ^= x >>> 12;
        x ^= x << 25;
        x ^= x >> 27;
        //я надеюсь, разница между unsigned int64 и long несущественна
        return x * 2685821657736338717L;
    }
}


