package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.*;

/**
 * bound of camera's view on 2d map
 *
 * Created by lgor on 28.12.2014.
 */
public class ViewBounds {

    public final Vect2f leftUp, leftDown, rightUp, rightDown;

    private final Point2i[] verts = new Point2i[4];
    private final Point2i[] normals = new Point2i[4];
    private final int[] pr = new int[4];

    public ViewBounds() {
        leftUp = new Vect2f();
        leftDown = new Vect2f();
        rightUp = new Vect2f();
        rightDown = new Vect2f();
        for(int i=0; i<4; i++){
            normals[i] = new Point2i();
            verts[i] = new Point2i();
        }
    }

    public void getAABB(Rectangle2i result){
        result.xMax = (int)Helper.max(leftUp.x, leftDown.x, rightUp.x, rightDown.x);
        result.xMin = (int)Helper.min(leftUp.x, leftDown.x, rightUp.x, rightDown.x);
        result.yMax = (int)Helper.max(leftUp.y, leftDown.y, rightUp.y, rightDown.y);
        result.yMin = (int)Helper.min(leftUp.y, leftDown.y, rightUp.y, rightDown.y);
    }

    public void update(){
        Vect2f center = new Vect2f();
        center.set(leftUp).add(leftDown).add(rightUp).add(rightDown).mul(0.25f);

        calculate(0, leftDown, rightDown, center);
        calculate(1, rightDown, rightUp, center);
        calculate(2, rightUp, leftUp, center);
        calculate(3, leftUp, leftDown, center);

        verts[0].set((int)leftDown.x, (int)leftDown.y);
        verts[1].set((int)leftUp.x, (int)leftUp.y);
        verts[2].set((int)rightDown.x, (int)rightDown.y);
        verts[3].set((int)rightUp.x, (int)rightUp.y);
    }

    private void calculate(int number, Vect2f v1, Vect2f v2, Vect2f center){
        int nx = (int)(v1.y- v2.y);
        int ny = (int)(v2.x - v1.x);
        int dc = (int)(nx*center.x + ny*center.y);
        int d = (int)(nx * v1.x + ny * v2.x);
        if (dc < d) {
            nx *= -1;
            ny *= -1;
            d *=-1;
        }
        assert(dc > d);
        normals[number].x = nx;
        normals[number].y = ny;
        pr[number] = d;
    }

    public boolean contains(iPoint2i point){
        int px = point.x();
        int py = point.y();
        for(int i=0; i<4; i++){
            if (px * normals[i].x + py * normals[i].y < pr[i]){
                return false;
            }
        }
        return true;
    }

    public boolean intersects(iRectangle2i rectangle){
        int xMin = rectangle.xMin();
        int xMax = rectangle.xMax();
        int yMin = rectangle.yMin();
        int yMax = rectangle.yMax();
        boolean biggerX = true;
        boolean smallerX = true;
        boolean biggerY = true;
        boolean smallerY = true;
        for(int i=0; i<4; i++){
            if (verts[i].x <= xMax) biggerX = false;
            if (verts[i].x >= xMin) smallerX = false;
            if (verts[i].y <= yMax) biggerY = false;
            if (verts[i].y >= yMin) smallerY = false;
        }
        return !(biggerX || smallerX || biggerY || smallerY);
    }
}
