package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Helper;
import com.vk.lgorsl.NedoEngine.math.Quaternion;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * tests for quaternions class
 * <p/>
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
        float sin = FloatMath.sin(angle / 2);
        q2.set(FloatMath.cos(angle / 2), v.x * sin, v.y * sin, v.z * sin);
        assertEquals(q, q2);
        assertFalse(q.equals(new Quaternion()));

        assertEquals(q.setIdentity(), q2.set(1, 0, 0, 0));

        q.set(w, x, y, z);
        assertEquals(new Quaternion().set(q), q);

        v.setLength(angle / 2);
        assertEquals(q.setFromExp(v), q2.set(v, angle));
    }

    public void testLogAndExp() {
        Vect3f v = new Vect3f().set(0.2f, 0.3f, 0.4f);
        Quaternion q = new Quaternion();
        Vect3f v2 = new Vect3f();
        q.setFromExp(v);
        q.getLog(v2);
        assertEquals(v, v2);
    }

    public void testGetAngle_n_Axis() {
        Vect3f v = new Vect3f().set(2, 3, 4);
        final float angle = 0.4f;
        Quaternion q = new Quaternion();
        q.set(v, angle);
        assertTrue(equals(q.getRotationAngleForNormalized(), angle));
        assertTrue(equals(q.getRotationAngle(), angle));
        Vect3f v2 = new Vect3f();
        q.getRotationAxis(v2);
        v.normalize();
        assertEquals(v, v2);

        q.set(v, angle);
        q.negate();
        assertEquals(v, v2);
    }

    public void testLength() {
        float x = 0.1f;
        float y = 0.3f;
        float z = 0.5f;
        float w = 0.7f;
        Quaternion q = new Quaternion();
        assertTrue(equals(q.set(w, x, y, z).length(), FloatMath.sqrt(q.dot(q))));
        assertTrue(equals(q.set(w, x, y, z).length(), FloatMath.sqrt(x * x + y * y + z * z + w * w)));
        assertTrue(equals(q.lengthXYZ(), FloatMath.sqrt(x * x + y * y + z * z)));
    }

    public void testNormalize_n_Negate() {
        float x = 0.1f;
        float y = 0.3f;
        float z = 0.5f;
        float w = 0.7f;
        Quaternion q = new Quaternion();
        q.set(w, x, y, z);
        q.normalize();
        assertEquals(q.length(), 1f);
        q.negate();
        assertEquals(q.length(), 1f);
    }

    public void testMultiplication_n_Pow() {
        Quaternion q = new Quaternion().set(2, 3, 4, 5);
        Quaternion q2 = new Quaternion();
        q2.multiplication(q, q);
        q.pow(2);
        q.normalize();
        q2.normalize();
        assertEquals(q2, q);

        q.set(2, 3, 4, 5);
        q2.set(q);
        q2.conjugate();
        q.pow(-1);
        q.normalize();
        q2.normalize();
        assertEquals(q, q2);
    }

    public void testEquals() {
        float x = 0.1f;
        float y = 0.3f;
        float z = 0.5f;
        float w = 0.7f;
        Quaternion q = new Quaternion().set(w, x, y, z);
        Quaternion q2 = new Quaternion().set(w, x, y, z);
        assertEquals(q, q2);
        q.set(-w, -x, -y, -z);
        assertEquals(q, q2);
        q.negate();
        assertEquals(q, q2);
    }

    public void testConjugate() {
        float x = 0.1f;
        float y = 0.3f;
        float z = 0.5f;
        float w = 0.7f;
        Quaternion q = new Quaternion().set(w, x, y, z);
        Quaternion q2 = new Quaternion().set(q);
        q2.conjugate();
        q2.multiplication(q, q2);
        q2.normalize();
        assertEquals(q2, q.set(1, 0, 0, 0));
    }

    /**
     * возможны косяки, лучше проверить потом в 3д, просто глазами посмотреть на вращения и убедиться
     * в правильности работы
     */
    public void testLerp() {
        Quaternion q1 = new Quaternion().set(1f, .5f, .07f, .09f);
        q1.normalize();
        Quaternion q2 = new Quaternion().set(q1);
        Quaternion q3 = new Quaternion().set(q1);
        q2.pow(2);
        q3.pow(3);
        q1.normalize();
        q2.normalize();
        q3.normalize();
        Quaternion r = new Quaternion();
        Quaternion.slerp(q1, q3, 0.5f, r);
        r.normalize();
        assertEquals(r, q2);

        Quaternion.lerp(q1, q3, 0.5f, r);
        r.normalize();
        assertEquals(r, q2);
    }

    private static boolean equals(float f1, float f2) {
        return Helper.equals(f1, f2, Helper.vectPrecision);
    }
}
