package com.vk.lgorsl.NedoEngine.math;

import android.util.FloatMath;

/**
 * abstract class of N-dimensional vector
 *
 * Created by lgor on 04.11.2014.
 */
abstract class VectNf<T extends VectNf<T>> implements Operable<T> {

    /**
     * this.coordinates = vect.coordinates;
     * @return itself
     */
    public abstract T set(T vect);

    /**
     * for each coordinate from this
     * this.c = op.unary(this.c);
     */
    public abstract void apply(Unary op);

    /**
     * for each coordinate from this
     * this.c = op.binary(first.c, second.c);
     */
    public abstract void apply(Binary op, T first, T second);

    /**
     * multiply vector to a single value
     */
    public abstract void mul(float mul);

    /**
     * this += vect * mul;
     */
    public abstract void add(T vect, float mul);

    /**
     * this += vect;
     */
    public abstract void add(T vect);

    /**
     * this -= vect;
     */
    public abstract void sub(T vect);

    /**
     * set length == 1
     */
    public final void normalize(){
        mul(1f / length());
    }

    /**
     * @param length desired length of vector
     */
    public final void setLength(float length){
        mul(length / length());
    }

    /**
     * @return length of the vector
     */
    public final float length(){
        return FloatMath.sqrt(length2());
    }

    /**
     * @return vector dotted itself
     */
    public abstract float length2();

    /**
     * @return result of dot multiplication
     */
    public abstract float dot(T vect);

    /**
     * @return distance between two vectors (or length of subtraction)
     */
    public abstract float distance(T vect);

    /**
     * @return cosine of angle between vectors
     */
    public float cos(T vect){
        return this.dot(vect)/FloatMath.sqrt(this.length2() * vect.length2());
    }

    /**
     * @param vect vector, which projection we interested
     * @param normal have to be with identity length
     */
    public void setAsProjectionToNormal(T vect, T normal){
        float len = vect.dot(normal);
        set(normal).mul(len);
    }

    /**
     * normal may have any nonzero length
     */
    public void setAsProjection(T vect, T normal){
        float len = vect.dot(normal) / normal.length2();
        set(normal).mul(len);
    }

    /**
     * @param array of float for storing coordinates
     * @param pos - position
     * @return new position in array (pos+2 for 2d vector, for example)
     */
    public abstract int putIntoArray(float[] array, int pos);
}
