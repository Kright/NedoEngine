package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.ViewBounds;

/**
 * Created by lgor on 06.01.2015.
 */
public class ViewBoundsTest extends AndroidTestCase {

    public void testContains(){
        ViewBounds vb = new ViewBounds();

        vb.rightUp.set(10, 20);
        vb.rightDown.set(10, 0);
        vb.leftUp.set(0, 20);
        vb.leftDown.set(0, 0);

        vb.update();

        assertTrue(vb.contains(new Point2i().set(5,5)));
        assertTrue(vb.contains(new Point2i().set(1,1)));
        assertTrue(vb.contains(new Point2i().set(9, 20)));

        assertFalse(vb.contains(new Point2i().set(11, 0)));
        assertFalse(vb.contains(new Point2i().set(-10, 0)));
        assertFalse(vb.contains(new Point2i().set(0, 21)));
        assertFalse(vb.contains(new Point2i().set(11, -20)));
    }

    public void testIntersects(){
        ViewBounds vb = new ViewBounds();

        vb.rightUp.set(10, 20);
        vb.rightDown.set(10, 0);
        vb.leftUp.set(0, 20);
        vb.leftDown.set(0, 0);

        vb.update();

        assertTrue(vb.intersects(new Rectangle2i(0, 0, 1, 1)));
        assertTrue(vb.intersects(new Rectangle2i(0, 0, 10, 20)));
        assertTrue(vb.intersects(new Rectangle2i(-100, -100, 100, 100)));
        assertTrue(vb.intersects(new Rectangle2i(5, 5, 30, 30)));
        assertTrue(vb.intersects(new Rectangle2i(2,2, 500, 3)));

        assertFalse(vb.intersects(new Rectangle2i(30, 0, 40, 20)));
        assertFalse(vb.intersects(new Rectangle2i(-30, 0, -20, 20)));
        assertFalse(vb.intersects(new Rectangle2i(30, 50, 40, 100)));
        assertFalse(vb.intersects(new Rectangle2i(5, -10, 10, -5)));
        assertFalse(vb.intersects(new Rectangle2i(5, 15, 10, 20)));
    }
}
