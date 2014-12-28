package com.vk.lgorsl.NedoEngine.math;

/**
 * Created by lgor on 28.12.2014.
 */
public class Matrix2_2f implements iMatrix<Matrix2_2f, Vect2f> {

    public float
            m11, m12,
            m21, m22;

    public Matrix2_2f() {
        m11 = m22 = 1f;
    }

    public Matrix2_2f set(float m11, float m12, float m21, float m22) {
        this.m11 = m11;
        this.m12 = m12;
        this.m21 = m21;
        this.m22 = m22;
        return this;
    }

    public Matrix2_2f set(Matrix4_4f source) {
        float[] arr = source.getArray();
        return set(arr[0], arr[4], arr[1], arr[5]);
    }

    public Matrix2_2f set(Matrix3_3f source) {
        return set(source.m11, source.m12, source.m21, source.m22);
    }

    @Override
    public Matrix2_2f set(Matrix2_2f source) {
        return set(source.m11, source.m12, source.m21, source.m22);
    }

    @Override
    public Matrix2_2f makeIdentity() {
        return set(1, 0, 0, 1);
    }

    public void mul(Vect2f result, Vect2f v) {
        result.set(m11 * v.x + m12 * v.y, m21 * v.x + m22 * v.y);
    }

    public boolean invert() {
        return getInverted(this);
    }

    @Override
    public boolean symmetric() {
        return eq(m12, m21);
    }

    @Override
    public boolean antisymmetric() {
        return eq(m12, -m21);
    }

    public Matrix2_2f transpose() {
        float t = m12;
        m12 = m21;
        m21 = t;
        return this;
    }

    @Override
    public float getDeterminant() {
        return m11 * m22 - m12 * m21;
    }

    public Matrix2_2f multiplication(Matrix2_2f f, Matrix2_2f s) {
        this.set(f.m11 * s.m11 + f.m12 * s.m21, f.m11 * s.m12 + f.m12 * s.m22,
                f.m21 * s.m11 + f.m22 * s.m21, f.m21 * s.m12 + f.m22 * s.m22);
        return this;
    }

    public boolean getInverted(Matrix2_2f result) {
        final float det = getDeterminant();
        if (det == 0f) return false;
        final float mul = 1f / det;
        result.set(mul * m22, -m12 * mul, -m21 * mul, mul * m11);
        return true;
    }

    private static boolean eq(float f, float s) {
        return Math.abs(f - s) < Helper.matrix4_4fPrecision;
    }
}
