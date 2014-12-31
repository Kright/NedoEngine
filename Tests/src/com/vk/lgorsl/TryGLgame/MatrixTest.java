package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Matrix3_3f;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Quaternion;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * тестирование моей реализации матриц 3*3 и 4*4
 * <p/>
 * Created by lgor on 05.11.2014.
 */
public class MatrixTest extends AndroidTestCase {

    public void testMatrix3_3f() {
        Matrix3_3f id = new Matrix3_3f();
        Matrix3_3f m = new Matrix3_3f();
        assertEquals(id, m);

        Matrix3_3f r = new Matrix3_3f();
        Matrix3_3f.multiply(r, m, id);
        assertEquals(m, id);

        r.invert();
        assertEquals(r, id);

        m.makeRotation(new Quaternion().set(0, 1, 0, 0));
        Matrix3_3f m2 = new Matrix3_3f().set(m);
        assertEquals(m2, m);

        m2.transpose();
        r.set(m).invert();

        assertEquals(m2, r);
        m2.transpose();

        Matrix3_3f.multiply(m, m2, r);
        assertEquals(m, id);

        assertEquals(m.getDeterminant(), 1f);
        m.multiply(2);
        assertEquals(m.getDeterminant(), 8f);

        assertTrue(id.antisymmetric());
        assertTrue(id.symmetric());

        m2.makeRotation(new Quaternion().set(0.6f, 0.8f, 0, 0));
        assertFalse(m2.symmetric());
        assertTrue(m2.antisymmetric());

        m2.makeRotation(new Quaternion().set(0.6f, 0, 0.8f, 0));
        assertFalse(m2.symmetric());
        assertTrue(m2.antisymmetric());

        m2.makeRotation(new Quaternion().set(0.6f, 0, 0, 0.8f));
        assertFalse(m2.symmetric());
        assertTrue(m2.antisymmetric());

        Quaternion q = new Quaternion().set(1f, 0.3f, 0.1f, -0.2f);
        q.normalize();
        m2.makeRotation(q);
        assertTrue("determinant =" + m2.getDeterminant(), Math.abs(m2.getDeterminant() - 1f) < 0.0001);
        Vect3f v1 = new Vect3f(), v2 = new Vect3f(), v3 = new Vect3f();

        m.set(m2);
        m.transpose();
        r.multiplication(m2, m);
        assertEquals(r, id);

        q.set(1, 2, 3, 4);
        q.normalize();
        m2.makeRotation(q);
        m.set(m2);
        m.transpose();
        m.invert();
        assertEquals(m, m2);

        m2.m11 = 12;
        m2.m12 = -3;
        m.set(m2);
        m.invert();
        m.invert();
        assertEquals(m, m2);
    }

    public void testMatrix4_4f() {
        Matrix4_4f id = new Matrix4_4f();
        Matrix4_4f m = new Matrix4_4f();
        assertEquals(id, m);

        Matrix4_4f r = new Matrix4_4f();
        Matrix4_4f.multiply(m, id, r);
        assertEquals(m, id);

        r.invert();
        assertEquals(r, id);

        m.makeRotation(new Quaternion().set(0, 1, 0, 0));
        Matrix4_4f m2 = new Matrix4_4f().set(m);
        assertEquals(m2, m);

        m2.transpose();
        r.set(m).invert();

        assertEquals(m2, r);
        m2.transpose();

        Matrix4_4f.multiply(m, m2, r);
        assertEquals(m, id);

        assertEquals(m.getDeterminant(), 1f);

        assertTrue(id.antisymmetric());
        assertTrue(id.symmetric());

        m2.makeRotation(new Quaternion().set(0.6f, 0.8f, 0, 0));
        assertFalse(m2.symmetric());
        assertTrue(m2.antisymmetric());

        m2.makeRotation(new Quaternion().set(0.6f, 0, 0.8f, 0));
        assertFalse(m2.symmetric());
        assertTrue(m2.antisymmetric());

        m2.makeRotation(new Quaternion().set(0.6f, 0, 0, 0.8f));
        assertFalse(m2.symmetric());
        assertTrue(m2.antisymmetric());

        Quaternion q = new Quaternion().set(1f, 0.3f, 0.1f, -0.2f);
        q.normalize();
        m2.makeRotation(q);
        assertTrue("determinant =" + m2.getDeterminant(), Math.abs(m2.getDeterminant() - 1f) < 0.0001);

        m.set(m2);
        m.transpose();
        r.multiplication(m2, m);
        assertEquals(r, id);

        q.set(1, 2, 3, 4);
        q.normalize();
        m2.makeRotation(q);
        m.set(m2);
        m.transpose();
        m.invert();
        assertEquals(m, m2);

        m.set(m2);
        m.invert();
        m.invert();
        assertEquals(m, m2);

        float[] arr = m.getArray();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float) Math.random();
        }
        m2.set(m);
        if (Math.abs(m2.getDeterminant()) > 0.1) {
            m2.invert();
            m2.invert();
            assertEquals(m, m2);
        }
    }

    public void testMatrixInteraction() {
        Matrix3_3f m3 = new Matrix3_3f();
        Matrix4_4f m4 = new Matrix4_4f();
        float ar[] = m4.getArray();
        for (int i = 0; i < ar.length; i++) {
            ar[i] = (float) Math.random();
        }
        m3.set(m4);
        m4.set(m3);
        Matrix3_3f mm3 = new Matrix3_3f().set(m4);
        assertEquals(m3, mm3);


        float s = 1 / FloatMath.sqrt(2);
        Quaternion q = new Quaternion().set(s, 0, 0, s);
        Vect3f id = new Vect3f().set(1, 2, 3);
        Vect3f expected = new Vect3f().set(-2, 1, 3);
        Vect3f cp = new Vect3f();

        m4.makeRotation(90, 0, 0, 1);
        m4.rotate(cp, id);
        assertEquals(cp, expected);

        m4.mul(cp, id);
        assertEquals(cp, expected);

        m4.makeRotation(q);
        m4.rotate(cp, id);
        assertEquals(cp, expected);

        m4.mul(cp, id);
        assertEquals(cp, expected);

        m3.makeRotation(q);
        m3.mul(cp, id);
        assertEquals(cp, expected);

        assertEquals(m3, m4);

        Vect3f v = new Vect3f().set(1, 2, 3);
        q.set(v, (float) (Math.PI / 2));
        q.normalize();

        Matrix4_4f mm = new Matrix4_4f().makeRotation(90, v.x, v.y, v.z);
        m4.makeRotation(q);
        assertEquals(m4, mm);
        m3.makeRotation(q);
        assertEquals(m3, mm);
        assertEquals(mm, m3);
    }
}
