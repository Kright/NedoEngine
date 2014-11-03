package com.vk.lgorsl.NedoEngine.math;

import android.util.FloatMath;

/**
 * abstract class of N-dimensional vector
 *
 * Created by lgor on 04.11.2014.
 */
abstract class VectNf {

    /**
     * multiply vector to a single value
     */
    public abstract void mul(float mul);

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
     * @param array of float for storing coordinates
     * @param pos - position
     * @return new position in array (pos+2 for 2d vector, for example)
     */
    public abstract int putIntoArray(float[] array, int pos);
}
