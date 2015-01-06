package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.cossacks.world.interfaces.iLandscapeMap;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * chunk of landscape
 * <p>
 * Created by lgor on 06.01.2015.
 */
public class LandscapeChunk {

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

        makeVerticesAndNormals(map, cellSize);
        if (generateIndices){
            indices = makeIndices(width, height);
        }
    }

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
