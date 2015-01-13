package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * chunk of landscape
 * <p>
 * Created by lgor on 06.01.2015.
 */
public class LandscapeChunk {

    private static WeakReference<float[]> tempArray = new WeakReference<>(new float[16]);

    public final Rectangle2i area = new Rectangle2i();
    public int width, height;

    public FloatBuffer vertices, normals;
    public ShortBuffer indices;

    private final int cellSize;

    public LandscapeChunk(int cellSize) {
        this.cellSize = cellSize;
    }

    public void set(iRectangle2i area, iLandscapeMap map, boolean generateIndices) {
        this.area.set(area);
        width = area.width() / cellSize + 1;
        height = area.height() / cellSize + 1;

        makeVerticesAndNormalsWithWeak(map, cellSize);
        if (generateIndices) {
            indices = makeIndices(width, height);
        }
    }

    /**
     * old, more simple realization of buffers creating
     * function create new temp arrays for each call
     * It is not used because of garbage collector
     */
    @Deprecated
    private void makeVerticesAndNormals(iLandscapeMap map, int cellSize) {
        float[] verts = new float[width * height * 3];
        float[] norms = new float[width * height * 3];
        int pos = 0;
        Point2i p = new Point2i();
        Vect3f n = new Vect3f();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                p.x = i * cellSize + area.xMin();
                p.y = j * cellSize + area.yMin();
                map.getNormal(p, n);
                n.putIntoArray(norms, pos);
                verts[pos++] = p.x;
                verts[pos++] = p.y;
                verts[pos++] = map.getHeight(p);
            }
        }
        vertices = GLHelper.make(verts);
        normals = GLHelper.make(norms);
    }

    private void makeVerticesAndNormalsWithWeak(iLandscapeMap map, int cellSize) {
        final int requiredLength = width * height * 6;
        float[] tempArr = tempArray.get();
        if (tempArr == null || tempArr.length < requiredLength) {
            tempArr = new float[requiredLength];
            tempArray = new WeakReference<>(tempArr);
        }
        int delta = width * height * 3;
        int pos = 0;
        Point2i p = new Point2i();
        Vect3f n = new Vect3f();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                p.x = i * cellSize + area.xMin();
                p.y = j * cellSize + area.yMin();
                map.getNormal(p, n);
                n.putIntoArray(tempArr, pos + delta);
                tempArr[pos++] = p.x;
                tempArr[pos++] = p.y;
                tempArr[pos++] = map.getHeight(p);
            }
        }
        vertices = GLHelper.makeFloatBuffer(width * height * 3);
        vertices.put(tempArr, 0, width * height * 3);
        vertices.position(0);

        normals = GLHelper.makeFloatBuffer(width * height * 3);
        normals.put(tempArr, delta, width * height * 3);
        normals.position(0);
    }

    public static ShortBuffer makeIndices(int width, int height) {
        short[] s = new short[(width - 1) * (height - 1) * 6];
        int pos = 0;
        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                short p1, p2, p3, p4;
                p1 = (short) (i + j * width);
                p2 = (short) (p1 + 1);
                p3 = (short) (i + (j + 1) * width);
                p4 = (short) (p3 + 1);
                s[pos++] = p1;
                s[pos++] = p3;
                s[pos++] = p2;
                s[pos++] = p2;
                s[pos++] = p3;
                s[pos++] = p4;
            }
        }
        return GLHelper.make(s);
    }
}
