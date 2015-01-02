package com.vk.lgorsl.cossacks.graphics;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.ModelData;
import com.vk.lgorsl.NedoEngine.utils.ObjFile;
import com.vk.lgorsl.NedoEngine.utils.ObjLoader;
import com.vk.lgorsl.cossacks.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import static android.opengl.GLES20.*;

/**
 * System for buildings rendering
 * <p>
 * Created by lgor on 02.01.2015.
 */
public class BuildingsRenderer implements GameRenderable {

    private final int maxCount = 16;
    private final Matrix4_4f positionMatrix[] = new Matrix4_4f[maxCount];
    private final Matrix4_4f temp = new Matrix4_4f();

    private final Matrix4_4f matrix = new Matrix4_4f();

    {
        matrix.setColumn(0, 0.5f, 0, 0, 0);
        matrix.setColumn(1, 0, 0.5f, 0, 0);
        matrix.setColumn(2, 0, 0, 0.5f, 0);
        matrix.setColumn(3, 0.5f, 0.5f, 0.5f, 1f);
    }


    private CleverShader shader;

    public ModelData building;
    public Texture2D buildingsTexture;

    FloatBuffer fb;
    ShortBuffer sb;

    @Override
    public boolean load(RendererParams params) {
        ObjLoader loader = new ObjLoader();

        ObjFile file = loader.load(GLHelper.loadRawFileAsOneString(params.resources, R.raw.house1, "\n"));
        Matrix4_4f rot = new Matrix4_4f().makeRotation(90, 1, 0, 0);
        for (Vect3f v : file.vertices) {
            rot.mul(v, v);
        }
        for (Vect3f v : file.normals) {
            rot.mul(v, v);
        }
        building = file.makeData();

        fb = GLHelper.make(building.data);
        sb = GLHelper.make(building.indices);

        buildingsTexture = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.house_tex),
                GL_LINEAR_MIPMAP_LINEAR, GL_NEAREST, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, true);

        Random rnd = new Random();
        for (int i = 0; i < maxCount; i++) {
            int x = rnd.nextInt(32 * 64) + 32 * 96;
            int y = rnd.nextInt(32 * 64) + 32 * 96;
            positionMatrix[i] = new Matrix4_4f();
            positionMatrix[i].makeScale(params.world.metrics.meterSize());
            positionMatrix[i].translate(x, y, params.world.heightGrid.getHeight(x, y));
        }

        shader = new CleverShader(params.resources, R.raw.shader_buildings);

        return true;
    }

    @Override
    public void render(RendererParams params) {
        shader.useProgram();

        glUniform1f(shader.get("uEps"), params.settings.shadowsEps);
        Vect3f dir = new Vect3f();
        params.lightningView.getViewDirection(dir);
        glUniform3f(shader.get("uLightDirection"), dir.x, dir.y, dir.z);

        float amb = 0.5f;
        glUniform3f(shader.get("uAmbient"), 0.5f * amb, amb, 1.5f * amb);
        float dif = 0.6f;
        glUniform3f(shader.get("uDiffuse"), 2.0f * dif, 1.4f * dif, 0.6f * dif);

        params.depthTexture.use(1);
        glUniform1i(shader.get("uDepthMap"), 1);
        buildingsTexture.use(0);
        glUniform1i(shader.get("uTexture"), 0);

        shader.enableAllVertexAttribArray();

        fb.position(0);
        glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false, building.stride, fb);
        fb.position(3);
        glVertexAttribPointer(shader.get("aTexCoord"), 2, GL_FLOAT, false, building.stride, fb);
        fb.position(5);
        glVertexAttribPointer(shader.get("aNormal"), 3, GL_FLOAT, false, building.stride, fb);

        for (int i = 0; i < maxCount; i++) {
            temp.multiplication(params.mapView.projection(), positionMatrix[i]);
            glUniformMatrix4fv(shader.get("uMatrix"), 1, false, temp.getArray(), 0);

            temp.multiplication(matrix, params.lightningView.projection());
            temp.multiplication(temp, positionMatrix[i]);
            glUniformMatrix4fv(shader.get("uMatrixShadow"), 1, false, temp.getArray(), 0);

            glDrawElements(GL_TRIANGLES, building.indices.length, GL_UNSIGNED_SHORT, sb);
        }

        shader.disableAllVertexAttribArray();
        GLHelper.checkError();
    }

    public void shadowsRender(RendererParams params){
        shader.useProgram();
        shader.enableAllVertexAttribArray();

        fb.position(0);
        glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false, building.stride, fb);

        for (int i = 0; i < maxCount; i++) {

        }

        shader.disableAllVertexAttribArray();
    }

    @Override
    public void renderShadows(RendererParams params) {

    }
}
