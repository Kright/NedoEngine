package com.vk.lgorsl.NedoEngine.math;

/**
 * abstract class for matrices
 *
 * Created by lgor on 28.12.2014.
 */
interface iMatrix<Matrix, Vect> {

    Matrix makeIdentity();

    Matrix set(Matrix source);

    boolean getInverted(Matrix result);

    boolean invert();

    boolean symmetric();

    boolean antisymmetric();

    Matrix transpose();

    float getDeterminant();

    void mul(Vect result, Vect v);

    Matrix multiplication(Matrix first, Matrix second);
}
