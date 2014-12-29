package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Rectangle2i;

import java.util.Random;

/**
 * карта высот
 * Created by lgor on 14.12.2014.
 */
public class HeightGrid {

    /*
        *---*---*---*
         \ / \ / \ / \
          *---*---*---*
         / \ / \ / \ /
        *---*---*---*
         \ / \ / \ / \
          *---*---*---*
     */

    public final short[] data;
    public final int width, height;
    public final int scaleShift;

    public HeightGrid(int gridW, int gridH, int scaleShift) {
        this.height = gridH;
        this.width = gridW;
        this.scaleShift = scaleShift;
        data = new short[gridH * gridW];
    }

    public void addHeight(Rectangle2i rect, float dh1, float dh2, float dh3, float dh4, boolean smooth){
        final float pi = (float) Math.PI;
        for(int x=rect.xMin; x<=rect.xMax; x++){
            float kx0 = (x-rect.xMin)/(float)rect.width();
            float dkx = 0.5f / rect.width();
            for(int y=rect.yMin; y<=rect.yMax; y++){
                int n = getIndex(x, y);
                if (n<0) continue;
                float kx = kx0;
                if (y%2==1){
                    kx += dkx;
                }
                float ky = (y-rect.yMin)/(float)rect.height();
                if (smooth){
                    kx = 0.5f - 0.5f*FloatMath.cos(kx*pi);
                    ky = 0.5f - 0.5f*FloatMath.cos(ky*pi);
                }
                float dh = (1-ky)*(dh1*(1-kx) + kx*dh2) + ky*(dh3*(1-kx)+kx*dh4);
                data[n] += (short)dh;
            }
        }
    }

    private int getIndex(int x, int y) {
        if (contains(x, y)) {
            return y * width + x;
        }
        return -1;
    }

    private boolean contains(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    //crazy temporal code
    public void randomHeight(int levels, float max, float persistence, boolean smooth) {
        Random rnd = new Random();
        Rectangle2i rect = new Rectangle2i(0, 0, width-1, height-1);
        int size = width;
        int pow = 1;
        float dh = max;
        for(int level=0; level<levels && size>1 && dh>1; level++){
            float[][] h = new float[pow+1][pow+1];
            for(int dx=0; dx<pow+1; dx++){
                for(int dy=0; dy<pow+1; dy++){
                    h[dx][dy] = dh * (float)rnd.nextGaussian();
                }
            }
            for(int dx=0; dx<pow; dx++){
                for(int dy=0; dy<pow; dy++){
                    rect.set(dx*size, dy*size, (dx+1)*size-1, (dy+1)*size-1);
                    addHeight(rect, h[dx][dy], h[dx+1][dy], h[dx][dy+1], h[dx+1][dy+1], smooth);
                }
            }
            size /= 2;
            pow *= 2;
            dh *= persistence;
        }
    }

    //crazy, not optimized code. May be wrong
    public int getHeight(int x, int y) {
        x *= 2;
        final int size = 1 << scaleShift;

        final int row = y / size;
        if (row<0 || row >= height) return 0;
        final int dy = y - row * size;

        if (!even(y)) {
            x += size;
        }
        final int column = x / (size*2);
        if (column <0 || column >= width) return 0;

        final int dx = x - column * size;

        final int result = data[row*width+column];

        // it is crasy :(
        // but I didn't write working approximate code yet

        return result;
    }

    private static int average(int dh2, int dh3, int dx, int dy, int size){
        float k3 = (float)(dy) / size;
        float k2 = (float)(dx-dy) / 2 / size;
        return (int)(k3*dh3 + k2*dh2);
    }

    private static boolean even(int n) {
        return (n & 1) == 0;
    }
}
