package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import com.vk.lgorsl.NedoEngine.math.Matrix3_3f;
import com.vk.lgorsl.NedoEngine.math.Quaternion;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * тестирование моей реализации матриц 3*3 и 4*4
 *
 * Created by lgor on 05.11.2014.
 */
public class MatrixTest extends AndroidTestCase{

    public void testMatrix3_3f(){
        Matrix3_3f id = new Matrix3_3f();
        Matrix3_3f m = new Matrix3_3f();
        assertEquals(id, m);

        Matrix3_3f r = new Matrix3_3f();
        Matrix3_3f.multiply(m, id, r);
        assertEquals(m, id);

        r.invert();
        assertEquals(r, id);

        m.makeRotation(new Quaternion().set(0,1,0,0));
        Matrix3_3f m2 = new Matrix3_3f().set(m);
        assertEquals(m2,m);

        m2.transpose();
        r.set(m).invert();

        assertEquals(m2,r);
        m2.transpose();

        Matrix3_3f.multiply(m2, r, m);
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
        m2.getXAxis(v1);
        m2.getYAxis(v2);
        m2.getZAxis(v3);
        assertTrue(v1.length() == 1f);
        assertTrue(v2.length()==1f);
        assertTrue("length = " + v3.length(), Math.abs(v3.length()-1)<0.00001);
        assertTrue(v1.dot(v2)+"", Math.abs(v1.dot(v2))<0.000001f);
        assertTrue(v1.dot(v3)==0f);
        assertTrue(v2.dot(v3)==0f);

        m.set(m2);
        m.transpose();
        r.multiplication(m2, m);
        assertEquals(r, id);

        q.set(1,2,3,4);
        q.normalize();
        m2.makeRotation(q);
        m.set(m2);
        m.transpose();
        m.invert();
        assertEquals(m, m2);

        m2.m11=12;
        m2.m12=-3;
        m.set(m2);
        m.invert();
        m.invert();
        assertEquals(m, m2);
    }
}
