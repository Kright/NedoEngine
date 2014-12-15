package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.NedoEngine.math.iPoint2i;

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

    public final int getHeight(iPoint2i position) {
        return getHeight(position.x(), position.y());
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
                if (!even(row)){
                    pos3++;
                }
                return simpleTriangle(dx, dy, size, pos1, pos1+1, pos3);
            } else {
                int pos3 = row * width + column;
                int pos1 = pos3 + width;
                if (!even(row)){
                    pos1--;
                }
                return simpleTriangle(dx - size, size - dy, size, pos1, pos1+1, pos3);
            }
        } else {
            if (dx+dy<size){
                int pos1 = row * width + column;
                int pos3 = (row + 1) * width + column;
                if (!even(row)){
                    pos3++;
                }
                return simpleTriangle(dx, dy, size, pos1, pos1+1, pos3);
            }
            else {
                int pos3 = 1 + row * width + column;
                int pos1 = pos3 + width;
                if (!even(row)){
                    pos1--;
                }
                return simpleTriangle(dx - size, size - dy, size, pos1, pos1+1, pos3);
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
