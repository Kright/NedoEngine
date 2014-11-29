package com.vk.lgorsl.NedoEngine.openGL;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;

/**
 * something which can be rendered
 *
 * Created by lgor on 23.11.2014.
 */
public interface Renderable {

    void render(Matrix4_4f matrix4_4f);
}
