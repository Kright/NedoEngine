package com.vk.lgorsl.cossacks.graphics;

import static android.opengl.GLES20.*;

import com.vk.lgorsl.NedoEngine.openGL.CleverShader;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.NedoEngine.utils.NedoException;
import com.vk.lgorsl.cossacks.R;

/**
 * it will produce depth map for directed shadows
 * <p>
 * this post can help:
 * http://4pda.ru/forum/index.php?s=&showtopic=418429&view=findpost&p=27856233
 * <p>
 * Created by lgor on 20.12.2014.
 */
public class LightRenderer implements GameRenderable {

    private final int[]
            frameBuffer = new int[1],
            renderBuffer = new int[1];
    private Texture2D texture2D;

    public CleverShader shaderDepthDraw;
    public CleverShader shaderDepthDiscardDraw;

    private int depthTextureSize;
    private boolean firstDrawCall = true;

    @Override
    public boolean load(RendererParams params) {
        depthTextureSize = params.settings.depthTextureSize;
        if (depthTextureSize<256){
            depthTextureSize = 256;
        }

        glGenFramebuffers(1, frameBuffer, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);

        int[] texId = new int[1];
        texture2D = new Texture2D(texId);
        glGenTextures(1, texId, 0);
        glBindTexture(GL_TEXTURE_2D, texId[0]);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, params.settings.depthTextureSize,
                params.settings.depthTextureSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

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

        shaderDepthDraw = new CleverShader(params.resources, R.raw.shader_light_depth);
        //shaderDepthDraw = new CleverShader(params.resources, R.raw.shader_depth_debug);
        shaderDepthDiscardDraw = new CleverShader(params.resources, R.raw.shader_depth_discard);

        params.depthTexture = texture2D;

        params.lightRenderer = this;

        return true;
    }

    @Override
    public final void render(RendererParams params) {
        if (!firstDrawCall && !params.settings.shadowsEnabled){
            return;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        glViewport(0, 0, depthTextureSize, depthTextureSize);

        //rendering here
        renderWorld(params);

        glFlush();  //waiting drawing
        glBindFramebuffer(GL_FRAMEBUFFER, 0);   //set default settings
        glViewport(0, 0, params.defaultViewportSize.x, params.defaultViewportSize.y);
    }

    public void renderWorld(RendererParams params) {
        glClearColor(1f, 1f, 1f, 1f);
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if (params.settings.shadowsEnabled) {

            params.landMeshRenderer.renderShadows(params);
            params.treesRender.renderShadows(params);
        }
    }
}
