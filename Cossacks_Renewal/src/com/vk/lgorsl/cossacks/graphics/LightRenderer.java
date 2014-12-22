package com.vk.lgorsl.cossacks.graphics;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.NedoEngine.utils.NedoException;

/**
 * it will produce depth map for directed shadows
 * <p>
 * this post can help:
 * http://4pda.ru/forum/index.php?s=&showtopic=418429&view=findpost&p=27856233
 * <p>
 * Created by lgor on 20.12.2014.
 */
public class LightRenderer implements GameRenderable {

    public final static int depthTextureSize = 512;

    private final int[]
            frameBuffer = new int[1],
            renderBuffer = new int[1];
    private Texture2D texture2D;

    private CleverShader shader;

    @Override
    public void load(LoadedData loadedData) {
        glGenFramebuffers(1, frameBuffer, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);

        int[] texId = new int[1];
        texture2D = new Texture2D(texId);
        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, depthTextureSize, depthTextureSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        TextureLoader.setParameters(GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texId[0], 0);

        //z - buffer as renderbuffer
        glGenRenderbuffers(1, renderBuffer, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, depthTextureSize, depthTextureSize);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer[0]);

        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw new NedoException("frameBuffer didn't created! status = " + status);
        }

        shader = new CleverShader(
                "precision mediump float;" +
                "uniform mat4 uMatrix;\n" +
                "attribute vec3 aPosition;\n" +
                "varying float vZ;\n" +
                "void main(){\n" +
                        "gl_Position = uMatrix * vec4(aPosition.xyz, 1.0);\n" +
                        "vZ = gl_Position.z;\n" +
                "}",
                "varying float vZ;\n" +
                        "vec4 pack(float d){\n" +
                        "   float c = 0.5*d+0.5;" +
                        "   return vec4(floor(c*256.0)/256.0, 0.0, 0.0, 0.0);" +
                        "}\n" +
                        "vec4 Pack(float Value)\n" +
                        "{\n" +
                        "    Value = Value*0.5+0.5;\n" +
                        "    const vec4 BitSh  = vec4( 256.0 * 256.0 * 256.0, 256.0 * 256.0, 256.0, 1.0);\n" +
                        "    const vec4 BitMsk = vec4( 0.0, 1.0 / 256.0, 1.0 / 256.0, 1.0 / 256.0 );\n" +
                        "    vec4 Comp = fract( Value * BitSh );\n" +
                        "    Comp -= Comp.xxyz * BitMsk;\n" +
                        "    return Comp;\n" +
                        "}\n" +
                        "void main(){\n" +
                        "gl_FragColor = Pack(vZ);\n" +
                "}\n");
    }

    @Override
    public final void render(RendererParams params) {
        if (!params.lightningRendering) {
            return;
        }
        if (params.depthTexture == null) {
            params.depthTexture = texture2D;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        glViewport(0, 0, depthTextureSize, depthTextureSize);

        //rendering here
        renderWorld(params);

        glFlush();  //waiting drawing
        glBindFramebuffer(GL_FRAMEBUFFER, 0);   //set default settings
        glViewport(0, 0, params.defaultViewPortSize.x, params.defaultViewPortSize.y);
    }

    public void renderWorld(RendererParams params){
        if (params.meshIndices==null || params.meshVertices==null){
            return;
        }

        glClearColor(0, 0, 0, 0);
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.useProgram();
        shader.get("uMatrix");
        glUniformMatrix4fv(shader.get("uMatrix"), 1, false,
                params.lightningView.projection().getArray(), 0);

        shader.enableAllVertexAttribArray();
        glVertexAttribPointer(shader.get("aPosition"), 3, GL_FLOAT, false,
                0, params.meshVertices);

        glDrawElements(GL_TRIANGLES, params.meshIndices.capacity(), GL_UNSIGNED_SHORT, params.meshIndices);

        shader.disableAllVertexAttribArray();
    }
}