package com.vk.lgorsl.cossacks.world.realizations;

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

    public void addHeight(Rectangle2i rect, float dh1, float dh2, float dh3, float dh4){
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
    public void randomHeight(int levels, float max, float persistence) {
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
                    addHeight(rect, h[dx][dy], h[dx+1][dy], h[dx][dy+1], h[dx+1][dy+1]);
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
        final int dy = y - row * size;

        if (!even(y)) {
            x += size;
        }
        final int column = x / size;
        final int dx = x - column * size;

        if (even(column)) {
            if (dx > dy) {
                int pos1 = row * width + column;
                int pos3 = (row + 1) * width + column;
                if (!even(row)) {
                    pos3++;
                }
                return simpleTriangle(dx, dy, size, pos1, pos1 + 1, pos3);
            } else {
                int pos3 = row * width + column;
                int pos1 = pos3 + width;
                if (!even(row)) {
                    pos1--;
                }
                return simpleTriangle(dx - size, size - dy, size, pos1, pos1 + 1, pos3);
            }
        } else {
            if (dx + dy < size) {
                int pos1 = row * width + column;
                int pos3 = (row + 1) * width + column;
                if (!even(row)) {
                    pos3++;
                }
                return simpleTriangle(dx, dy, size, pos1, pos1 + 1, pos3);
            } else {
                int pos3 = 1 + row * width + column;
                int pos1 = pos3 + width;
                if (!even(row)) {
                    pos1--;
                }
                return simpleTriangle(dx - size, size - dy, size, pos1, pos1 + 1, pos3);
            }
        }
    }

    private int simpleTriangle(int dx, int dy, int size, int p1, int p2, int p3) {
        int result;
        result = (2 * size - dx - dy) * data[p1];
        result += (dx - dy) * data[p2];
        result += (2 * dy) * data[p3];
        return result / 2 * size;
    }

    private static boolean even(int n) {
        return (n & 1) == 0;
    }
}
