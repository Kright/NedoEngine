package com.vk.lgorsl.NedoEngine.math;

/**
 * класс матрицы 3*3
 *
 * методы вида make* создают определённую матрицу ВМЕСТО текущей.
 *
 * Created by lgor on 04.11.2014.
 */
public class Matrix3_3f implements iMatrix<Matrix3_3f, Vect3f>{

    protected static float eps = 0.0001f;

    public float
            m11, m12, m13,
            m21, m22, m23,
            m31, m32, m33;

    // first row - x axis vector, second - y, third - z

    public Matrix3_3f() {
        m11 = m22 = m33 = 1f;
    }

    public Matrix3_3f makeIdentity() {
        m11 = m22 = m33 = 1f;
        m12 = m13 = m21 = m23 = m31 = m32 = 0f;
        return this;
    }

    public Matrix3_3f set(Matrix3_3f mat) {
        m11 = mat.m11;
        m12 = mat.m12;
        m13 = mat.m13;
        m21 = mat.m21;
        m22 = mat.m22;
        m23 = mat.m23;
        m31 = mat.m31;
        m32 = mat.m32;
        m33 = mat.m33;
        return this;
    }

    public Matrix3_3f set(float s11, float s12, float s13, float s21, float s22, float s23, float s31, float s32, float s33){
        m11 = s11;
        m12 = s12;
        m13 = s13;
        m21 = s21;
        m22 = s22;
        m23 = s23;
        m31 = s31;
        m32 = s32;
        m33 = s33;
        return this;
    }

    /**
     * set to this matrix values from left top sub-matrix 3*3
     */
    public Matrix3_3f set(Matrix4_4f mat){
        float[] arr = mat.getArray();
        m11 = arr[0];
        m12 = arr[4];
        m13 = arr[8];
        m21 = arr[1];
        m22 = arr[5];
        m23 = arr[9];
        m31 = arr[2];
        m32 = arr[6];
        m33 = arr[10];
        return this;
    }

    /**
     * multiply first and second matrices and write result to this
     * @return this
     */
    public Matrix3_3f multiplication(Matrix3_3f first, Matrix3_3f second){
        Matrix3_3f.multiply(this, first, second);
        return this;
    }

    /**
     * надо будет проверить.
     * если не в ту сторону, то можно транспонировать матрицу
     */
    public Matrix3_3f makeRotation(Quaternion q) {
        m11 = 1f - 2f * (q.y * q.y + q.z * q.z);
        m12 = 2f * (q.x * q.y - q.w * q.z);
        m13 = 2f * (q.x * q.z + q.w * q.y);

        m21 = 2f * (q.x * q.y + q.w * q.z);
        m22 = 1f - 2f * (q.x * q.x + q.z * q.z);
        m23 = 2f * (q.y * q.z - q.w * q.x);

        m31 = 2f * (q.x * q.z - q.w * q.y);
        m32 = 2f * (q.y * q.z + q.w * q.x);
        m33 = 1f - 2f * (q.x * q.x + q.y * q.y);

        return this;
    }

    public Matrix3_3f transpose() {
        float t;
        t = m12;
        m12 = m21;
        m21 = t;
        t = m13;
        m13 = m31;
        m31 = t;
        t = m32;
        m32 = m23;
        m23 = t;
        return this;
    }

    public final float getDeterminant() {
        return m11 * (m22 * m33 - m23 * m32)
                + m12 * (m23 * m31 - m21 * m33)
                + m13 * (m21 * m32 - m22 * m31);
    }

    public void mul(Vect3f result, Vect3f v) {
        result.set(
                m11 * v.x + m12 * v.y + m13 * v.z,
                m21 * v.x + m22 * v.y + m23 * v.z,
                m31 * v.x + m32 * v.y + m33 * v.z);
    }

    public void mul(Vect2f result, Vect2f v){
        result.set(
                m11 * v.x + m12* v.y + m13 ,
                m21 * v.x + m22 *v.y + m23);
    }

    public void multiply(float m) {
        m11 *= m;
        m12 *= m;
        m13 *= m;
        m21 *= m;
        m22 *= m;
        m23 *= m;
        m31 *= m;
        m32 *= m;
        m33 *= m;
    }

    public final boolean antisymmetric() {
        return eq(m12, -m21) && eq(m13, -m31) && eq(m23, -m32);
    }

    public final boolean symmetric() {
        return eq(m12, m21) && eq(m13, m31) && eq(m23, m32);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Matrix3_3f){
            Matrix3_3f f = (Matrix3_3f)o;
            return eq(m11, f.m11) &&
                    eq(m12, f.m12) &&
                    eq(m13, f.m13) &&
                    eq(m21, f.m21) &&
                    eq(m22, f.m22) &&
                    eq(m23, f.m23) &&
                    eq(m31, f.m31) &&
                    eq(m32, f.m32) &&
                    eq(m33, f.m33);
        }
        return (o instanceof Matrix4_4f) && Helper.equals((Matrix4_4f)o, this);
    }

    private static boolean eq(float f1, float f2) {
        return Helper.equals(f1, f2, Helper.matrix4_4fPrecision);
    }

    /**
     * @return false for singular matrix
     */
    public boolean getInverted(Matrix3_3f result){
        //ищем миноры
        float mi11 = m22 * m33 - m23 * m32;
        float mi12 = m21 * m33 - m23 * m31;
        float mi13 = m21 * m32 - m22 * m31;

        final float determinant = m11 * mi11 - m12 * mi12 + m13 * mi13;
        if (determinant == 0f) return false;
        final float m = 1 / determinant;

        float mi21 = m12 * m33 - m32 * m13;
        float mi22 = m11 * m33 - m13 * m31;
        float mi23 = m11 * m32 - m31 * m12;

        float mi31 = m12 * m23 - m22 * m13;
        float mi32 = m11 * m23 - m21 * m13;
        float mi33 = m11 * m22 - m21 * m12;

        //транспонируем, меняем знаки, делим на детерминант.
        result.m11 = mi11 * m;
        result.m12 = -mi21 * m;
        result.m13 = mi31 * m;
        result.m21 = -mi12 * m;
        result.m22 = mi22 * m;
        result.m23 = -mi32 * m;
        result.m31 = mi13 * m;
        result.m32 = -mi23 * m;
        result.m33 = mi33 * m;

        return true;
    }

    /**
     * @return false for singular matrix
     */
    public boolean invert() {
        return getInverted(this);
    }

    public void setColumn(int number, Vect3f column){
        switch (number){
            case 0:
                m11 = column.x;
                m21 = column.y;
                m31 = column.z;
                break;
            case 1:
                m12 = column.x;
                m22 = column.y;
                m32 = column.z;
                break;
            case 2:
                m13 = column.x;
                m23 = column.y;
                m33 = column.z;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException("this matrix has only [0], [1], [2] columns");
        }
    }

    /*
        m11, m12, m13,
        m21, m22, m23,
        m31, m32, m33;
    */
    public static void multiply(Matrix3_3f result, Matrix3_3f f, Matrix3_3f s) {
        float m11 = f.m11 * s.m11 + f.m12 * s.m21 + f.m13 * s.m31;
        float m12 = f.m11 * s.m12 + f.m12 * s.m22 + f.m13 * s.m32;
        float m13 = f.m11 * s.m13 + f.m12 * s.m23 + f.m13 * s.m33;

        float m21 = f.m21 * s.m11 + f.m22 * s.m21 + f.m23 * s.m31;
        float m22 = f.m21 * s.m12 + f.m22 * s.m22 + f.m23 * s.m32;
        float m23 = f.m21 * s.m13 + f.m22 * s.m23 + f.m23 * s.m33;

        float m31 = f.m31 * s.m11 + f.m32 * s.m21 + f.m33 * s.m31;
        float m32 = f.m31 * s.m12 + f.m32 * s.m22 + f.m33 * s.m32;
        float m33 = f.m31 * s.m13 + f.m32 * s.m23 + f.m33 * s.m33;

        result.m11 = m11;
        result.m12 = m12;
        result.m13 = m13;
        result.m21 = m21;
        result.m22 = m22;
        result.m23 = m23;
        result.m31 = m31;
        result.m32 = m32;
        result.m33 = m33;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[[").
                append(m11).append(",").
                append(m12).append(",").
                append(m13).append("], [").
                append(m21).append(",").
                append(m22).append(",").
                append(m23).append("], [").
                append(m31).append(",").
                append(m32).append(",").
                append(m33).append("]]");
        return sb.toString();
    }
}
