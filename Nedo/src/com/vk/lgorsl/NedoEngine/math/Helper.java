package com.vk.lgorsl.NedoEngine.math;

/**
 * Class with static helper functions
 *
 * Created by lgor on 07.11.2014.
 */
public class Helper {

    private Helper() {
    }

    public static final float vectPrecision = 0.000001f;
    public static final float matrix4_4fPrecision = 0.00001f;

    public static boolean equals(float a, float b, float precision) {
        return a > b ? (a - b) < precision : (b - a) < precision;
    }

    public static boolean equals(Matrix4_4f m4, Matrix3_3f m) {
        float arr[] = m4.getArray();
        return equals(arr[0], m.m11, matrix4_4fPrecision) &&
                equals(arr[1], m.m21, matrix4_4fPrecision) &&
                equals(arr[2], m.m31, matrix4_4fPrecision) &&

                equals(arr[4], m.m12, matrix4_4fPrecision) &&
                equals(arr[5], m.m22, matrix4_4fPrecision) &&
                equals(arr[6], m.m32, matrix4_4fPrecision) &&

                equals(arr[8], m.m13, matrix4_4fPrecision) &&
                equals(arr[9], m.m23, matrix4_4fPrecision) &&
                equals(arr[10], m.m33, matrix4_4fPrecision) &&

                equals(arr[3], 0, matrix4_4fPrecision) &&
                equals(arr[7], 0, matrix4_4fPrecision) &&
                equals(arr[11], 0, matrix4_4fPrecision) &&
                equals(arr[12], 0, matrix4_4fPrecision) &&
                equals(arr[13], 0, matrix4_4fPrecision) &&
                equals(arr[14], 0, matrix4_4fPrecision) &&
                equals(arr[15], 1f, matrix4_4fPrecision);
        /*
        строго говоря, возможен случай, когда arr[15]!=1
        и тогда Matrix4.rotate(vector) != Matrix4.mul(vector)
        и, что ещё хуже, возможна ситуация, когда для любого вектора
        Matrix4.mul(vector) == Matrix3.mul(vector), но компоненты
        m11 и т.п. не равны.
        я на него забил и считаю такие матрицы разными
        */
    }
}
