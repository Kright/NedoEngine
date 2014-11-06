package com.vk.lgorsl.NedoEngine.math;

import android.util.FloatMath;

/**
 * Created by lgor on 04.11.2014.
 * <p/>
 * I used the book :
 * F. Dunn, I. Parberry - 3D Math Primer for Graphics and Game Development
 */
public class Quaternion {

    private final static float eps = 0.00001f;
    private final static float linearCriteria = 0.5f;

    public float w;
    public float x, y, z;

    /**
     * make identity quaternion
     */
    public Quaternion() {
        w = 1f;
    }

    public Quaternion set(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Quaternion set(Quaternion q) {
        return set(q.w, q.x, q.y, q.z);
    }

    public Quaternion set(Vect3f axis, float angle) {
        angle *= 0.5f;
        float m = FloatMath.sin(angle) / axis.length();
        return set(FloatMath.cos(angle), axis.x * m, axis.y * m, axis.z * m);
    }

    public Quaternion setIdentity() {
        return set(1f, 0f, 0f, 0f);
    }

    /**
     * @param v - exponent (length is 0.5*angle of rotation)
     * @return itself
     */
    public Quaternion setFromExp(Vect3f v) {
        float l = v.length();
        float m = FloatMath.sin(l) / l;
        return set(FloatMath.cos(l), v.x * m, v.y * m, v.z * m);
    }

    /**
     * @param result - rotation axis with length of 0.5*angle in radians
     */
    public void getLog(Vect3f result) {
        float sin = FloatMath.sqrt(x * x + y * y + z * z);
        float angle2 = (float) Math.atan2(sin, w);
        result.x = angle2 * x;
        result.y = angle2 * y;
        result.z = angle2 * z;
    }

    /**
     * @return angle from [0..2*pi]
     */
    public float getRotationAngle() {
        float l = length();
        if (l < eps) return 0;
        return 2f * (float) Math.acos(w / l);
    }

    public float getRotationAngleFast() {
        return 2f * (float) Math.acos(w);
    }

    public void getRotationAxis(Vect3f result) {
        result.set(x, y, z).normalize();
    }

    public final float length() {
        return FloatMath.sqrt(w * w + x * x + y * y + z * z);
    }

    public final float lengthXYZ() {
        return FloatMath.sqrt(x * x + y * y + z * z);
    }

    public void conjugate() {
        x = -x;
        y = -y;
        z = -z;
        //возможно, лучше делать w = -w вместо этого
    }

    public void negate() {
        w = -w;
        x = -x;
        y = -y;
        z = -z;
    }

    private void mul(float m) {
        w *= m;
        x *= m;
        y *= m;
        z *= m;
    }

    public void normalize() {
        float l = length();
        if (l < eps) {  //Houston, we have a problem
            setIdentity();
            return;
        }
        mul(1 / l);
    }

    public void pow(float pow) {
        float l = lengthXYZ();
        if (eq(l, 0)) return;
        float alpha = pow * (float) Math.atan2(l, w);
        float m = FloatMath.sin(alpha) / l * length();
        set(FloatMath.cos(alpha), x * m, y * m, z * m);
    }

    public float dot(Quaternion q) {
        return w * q.w + x * q.x + y * q.y + z * q.z;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Quaternion)) return false;
        Quaternion q = (Quaternion) o;
        if (eq(w, q.w)) {
            return eq(x, q.x) && eq(y, q.y) && eq(z, q.z);
        }
        return eq(w, -q.w) && eq(x, -q.x) && eq(y, -q.y) && eq(z, -q.z);
    }

    private static boolean eq(float a, float b) {
        return Math.abs(a - b) < eps;
    }

    @Override
    public String toString() {
        return "[w=" + w + ", x=" + x + ", y=" + y + ", z=" + z + "]";
    }

    public static void multiply(Quaternion result, Quaternion first, Quaternion second) {
        result.set(first.w * second.w - first.x * second.x - first.y * second.y - first.z * second.z,
                first.w * second.x + first.x * second.w + first.y * second.z - first.z * second.y,
                first.w * second.y + first.y * second.w + first.z * second.x - first.x * second.z,
                first.w * second.z + first.z * second.w + first.x * second.y - first.y * second.x);
    }

    public static void mix(Quaternion q1, float k1, Quaternion q2, float k2, Quaternion result) {
        result.set(
                q1.w * k1 + q2.w * k2,
                q1.x * k1 + q2.x * k2,
                q1.y * k1 + q2.y * k2,
                q1.z * k1 + q2.z * k2);
    }

    /**
     * spherical linear interpolation.
     * works only for normalized quaternions
     */
    public static void slerp(Quaternion q1, Quaternion q2, float t, Quaternion result) {
        if (t < 0) {
            result.set(q1);
            return;
        }
        if (t > 1) {
            result.set(q2);
            return;
        }
        float cosOmega = q1.dot(q2);
        final float sign = cosOmega >= 0 ? 1f : -1f;
        cosOmega *= sign;
        //if angle small, cosine is big, and we use linear interpolation
        if (cosOmega > linearCriteria) {
            mix(q1, t * sign, q2, 1 - t, result);
            return;
        }
        //copy-paste code from page 213
        final float sinOmega = FloatMath.sqrt(1 - cosOmega * cosOmega);
        final float omega = (float) Math.atan2(sinOmega, cosOmega);
        final float mult = 1f / sinOmega;

        final float k0 = FloatMath.sin((1f - t) * omega) * mult;
        final float k1 = FloatMath.sin(t * omega) * mult;

        mix(q1, k0 * sign, q2, k1, result);
    }

    /**
     * linear interpolation. Really faster then spherical
     */
    public static void lerp(Quaternion q1, Quaternion q2, float t, Quaternion result) {
        if (q1.dot(q2) >= 0)
            mix(q1, t, q2, 1 - t, result);
        else
            mix(q1, t, q2, t - 1, result);
    }
}
