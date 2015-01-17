package com.vk.lgorsl.NedoEngine.math;

/**
 * Class with static helper functions
 * <p>
 * Created by lgor on 07.11.2014.
 */
public class Helper {

    private Helper() {
    }

    public static final float vectPrecision = 0.000001f;
    public static final float matrix4_4fPrecision = 0.00001f;

    public static final float degreesInRadian = (float) (180 / Math.PI);
    public static final float radiansInDegree = (float) (Math.PI / 180);

    public static boolean equals(float a, float b, float precision) {
        return a > b ? (a - b) < precision : (b - a) < precision;
    }


    public static float max(float... params) {
        float max = params[0];
        for (int i = 1; i < params.length; i++) {
            if (params[i] > max) {
                max = params[i];
            }
        }
        return max;
    }

    public static float max(float p1, float p2) {
        return p1 > p2 ? p1 : p2;
    }

    public static float max(float p1, float p2, float p3) {
        if (p1 > p2) {
            return p1 > p3 ? p1 : p3;
        } else {
            return p2 > p3 ? p2 : p3;
        }
    }

    public static float max(float p1, float p2, float p3, float p4) {
        float t1 = p1 > p2 ? p1 : p2;
        float t2 = p3 > p4 ? p3 : p4;
        return t1 > t2 ? t1 : t2;
    }

    public static float min(float... params) {
        float min = params[0];
        for (int i = 1; i < params.length; i++) {
            if (params[i] < min) {
                min = params[i];
            }
        }
        return min;
    }

    public static float min(float p1, float p2) {
        return p1 < p2 ? p1 : p2;
    }

    public static float min(float p1, float p2, float p3) {
        float t = p1 < p2 ? p1 : p2;
        return t < p3 ? t : p3;
    }

    public static float min(float p1, float p2, float p3, float p4) {
        float t1 = p1 < p2 ? p1 : p2;
        float t2 = p3 < p4 ? p3 : p4;
        return t1 < t2 ? t1 : t2;
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
        я считаю такие матрицы разными
        */
    }
}
