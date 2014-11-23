package com.vk.lgorsl.TryGLgame;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.openGL.Shader;

/**
 * nothing serious
 *
 * Created by lgor on 24.11.2014.
 */
public class SpriteShader extends Shader{

    public final int uCamera, uTexture;
    public final int aScreenPos, aTexturePos;

    public SpriteShader(Resources resources, int resId) {
        super(resources, resId);
        uCamera = getUniformLocation("uCamera");
        uTexture = getUniformLocation("uTexture");
        aScreenPos = getAttributeLocation("aScreenPos");
        aTexturePos = getAttributeLocation("aTexturePos");
    }
}
