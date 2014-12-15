package com.vk.lgorsl.NedoEngine.math;

import android.opengl.Matrix;

/**
 * It uses some functions from android.opengl.Matrix;
 * немного неочевидная нумерация :
 * [0, 4, 8, 12]
 * [1, 5, 9, 13]
 * [2, 6, 10, 14]
 * [3, 7, 11, 15]
 * К сожалению, буду её использовать
 * <p/>
 * vectors is a columns
 * <p/>
 * методы вида make* создают определённую матрицу ВМЕСТО текущей.
 * <p/>
 * Created by lgor on 04.11.2014.
 */
public class Matrix4_4f {

    protected final static int size = 4;
    protected final static int len = size * size;

    protected final float[] arr;

    public Matrix4_4f() {
        arr = new float[len];
        arr[0] = arr[5] = arr[10] = arr[15] = 1f;
    }

    public Matrix4_4f(float[] f) {
        if (f.length != len) {
            throw new IllegalArgumentException("array length must be " + size * size + ", but it is " + f.length);
        }
        arr = f;
    }

    public Matrix4_4f set(Matrix4_4f example) {
        System.arraycopy(example.arr, 0, arr, 0, len);
        return this;
    }

    public Matrix4_4f makeIdentity() {
        return makeScale(1f, 1f, 1f);
    }

    public Matrix4_4f set(Matrix3_3f m) {
        arr[0] = m.m11;
        arr[1] = m.m21;
        arr[2] = m.m31;
        arr[3] = 0f;
        arr[4] = m.m12;
        arr[5] = m.m22;
        arr[6] = m.m32;
        arr[7] = 0f;
        arr[8] = m.m13;
        arr[9] = m.m23;
        arr[10] = m.m33;
        arr[11] = 0f;
        arr[12] = arr[13] = arr[14] = 0f;
        arr[15] = 1f;

        return this;
    }

    public final Matrix4_4f makeScale(float sx, float sy, float sz) {
        arr[0] = sx;
        arr[1] = arr[2] = arr[3] = arr[4] = 0f;
        arr[5] = sy;
        arr[6] = arr[7] = arr[8] = arr[9] = 0f;
        arr[10] = sz;
        arr[11] = arr[12] = arr[13] = arr[14] = 0f;
        arr[15] = 1f;
        return this;
    }

    public Matrix4_4f makeScale(float scale) {
        return makeScale(scale, scale, scale);
    }

    /**
     * сжатие-растяжение вдоль оси dirNorm
     */
    public Matrix4_4f makeScale(Vect3f dirNorm, float km) {
        float xy = dirNorm.x * dirNorm.y;
        float xz = dirNorm.x * dirNorm.z;
        float yz = dirNorm.y * dirNorm.z;
        km = km - 1;
        arr[0] = 1 + km * dirNorm.x * dirNorm.x;
        arr[1] = km * xy;
        arr[2] = km * xz;
        arr[3] = 0;
        arr[4] = km * xy;
        arr[5] = 1 + km * dirNorm.y * dirNorm.y;
        arr[6] = km * yz;
        arr[7] = 0;
        arr[8] = km * xz;
        arr[9] = km * yz;
        arr[10] = 1 + km * dirNorm.z * dirNorm.z;
        arr[11] = arr[12] = arr[13] = arr[14] = 0;
        arr[15] = 0;
        return this;
    }

    public Matrix4_4f makeRotation(float angleInDegrees, float nx, float ny, float nz) {
        Matrix.setRotateM(arr, 0, angleInDegrees, nx, ny, nz);
        return this;
    }

    public Matrix4_4f makeRotation(Quaternion q) {
        arr[0] = 1f - 2f * (q.y * q.y + q.z * q.z);
        arr[1] = 2f * (q.x * q.y + q.w * q.z);
        arr[2] = 2f * (q.x * q.z - q.w * q.y);
        arr[3] = 0;

        arr[4] = 2f * (q.x * q.y - q.w * q.z);
        arr[5] = 1f - 2f * (q.x * q.x + q.z * q.z);
        arr[6] = 2f * (q.y * q.z + q.w * q.x);
        arr[7] = 0;

        arr[8] = 2f * (q.x * q.z + q.w * q.y);
        arr[9] = 2f * (q.y * q.z - q.w * q.x);
        arr[10] = 1f - 2f * (q.x * q.x + q.y * q.y);
        arr[11] = 0;

        arr[12] = arr[13] = arr[14] = 0f;
        arr[15] = 1f;
        return this;
    }

    public void translate(Vect3f v) {
        arr[12] += v.x;
        arr[13] += v.y;
        arr[14] += v.z;
    }

    public void translate(float x, float y, float z){
        arr[12] += x;
        arr[13] += y;
        arr[14] += z;
    }

    public void translate(Vect3f v, float mul) {
        arr[12] += v.x * mul;
        arr[13] += v.y * mul;
        arr[14] += v.z * mul;
    }

    public void setTranslation(float x, float y, float z) {
        arr[12] = x;
        arr[13] = y;
        arr[14] = z;
    }

    public void setTranslation(Vect3f v, float mul) {
        arr[12] = v.x * mul;
        arr[13] = v.y * mul;
        arr[14] = v.z * mul;
    }

    public void getTranslate(Vect3f result) {
        result.set(arr[12], arr[13], arr[14]);
    }

    public boolean getInvert(Matrix4_4f inverted) {
        /*
        if (arr[3]==0f && arr[7]==0f && arr[11]==0f && arr[15]==1f){
            ... быстрое инвертирование для частного случая
        }
        */
        return Matrix.invertM(inverted.arr, 0, arr, 0);
    }

    public boolean invert() {
        return getInvert(this);
    }

    public boolean symmetric() {
        return symmetric(1f);
    }

    public boolean antisymmetric() {
        return symmetric(-1f);
    }

    public Matrix4_4f transpose() {
        swap(1, 4);
        swap(2, 8);
        swap(3, 12);
        swap(6, 9);
        swap(7, 13);
        swap(11, 14);
        return this;
    }

    private void swap(int i, int j) {
        float t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    /*
    * copy-paste code from Matrix.invertM()
    *
    * [0, 4, 8, 12]
    * [1, 5, 9, 13]
    * [2, 6, 10, 14]
    * [3, 7, 11, 15]
    */

    public float getDeterminant() {
        if (arr[3] == 0f && arr[7] == 0f && arr[11] == 0f) {
            //optimisation for trivial matrices
            return arr[15] * (
                    arr[0] * (arr[5] * arr[10] - arr[6] * arr[9]) -
                            arr[4] * (arr[1] * arr[10] - arr[2] * arr[9]) +
                            arr[8] * (arr[1] * arr[6] - arr[2] * arr[5]));
        }

        final float atmp0 = arr[10] * arr[15];
        final float atmp1 = arr[11] * arr[14];
        final float atmp2 = arr[9] * arr[15];
        final float atmp3 = arr[11] * arr[13];
        final float atmp4 = arr[9] * arr[14];
        final float atmp5 = arr[10] * arr[13];
        final float atmp6 = arr[8] * arr[15];
        final float atmp7 = arr[11] * arr[12];
        final float atmp8 = arr[8] * arr[14];
        final float atmp9 = arr[10] * arr[12];
        final float atmp10 = arr[8] * arr[13];
        final float atmp11 = arr[9] * arr[12];

        final float dst0 = (atmp0 * arr[5] + atmp3 * arr[6] + atmp4 * arr[7])
                - (atmp1 * arr[5] + atmp2 * arr[6] + atmp5 * arr[7]);
        final float dst1 = (atmp1 * arr[4] + atmp6 * arr[6] + atmp9 * arr[7])
                - (atmp0 * arr[4] + atmp7 * arr[6] + atmp8 * arr[7]);
        final float dst2 = (atmp2 * arr[4] + atmp7 * arr[5] + atmp10 * arr[7])
                - (atmp3 * arr[4] + atmp6 * arr[5] + atmp11 * arr[7]);
        final float dst3 = (atmp5 * arr[4] + atmp8 * arr[5] + atmp11 * arr[6])
                - (atmp4 * arr[4] + atmp9 * arr[5] + atmp10 * arr[6]);

        final float det =
                arr[0] * dst0 + arr[1] * dst1 + arr[2] * dst2 + arr[3] * dst3;

        return det;
    }

    private boolean symmetric(float sign) {
        return equals(arr[4], sign * arr[1]) &&
                equals(arr[8], sign * arr[2]) &&
                equals(arr[12], sign * arr[3]) &&
                equals(arr[9], sign * arr[6]) &&
                equals(arr[13], sign * arr[7]) &&
                equals(arr[14], sign * arr[11]);
    }

    /**
     * multiplication first 3 rows of matrix to vector (x,y,z,1)
     */
    public void mulAs3_4(Vect3f result, Vect3f v) {
        result.set(
                v.x * arr[0] + v.y * arr[4] + v.z * arr[8] + arr[12],
                v.x * arr[1] + v.y * arr[5] + v.z * arr[9] + arr[13],
                v.x * arr[2] + v.y * arr[6] + v.z * arr[10] + arr[14]);
    }

    /**
     * multiplication in homogeneous space
     * result = (x/w, y/w, z/w)
     */
    public void mul(Vect3f result, Vect3f v) {
        float m = 1 / (v.x * arr[3] + v.y * arr[7] + v.z * arr[11] + arr[15]);
        result.set(
                m * (v.x * arr[0] + v.y * arr[4] + v.z * arr[8] + arr[12]),
                m * (v.x * arr[1] + v.y * arr[5] + v.z * arr[9] + arr[13]),
                m * (v.x * arr[2] + v.y * arr[6] + v.z * arr[10] + arr[14]));
    }

    /**
     * multiplication up left 3*3 submatrix to vector
     */
    public void rotate(Vect3f result, Vect3f v) {
        result.set(
                v.x * arr[0] + v.y * arr[4] + v.z * arr[8],
                v.x * arr[1] + v.y * arr[5] + v.z * arr[9],
                v.x * arr[2] + v.y * arr[6] + v.z * arr[10]);
    }

    public void multiplication(Matrix4_4f first, Matrix4_4f second) {
        Matrix4_4f.multiply(this, first, second);
    }

    /**
     * [0, 4, 8, 12]
     * [1, 5, 9, 13]
     * [2, 6, 10, 14]
     * [3, 7, 11, 15]
     * <p/>
     * it gives array instead of copy of values,
     * so matrix changes when you change this array
     *
     * @return array of values
     */
    public float[] getArray() {
        return arr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[[");
        for (int i = 0; i < len; i++) {
            sb.append(arr[i]);
            if (i % 4 != 3) {
                sb.append(", ");
            } else {
                if (i!=15) {
                    sb.append("], [");
                } else {
                    sb.append("]]");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Matrix4_4f) {
            Matrix4_4f m = (Matrix4_4f) o;
            for (int i = 0; i < len; i++) {
                if (!equals(arr[i], m.arr[i])) {
                    return false;
                }
            }
            return true;
        }
        if (o instanceof Matrix3_3f) {
            return Helper.equals(this, (Matrix3_3f) o);
        }
        return false;
    }

    private static boolean equals(float f1, float f2) {
        return Helper.equals(f1, f2, Helper.matrix4_4fPrecision);
    }

    public static void multiply(Matrix4_4f result, Matrix4_4f left, Matrix4_4f right) {
        Matrix.multiplyMM(result.arr, 0, left.arr, 0, right.arr, 0);
    }
}
