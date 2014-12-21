package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.cossacks.world.realizations.HeightGrid;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Renders land mesh
 *
 * Created by lgor on 21.12.2014.
 */
public class LandMeshRenderer implements GameRenderable {

    private CleverShader shader;
    private CleverShader shadowShader;
    boolean loaded = false;
    public FloatBuffer fb;
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
                    "varying vec4 vInv;\n" +
                    "varying float vH;\n" +
                    "void main(){\n" +
                    "   gl_Position = uMatrix * vec4(aPosition.xyz, 1.0);\n" +
                    "   vInv.xyz = (uMatrixShadow * vec4(aPosition.xyz, 1.0)).xyz;\n" +
                    "   vInv.xy = vInv.xy*0.5+vec2(0.5, 0.5);\n"+
                    "   vInv.w = aPosition.z*0.0005;\n" +
                    "}",
                    "uniform vec4 uColor;\n" +
                            "uniform float uEps;\n" +
                            "uniform sampler2D uDepthMap; \n" +
                            "varying vec4 vInv;\n" +
                            "float unpack(vec4 c){\n" +
                            "    const vec4 BitShifts = vec4( 1.0, 1.0 / 256.0, 1.0/ 256.0/ 256.0, 0.0 );\n" +
                            "    return -1.0+2.0*dot(c, BitShifts);\n" +
                            "}\n" +
                            "void main(){\n" +
                            "   float depth = uEps + unpack(texture2D(uDepthMap, vInv.xy));\n" +
                            "   if (depth < vInv.z){\n" +
                            "       gl_FragColor = uColor*(0.2+vInv.w);\n" +
                            "   } else {\n"+
                            "       gl_FragColor = uColor*(0.6+vInv.w);\n" +
                            "   }" +
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
                    s[pos++] = (short) (num + grid.width+1);
                    s[pos++] = (short) (num + grid.width);
                } else {
                    s[pos++] = (short) num;
                    s[pos++] = (short) (num + grid.width + 1);
                    s[pos++] = (short) (num + grid.width);

                    s[pos++] = (short) (num);
                    s[pos++] = (short) (num + 1);
                    s[pos++] = (short) (num + grid.width+1);
                }
            }
        }
        sb = GLHelper.make(s);
        params.meshVertices = fb;
        params.meshIndices = sb;
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
        } else{
            shadowShader.useProgram();

            glUniformMatrix4fv(shadowShader.get("uMatrix"), 1, false,
                    params.mapView.projection().getArray(), 0);

            glUniformMatrix4fv(shadowShader.get("uMatrixShadow"), 1, false,
                    params.lightningView.projection().getArray(), 0);

            glUniform4f(shadowShader.get("uColor"), 0, 1f, 0, 1f);

            glUniform1f(shadowShader.get("uEps"), 0.005f);

            params.depthTexture.use(0);
            glUniform1i(shadowShader.get("uDepthMap"), 0);

            GLHelper.checkError();

            shadowShader.enableAllVertexAttribArray();
            glVertexAttribPointer(shadowShader.get("aPosition"), 3, GL_FLOAT, false,
                    0, fb);

            glDrawElements(GL_TRIANGLES, sb.capacity(), GL_UNSIGNED_SHORT, sb);

            shadowShader.disableAllVertexAttribArray();
        }
    }
}