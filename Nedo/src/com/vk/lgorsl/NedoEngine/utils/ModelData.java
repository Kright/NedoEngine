package com.vk.lgorsl.NedoEngine.utils;

/**
 * couple of data in floats and indices in shorts
 *
 * Created by lgor on 02.01.2015.
 */
public class ModelData {

    public float[] data;
    public int stride;

    public short[] indices;

    public ModelData(int vertsCount, int floatsPerVertex, int indicesCount){
        data = new float[vertsCount * floatsPerVertex];
        stride = 4 * floatsPerVertex;
        indices = new short[indicesCount];
    }
}
