package com.vk.lgorsl.NedoEngine.math;

import android.util.FloatMath;

/**
 * 2-dimensional float vector
 *
 * Created by lgor on 04.11.2014.
 */
public class Vect2f extends VectNf<Vect2f> {

    public float x, y;

    public Vect2f() {
    }

    @Override
    public Vect2f set(Vect2f example) {
        x = example.x;
        y = example.y;
        return this;
    }

    @Override
    public void apply(Unary op) {
        x = op.apply(x);
        y = op.apply(y);
    }

    @Override
    public void apply(Binary op, Vect2f first, Vect2f second) {
        x = op.apply(first.x, second.x);
        y = op.apply(first.y, second.y);
    }

    public Vect2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public void mul(float mul) {
        x *= mul;
        y *= mul;
    }

    @Override
    public void add(Vect2f v) {
        x += v.x;
        y += v.y;
    }

    @Override
    public void add(Vect2f v, float mul) {
        x += v.x * mul;
        y += v.y * mul;
    }

    @Override
    public void sub(Vect2f v) {
        x -= v.x;
        y -= v.y;
    }

    @Override
    public float length2() {
        return x * x + y * y;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Vect2f v = (Vect2f) o;
            return v.x == x && v.y == y;
        } catch (ClassCastException ex) {
            return false;
        }
    }

    @Override
    public float dot(Vect2f v) {
        return x * v.x + y * v.y;
    }

    /**
     * it is looks like cross product of 3d vectors
     * @return x * v.y - y * v.x
     */
    public float cross(Vect2f v) {
        return x * v.y - y * v.x;
    }

    public float distance(Vect2f v){
        float dx = x - v.x;
        float dy = y - v.y;
        return FloatMath.sqrt(dx * dx + dy * dy);
    }

    @Override
    public int putIntoArray(float[] array, int pos) {
        array[pos++] = x;
        array[pos++] = y;
        return pos;
    }

    @Override
    public String toString() {
        return "["+x+','+y+']';
    }
}
