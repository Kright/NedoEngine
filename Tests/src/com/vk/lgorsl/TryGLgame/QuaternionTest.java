package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Quaternion;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * tests for quaternions class
 *
 * Created by lgor on 07.11.2014.
 */
public class QuaternionTest extends AndroidTestCase {

    public void testSetMethods() {
        float x = 1, y = 3, z = -2, w = 6;
        Quaternion q = new Quaternion();
        q.set(w, x, y, z);
        assertTrue(q.x == x && q.y == y && q.z == z && q.w == w);

        Quaternion q2 = new Quaternion();
        final float angle = 0.5f;
        Vect3f v = new Vect3f().set(x, y, z);
        q.set(v, angle);
        v.normalize();
        float sin = FloatMath.sin(angle/2);
        q2.set(FloatMath.cos(angle/2), v.x * sin, v.y * sin, v.z * sin);
        assertEquals(q, q2);
        assertFalse(q.equals(new Quaternion()));

        assertEquals(q.setIdentity(), q2.set(1,0,0,0));

        q.set(w, x, y, z);
        assertEquals(new Quaternion().set(q), q);

        v.setLength(angle/2);
        assertEquals(q.setFromExp(v), q2.set(v, angle));
    }
}
