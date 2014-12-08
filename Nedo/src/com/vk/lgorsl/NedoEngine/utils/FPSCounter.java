package com.vk.lgorsl.NedoEngine.utils;

/**
 * Счётчик кадров в секунду, можно использовать для дельта-тайминга или, например,
 * некоторых действий каждый n-ый кадр:
 * if (framesCount % n) {...}
 *
 * Created by lgor on 04.11.2014.
 */
public class FPSCounter {

    private long prev, now;

    private int framesCount;
    private int counter;
    private int fps;
    private int delta;

    public FPSCounter(){
        prev = now = System.currentTimeMillis();
    }

    /**
     * You have to call it once per frame drawing
     * @return fps
     */
    public int update(){
        framesCount++;
        counter++;
        long old = now;
        now = System.currentTimeMillis();
        delta = (int)(now - old);
        int dt = (int)(now - prev);
        if (dt > 1000){
            fps = 1 + (counter * 1000 - 1) / dt;
            counter = 0;
            prev = now;
        }
        return fps;
    }

    /**
     * @return time of last update in milliseconds
     */
    public long getTime(){
        return now;
    }

    /**
     * @return frames per second count
     */
    public int fps(){
        return fps;
    }

    /**
     * @return time between two last update() calls
     */
    public int deltaTime(){
        return delta;
    }

    /**
     * @return total number of update() calls
     */
    public int framesCount(){
        return framesCount;
    }
}
