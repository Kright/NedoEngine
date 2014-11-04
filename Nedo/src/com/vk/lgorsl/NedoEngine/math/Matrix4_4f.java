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
    protected static float eps = 0.0001f;

    protected final float[] arr;

    public Matrix4_4f() {
        arr = new float[len];
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

    public Matrix4_4f makeRotate(float angleInDegrees, float nx, float ny, float nz) {
        Matrix.setRotateM(arr, 0, angleInDegrees, nx, ny, nz);
        return this;
    }

    public Matrix4_4f makeRotate(Quaternion q) {
        arr[0] = 1f - 2f * (q.y * q.y + q.z * q.z);
        arr[1] = 2f * (q.x * q.y - q.w * q.z);
        arr[2] = 2f * (q.x * q.z + q.w * q.y);
        arr[3] = 0;

        arr[4] = 2f * (q.x * q.y + q.w * q.z);
        arr[5] = 1f - 2f * (q.x * q.x + q.z * q.z);
        arr[6] = 2f * (q.y * q.z - q.w * q.x);
        arr[7] = 0;

        arr[8] = 2f * (q.x * q.z - q.w * q.y);
        arr[9] = 2f * (q.y * q.z + q.w * q.x);
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

    public void setTranslate(Vect3f v, float mul) {
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

    public boolean symmetric() {
        return symmetric(1f);
    }

    public boolean antisymmetric() {
        return symmetric(-1f);
    }

    public Matrix4_4f transpose(){
        swap(1, 4);
        swap(2, 8);
        swap(3, 12);
        swap(6, 9);
        swap(7, 13);
        swap(11, 14);
        return this;
    }

    private void swap(int i, int j){
        float t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    private boolean symmetric(float sign) {
        return equals(arr[4], sign * arr[1]) &&
                equals(arr[8], sign * arr[2]) &&
                equals(arr[12], sign * arr[3]) &&
                equals(arr[9], sign * arr[6]) &&
                equals(arr[13], sign * arr[7]) &&
                equals(arr[14], sign * arr[11]);
    }

    protected static boolean equals(float a, float b) {
        return Math.abs(a - b) < eps;
    }

    public void mul(Vect3f v, Vect3f result) {
        result.set(
                v.x * arr[0] + v.y * arr[4] + v.z * arr[8] + arr[12],
                v.x * arr[1] + v.y * arr[5] + v.z * arr[9] + arr[13],
                v.x * arr[2] + v.y * arr[6] + v.z * arr[10] + arr[14]);
    }

    public void rotate(Vect3f v, Vect3f result) {
        result.set(
                v.x * arr[0] + v.y * arr[4] + v.z * arr[8],
                v.x * arr[1] + v.y * arr[5] + v.z * arr[9],
                v.x * arr[2] + v.y * arr[6] + v.z * arr[10]);
    }

    public float[] getArray() {
        return arr;
    }

    public static void multiply(Matrix4_4f result, Matrix4_4f left, Matrix4_4f right) {
        Matrix.multiplyMM(result.arr, 0, left.arr, 0, right.arr, 0);
    }
}
