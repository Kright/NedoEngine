package com.vk.lgorsl.NedoEngine.math;

/**
 * functional-style ))
 *
 * Created by lgor on 04.11.2014.
 */
public interface Operable<T> {

    public interface Unary{
        float apply(float f);
    }

    public interface Binary{
        float apply(float first, float second);
    }

    public void apply(Unary op);

    public void apply(Binary op, T first, T second);
}
