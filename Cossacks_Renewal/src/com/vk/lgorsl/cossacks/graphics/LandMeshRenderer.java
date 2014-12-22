package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Renders land mesh
 * Created by lgor on 21.12.2014.
 */
public class LandMeshRenderer implements GameRenderable {

    private CleverShader shader;
    private CleverShader shadowShader;

    boolean loaded = false;
    public FloatBuffer fb;  //vertices
    public FloatBuffer fbn; //normals
    public ShortBuffer sb;

    @Override
    public void load(LoadedData loadedData) {
        if (!loaded) {

            shader = new CleverShader(
                    "uniform mat4 uMatrix;" +
                            "attribute vec3 aPosition;" +
                            "varying float vH;" +
                            "void main(){" +
                            "   gl_Position = uMatrix * vec4(aPosition.xyz, 1.0);" +
                            "   vH = aPosition.z * 0.0005;" +
                            "}",
                    "uniform vec4 uColor;" +
                            "varying float vH;" +
                            "void main(){" +
                            "   gl_FragColor = uColor*(0.6+vH);" +
                            "}");

            shadowShader = new CleverShader("uniform mat4 uMatrix;\n" +
                    "uniform mat4 uMatrixShadow;\n" +
                    "attribute vec3 aPosition;\n" +
                    "attribute vec3 aNormal;" +
                    "varying vec4 vInv;\n" +
                    "varying vec3 vNormal;\n" +
                    "varying float vH;\n" +
                    "void main(){\n" +
                    "   gl_Position = uMatrix * vec4(aPosition.xyz, 1.0);\n" +
                    "   vInv.xyz = (uMatrixShadow * vec4(aPosition.xyz, 1.0)).xyz;\n" +
                    "   vNormal = aNormal;\n" +
                    "   if (abs(vInv.x)>1.0 || abs(vInv.y)>1.0 ){ vNormal *= 0.0; }\n" +
                    "   vInv.xy = vInv.xy*0.5+vec2(0.5, 0.5);\n" +
                    "   vInv.w = aPosition.z*0.0002;\n" +
                    "}",
                    "precision mediump float;\n" +
                            "uniform vec4 uColor;\n" +
                            "uniform vec3 uLightDirection;\n" +
                            "uniform float uEps;\n" +
                            "uniform sampler2D uDepthMap; \n" +
                            "varying vec4 vInv;\n" +
                            "varying vec3 vNormal;\n" +
                            "float unpack(vec4 c){\n" +
                            "    const vec4 BitShifts = vec4( 1.0, 1.0/256.0, 1.0/(256.0*256.0), 1.0/(256.0*256.0*256.0) );\n" +
                            "    return -1.0+2.0*dot(c, BitShifts);\n" +
                            "}\n" +
                            "float Unpack(vec4 Value)\n" +
                            "{\n" +
                            "    const vec4 BitShifts = vec4( 1.0 / (256.0 * 256.0 * 256.0), 1.0 / (256.0 * 256.0), 1.0 / 256.0, 1.0 );\n" +
                            "    return 2.0*dot( Value, BitShifts )-1.0;\n" +
                            "}\n" +
                            "void main(){\n" +
                            "   float nn = -dot(vNormal, uLightDirection);\n" +
                            "   if (nn <= 0.0){\n" +
                            "       gl_FragColor = uColor*(0.2+vInv.w);\n" +
                            "       return;\n" +
                            "   }\n" +
                            "   float depth = uEps + Unpack(texture2D(uDepthMap, vInv.xy));\n" +
                            "   if (depth < vInv.z){\n" +
                            "       gl_FragColor = uColor*(0.2+vInv.w);\n" +
                            "   } else {\n" +
                            "       gl_FragColor = uColor*(0.2+nn*0.4+vInv.w);\n" +
                            "   }\n" +
                            "}\n");

            loaded = true;
        }
    }

    private void createGrid(RendererParams params) {
        HeightGrid grid = params.world.heightGrid;
        float scale = params.world.metrics.meterSize();
        float meterSize = params.world.metrics.meterSize();

        float[] f = new float[grid.data.length * 3];
        for (int i = 0; i < grid.data.length; i++) {
            int x = i % grid.width;
            int y = i / grid.width;
            f[3 * i] = scale * (x + (y % 2 == 1 ? 0.5f : 0f));
            f[3 * i + 1] = scale * y;
            f[3 * i + 2] = scale * grid.data[i] / meterSize;
        }

        fb = GLHelper.make(f);
        sb = generateIndices(grid);
        fbn = generateNormals(meterSize, grid);

        params.meshVertices = fb;
        params.meshIndices = sb;
        params.meshNormals = fbn;
    }

    private ShortBuffer generateIndices(HeightGrid grid){
        short[] s = new short[(grid.height - 1) * (grid.width - 1) * 2 * 3];
        int pos = 0;
        for (int y = 0; y < grid.height - 1; y++) {
            for (int x = 0; x < grid.width - 1; x++) {
                int num = x + y * grid.width;
                if (y % 2 == 0) {
                    s[pos++] = (short) (num);
                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width);

                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width + 1);
                    s[pos++] = (short) (num + grid.width);
                } else {
                    s[pos++] = (short) num;
                    s[pos++] = (short) (num + grid.width + 1);
                    s[pos++] = (short) (num + grid.width);

                    s[pos++] = (short) (num);
                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width + 1);
                }
            }
        }
        return GLHelper.make(s);
    }

    private FloatBuffer generateNormals(float meterSize, HeightGrid grid) {
        float[] fn = new float[grid.data.length * 3];
        Vect3f normal = new Vect3f();
        Vect3f add = new Vect3f();
        for (int i = 0; i < grid.data.length; i++) {
            int x = i % grid.width;
            int y = i / grid.width;
            normal.set(0, 0, 0);

            int d = get(grid, x - 1, y) - get(grid, x + 1, y);
            add.set(d, 0, meterSize).normalize();
            normal.add(add);

            if (y % 2 == 0) {
                d  = get(grid, x-1, y-1) - get(grid, x, y+1) + get(grid, x, y-1) - get(grid, x-1, y+1);
            } else {
                d = get(grid, x, y-1) - get(grid, x+1, y+1) + get(grid, x+1, y-1) - get(grid, x, y+1);
            }
            add.set(0, d, meterSize*2).normalize();
            normal.add(add);
            normal.normalize();
            normal.putIntoArray(fn, i*3);
        }
        return GLHelper.make(fn);
    }

    private int get(HeightGrid grid, int x, int y) {
        if (x < 0) x++;
        if (y < 0) y++;
        if (x >= grid.width) x = grid.width - 1;
        if (y >= grid.height) y = grid.height - 1;
        return grid.data[x + y * grid.width];
    }

    @Override
    public void render(RendererParams params) {
        //crazy code, I will fix it
        load(null);
        if (sb == null) {
            createGrid(params);
        }

        glClearColor(0, 0, 0, 0);
        glClearDepthf(1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (!params.lightningRendering) {
            shader.useProgram();

            glUniformMatrix4fv(shader.get("uMatrix"), 1, false,
                    params.mapView.projection().getArray(), 0);

            glUniform4f(shader.get("uColor"), 0, 1f, 0, 1f);

            shader.enableAllVertexAttribArray();
            glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false,
                    0, fb);

            glDrawElements(GL_TRIANGLES, sb.capacity(), GL_UNSIGNED_SHORT, sb);

            shader.disableAllVertexAttribArray();
        } else {
            shadowShader.useProgram();

            glUniformMatrix4fv(shadowShader.get("uMatrix"), 1, false,
                    params.mapView.projection().getArray(), 0);
            glUniformMatrix4fv(shadowShader.get("uMatrixShadow"), 1, false,
                    params.lightningView.projection().getArray(), 0);

            glUniform4f(shadowShader.get("uColor"), 0.3f, 1f, 0.2f, 1f);
            glUniform1f(shadowShader.get("uEps"), 0.01f);

            params.depthTexture.use(0);
            glUniform1i(shadowShader.get("uDepthMap"), 0);

            Vect3f dir = params.lightDirection;
            glUniform3f(shadowShader.get("uLightDirection"), dir.x, dir.y, dir.z);

            GLHelper.checkError();
            shadowShader.enableAllVertexAttribArray();
            glVertexAttribPointer(shadowShader.get("aPosition"), 3, GL_FLOAT, false, 0, fb);
            glVertexAttribPointer(shadowShader.get("aNormal"), 3, GL_FLOAT, false, 0, fbn);

            glDrawElements(GL_TRIANGLES, sb.capacity(), GL_UNSIGNED_SHORT, sb);

            shadowShader.disableAllVertexAttribArray();
        }
    }
}