package com.vk.lgorsl.NedoEngine.math;

import android.util.FloatMath;

/**
 * three - dimensional float vector
 *
 * Created by lgor on 04.11.2014.
 */
public class Vect3f extends VectNf<Vect3f> {

    public float x, y, z;

    public Vect3f() {
    }

    @Override
    public Vect3f set(Vect3f example) {
        x = example.x;
        y = example.y;
        z = example.z;
        return this;
    }

    @Override
    public void apply(Unary op) {
        x = op.apply(x);
        y = op.apply(y);
        z = op.apply(z);
    }

    @Override
    public void apply(Binary op, Vect3f first, Vect3f second) {
        x = op.apply(first.x, second.x);
        y = op.apply(first.y, second.y);
        z = op.apply(first.z, second.z);
    }

    public Vect3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public void mul(float mul) {
        x *= mul;
        y *= mul;
        z *= mul;
    }

    @Override
    public void add(Vect3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    @Override
    public void add(Vect3f v, float mul) {
        x += v.x * mul;
        y += v.y * mul;
        z += v.z * mul;
    }

    @Override
    public void sub(Vect3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    @Override
    public float length2() {
        return x * x + y * y + z * z;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Vect3f v = (Vect3f) o;
            return v.x == x && v.y == y && v.z == z;
        } catch (ClassCastException ex) {
            return false;
        }
    }

    @Override
    public float dot(Vect3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    @Override
    public float distance(Vect3f v) {
        float dx = x - v.x;
        float dy = y - v.y;
        float dz = z - v.z;
        return FloatMath.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public int putIntoArray(float[] array, int pos) {
        array[pos++] = x;
        array[pos++] = y;
        array[pos++] = z;
        return pos;
    }

    public static void cross(Vect3f result, Vect3f f, Vect3f s) {
        result.set(
                f.y * s.z - f.z * s.y,
                f.z * s.x - f.x * s.z,
                f.x * s.y - f.y * s.x);
    }

    @Override
    public String toString() {
        return "["+x+','+y+','+z+']';
    }
}
