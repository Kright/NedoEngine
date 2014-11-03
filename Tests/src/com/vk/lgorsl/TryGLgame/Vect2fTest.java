package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Vect2f;

/**
 * test example
 *
 * Created by lgor on 04.11.2014.
 */
public class Vect2fTest extends AndroidTestCase {

    public void testEquals() {
        Vect2f v1 = new Vect2f();
        Vect2f v2 = new Vect2f();
        Vect2f v3 = new Vect2f().set(2, 3);
        assertEquals(v1, v2);
        assertFalse(v1.equals(v3));
    }

    public void testArithmetic() {
        Vect2f v1 = new Vect2f();
        Vect2f v2 = new Vect2f().set(1, 1);
        v1.add(v2);
        assertTrue(v1.equals(v2));
        v1.sub(v1);
        assertEquals(v1, new Vect2f());
        assertEquals(v1.length(), 0f);
        assertEquals(v2.length(), FloatMath.sqrt(2));
        v1.set(v2);
        v1.normalize();
        v2.setLength(1);
        assertEquals(v1, v2);
        v1.mul(-2);
        v1.add(v2, 1);
        assertEquals(v1.distance(v2), v1.length() * 2);
        assertTrue(v1.cross(v2) == 0);
        assertTrue(Math.abs(v1.dot(v2) + 1) < 0.00001);
    }
}
