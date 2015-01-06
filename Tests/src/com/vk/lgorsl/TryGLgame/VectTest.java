package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Vect2f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * Тестирование векторов
 *
 * Created by lgor on 05.11.2014.
 */
public class VectTest extends AndroidTestCase {

    public void testVectOperators(){
    }

    public void testVectEquals() {
        assertEquals(new Vect3f().set(1, 2, 3), new Vect3f().set(1, 2, 3));
        assertEquals(new Vect2f().set(1, 2), new Vect2f().set(1, 2));
        assertFalse(new Vect3f().equals(new Vect2f()));
        assertFalse(new Vect2f().set(1,2).equals(new Vect2f().set(2, 1)));
        assertFalse(new Vect3f().set(1,2,3).equals(new Vect3f().set(2,1,3)));
    }

    public void testEquals() {
        Vect2f v1 = new Vect2f();
        Vect2f v2 = new Vect2f();
        Vect2f v3 = new Vect2f().set(2, 3);
        assertEquals(v1, v2);
        assertFalse(v1.equals(v3));
    }

    public void testVect2Arithmetic(){
        Vect2f v1, v2;
        v1 = new Vect2f();
        v2 = new Vect2f();
        v1.set(1,2).mul(2);
        assertEquals(v1, v2.set(2, 4));
        v1.add(v2);
        assertEquals(v1, v2.set(4, 8));
        v1.madd(v2, -2);
        assertEquals(v1, v2.set(-4, -8));
        v1.sub(v2.set(-2, -4));
        assertEquals(v1, v2.set(-2, -4));
        v1.set(2, 3);
        v2.set(1,4);
        assertEquals(v1.dot(v2), v2.dot(v1));
        assertEquals(v1.dot(v2), 14f);
        assertEquals(v1.length2(), 13f);
        assertEquals(v1.cross(v2), (float)(8-3));
        v1.set(0, 3);
        v2.set(3,-1);
        assertEquals(v1.distance(v2), 5f);
    }

    public void testVect3Arithmetic(){
        Vect3f v1, v2;
        v1 = new Vect3f();
        v2 = new Vect3f();
        v1.set(1,2,3).mul(4);
        assertEquals(v1, v2.set(4, 8, 12));
        assertEquals(v1.set(v2), v2);
        v2.set(1, 2, 3);
        v1.set(0,0,0).add(v2);
        assertEquals(v1, v2);
        v1.madd(v2, -2);
        assertEquals(v1, v2.set(-1, -2, -3));
        v1.set(-2,-4,-6);
        v2.set(-1,-2,-3);
        v1.sub(v2);
        assertEquals(v1, v2);
        assertEquals(v1.length2(), 1f+4+9);
        assertEquals(v1.dot(v1), v1.length2());
        assertEquals(v1.length(), FloatMath.sqrt(v1.length2()));
        assertEquals(v1.distance(v2), 0f);

        Vect3f.cross(v1, v2, v2);
        assertEquals(v1.length2(), 0f);

        v1.set(1,2,3);
        v2.set(4,5,6);
        Vect3f.cross(v1, v1, v2);
        v2.set(2*6-3*5, 3*4-6, 5-8);
        assertEquals(v1,v2);
    }

    public void testVectNArithmetic(){
        Vect3f v1 = new Vect3f().set(1,1,2);
        Vect3f v2 = new Vect3f().set(1,1,-1);
        v1.normalize();
        v2.normalize();
        Vect3f v3 = new Vect3f();
        Vect3f.cross(v3, v1, v2);
        assertTrue(Math.abs(v3.length() - 1f) < 0.00001f);

        Vect3f.cross(v3, v1, v1);
        assertTrue(Math.abs(v3.length()) < 0.00001f);

        v1.setLength(3);
        assertTrue(Math.abs(v3.length()) < 0.00001f);
    }
}
