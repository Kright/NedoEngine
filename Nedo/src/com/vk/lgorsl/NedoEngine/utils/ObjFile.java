package com.vk.lgorsl.NedoEngine.utils;

import com.vk.lgorsl.NedoEngine.math.Vect2f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * representation of obj file
 * <p>
 * Created by lgor on 02.01.2015.
 */
public class ObjFile {

    public static class Face {
        VertexData[] data;
    }

    public static class VertexData {

        public int v, vt, vn;

        public VertexData(int v, int vt, int vn) {
            this.v = v;
            this.vt = vt;
            this.vn = vn;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof VertexData) {
                VertexData d = (VertexData) o;
                return v == d.v && vt == d.vt && vn == d.vn;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (v << 24) ^ (vt << 16) ^ (vn << 8) ^ v ^ vt ^ vn;
        }
    }

    public String name;
    public final List<Vect3f> vertices = new ArrayList<>();
    public final List<Vect3f> normals = new ArrayList<>();
    public final List<Vect2f> textureCoords = new ArrayList<>();
    public final List<Face> faces = new ArrayList<>();
    public final List<String> notParsed = new ArrayList<>();

    public int trianglesCount() {
        int count = 0;
        for (ObjFile.Face f : faces) {
            count += f.data.length - 2;
        }
        return count;
    }

    public int floatsPerVertex() {
        int count = 0;
        VertexData vd = faces.get(0).data[0];
        if (vd.v != 0) count += 3;
        if (vd.vn != 0) count += 3;
        if (vd.vt != 0) count += 2;
        return count;
    }

    public ModelData makeData() {

        int count = 0;
        HashMap<VertexData, Integer> map = new HashMap<>();
        List<VertexData> lst = new ArrayList<>();
        for (Face f : faces) {
            for (VertexData vd : f.data) {
                if (!map.containsKey(vd)) {
                    map.put(vd, count);
                    lst.add(vd);
                    count++;
                }
            }
        }
        ModelData result = new ModelData(count, floatsPerVertex(), trianglesCount() * 3);

        int pos = 0;
        for (VertexData vd : lst) {
            pos = putVertexData(vd, result.data, pos);
        }

        pos = 0;
        for (ObjFile.Face f : faces) {
            for (int i = 0; i < f.data.length - 2; i++) {
                result.indices[pos++] = (short) (int) map.get(f.data[0]);  //always first element
                result.indices[pos++] = (short) (int) map.get(f.data[i + 1]);
                result.indices[pos++] = (short) (int) map.get(f.data[i + 2]);
            }
        }

        return result;
    }

    private int putVertexData(VertexData vd, float[] arr, int pos) {
        int num;
        num = vd.v - 1;
        if (num >= 0) {
            pos = vertices.get(num).putIntoArray(arr, pos);
        }
        num = vd.vt - 1;
        if (num >= 0) {
            pos = textureCoords.get(num).putIntoArray(arr, pos);
        }
        num = vd.vn - 1;
        if (num >= 0) {
            pos = normals.get(num).putIntoArray(arr, pos);
        }
        return pos;
    }

    public ModelData simpleMakeData() {
        ModelData result = new ModelData(trianglesCount() * 3, floatsPerVertex(), trianglesCount() * 3);
        int pos = 0;
        for (ObjFile.Face f : faces) {
            pos = putVertexData(f.data[0], result.data, pos);
            pos = putVertexData(f.data[1], result.data, pos);
            pos = putVertexData(f.data[2], result.data, pos);
        }
        for (int i = 0; i < result.indices.length; i++) {
            result.indices[i] = (short) i;
        }
        return result;
    }
}
