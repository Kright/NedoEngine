package com.vk.lgorsl.TryGLgame;

import android.test.AndroidTestCase;
import com.vk.lgorsl.NedoEngine.math.Matrix3_3f;
import com.vk.lgorsl.NedoEngine.math.Quaternion;

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

        r.invert(r);
        assertEquals(r, id);

        m.makeRotation(new Quaternion().set(0,1,0,0));
        Matrix3_3f m2 = new Matrix3_3f().set(m);
        assertEquals(m2,m);

        m2.transpose();
        r.set(m).invert(r);

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
    }
}
