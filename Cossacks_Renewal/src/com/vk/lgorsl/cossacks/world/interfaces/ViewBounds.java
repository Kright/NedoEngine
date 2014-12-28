package com.vk.lgorsl.cossacks.world.interfaces;

import com.vk.lgorsl.NedoEngine.math.Helper;
import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.Vect2f;

/**
 * bound of camera's view on 2d map
 *
 * Created by lgor on 28.12.2014.
 */
public class ViewBounds {

    public final Vect2f leftUp, leftDown, rightUp, rightDown;

    public ViewBounds() {
        leftUp = new Vect2f();
        leftDown = new Vect2f();
        rightUp = new Vect2f();
        rightDown = new Vect2f();
    }

    public void getAABB(Rectangle2i result){
        result.xMax = (int)Helper.max(leftUp.x, leftDown.x, rightUp.x, rightDown.x);
        result.xMin = (int)Helper.min(leftUp.x, leftDown.x, rightUp.x, rightDown.x);
        result.yMax = (int)Helper.max(leftUp.y, leftDown.y, rightUp.y, rightDown.y);
        result.yMin = (int)Helper.min(leftUp.y, leftDown.y, rightUp.y, rightDown.y);
    }
}
