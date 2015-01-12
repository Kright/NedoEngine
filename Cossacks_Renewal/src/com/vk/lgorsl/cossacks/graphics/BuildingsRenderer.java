package com.vk.lgorsl.cossacks.graphics;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.*;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.ModelData;
import com.vk.lgorsl.NedoEngine.utils.ObjFile;
import com.vk.lgorsl.NedoEngine.utils.ObjLoader;
import com.vk.lgorsl.cossacks.R;
import com.vk.lgorsl.cossacks.world.interfaces.iBuilding;
import com.vk.lgorsl.cossacks.world.interfaces.iCountry;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * System for buildings rendering
 * <p>
 * Created by lgor on 02.01.2015.
 */
public class BuildingsRenderer implements GameRenderSystem {

    private final Matrix4_4f tempM = new Matrix4_4f();
    private final Matrix4_4f tempM2 = new Matrix4_4f();
    private final Rectangle2i tempRect = new Rectangle2i();
    private final float[] tempRotation = new float[9];

    {
        tempRotation[0] = tempRotation[4] = tempRotation[8] = 1f;
    }

    private CleverShader shader;

    public ModelData building;
    public Texture2D buildingsTexture;

    FloatBuffer fb;
    ShortBuffer sb;

    @Override
    public boolean load(RendererParams params) {
        ObjLoader loader = new ObjLoader();
        float scale = params.world.metrics.meterSize();
        loader.setVerticesTransform(new Matrix4_4f().makeRotation(90, 1, 0, 0).scale(scale, scale, scale));

        //ObjFile file = loader.load(GLHelper.loadRawFileAsOneString(params.resources, R.raw.house1, "\n"));
        ObjFile file = loader.load(GLHelper.loadRawFileAsOneString(params.resources, R.raw.house2, "\n"));
        building = file.makeData();

        fb = GLHelper.make(building.data);
        sb = GLHelper.make(building.indices);

        //buildingsTexture = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.house_tex),
        //        params.settings.buildingsFilterMin, params.settings.buildingsFilterMag, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, true);
        buildingsTexture = TextureLoader.loadTexture(GLHelper.loadBitmap2(params.resources, R.drawable.house3_tex),
                        params.settings.buildingsFilterMin, params.settings.buildingsFilterMag, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, true);

        shader = params.loadShader(R.raw.shader_buildings);
        params.buildingsRenderer = this;

        return true;
    }

    @Override
    public void render(RendererParams params) {
        shader.useProgram();

        glUniform1f(shader.get("uEps"), params.settings.shadowsEps);
        Vect3f dir = params.lightView.viewDirection();
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

        params.mapView.viewBounds().getAABB(tempRect);
        tempM.makeIdentity();

        int uMatrixNormals = shader.get("uMatrixNormals");
        int uMatrix = shader.get("uMatrix");
        int uMatrixShadow = shader.get("uMatrixShadow");

        for (iCountry country : params.world.countries) {
            for (iBuilding b : country.buildings().objects(tempRect)) {
                float angle = b.getDirection() * Helper.radiansInDegree;
                float cos = FloatMath.cos(angle);
                float sin = FloatMath.sin(angle);

                tempM.makeIdentity();
                float[] arr = tempM.getArray();
                tempRotation[0] = arr[0] = cos;
                tempRotation[1] = arr[1] = sin;
                tempRotation[3] = arr[4] = -sin;
                tempRotation[4] = arr[5] = cos;

                arr[12] = b.x();
                arr[13] = b.y();
                arr[14] = params.world.map.getHeight(b);

                glUniformMatrix3fv(uMatrixNormals, 1, false, tempRotation, 0);

                tempM2.multiplication(params.mapView.projection(), tempM);
                glUniformMatrix4fv(uMatrix, 1, false, tempM2.getArray(), 0);

                tempM2.multiplication(params.lightView.anotherProjection(), tempM);
                glUniformMatrix4fv(uMatrixShadow, 1, false, tempM2.getArray(), 0);

                glDrawElements(GL_TRIANGLES, building.indices.length, GL_UNSIGNED_SHORT, sb);
            }
        }

        shader.disableAllVertexAttribArray();
    }

    @Override
    public void renderShadows(RendererParams params) {
        CleverShader shader = params.lightRenderer.shaderDepthDraw;
        shader.useProgram();
        shader.enableAllVertexAttribArray();

        fb.position(0);
        glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false, building.stride, fb);

        params.mapView.viewBounds().getAABB(tempRect);
        tempM.makeIdentity();
        int uMatrix = shader.get("uMatrix");

        for (iCountry country : params.world.countries) {
            for (iBuilding b : country.buildings().objects(tempRect)) {
                float angle = b.getDirection() * Helper.radiansInDegree;
                float cos = FloatMath.cos(angle);
                float sin = FloatMath.sin(angle);

                float[] arr = tempM.getArray();
                arr[0] = cos;
                arr[1] = sin;
                arr[4] = -sin;
                arr[5] = cos;

                arr[12] = b.x();
                arr[13] = b.y();

                arr[14] = params.world.map.getHeight(b);

                tempM2.multiplication(params.lightView.projection(), tempM);

                glUniformMatrix4fv(uMatrix, 1, false, tempM2.getArray(), 0);

                glDrawElements(GL_TRIANGLES, building.indices.length, GL_UNSIGNED_SHORT, sb);
            }
        }

        shader.disableAllVertexAttribArray();
    }
}
