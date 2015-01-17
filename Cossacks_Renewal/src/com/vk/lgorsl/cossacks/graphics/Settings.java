package com.vk.lgorsl.cossacks.graphics;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_NEAREST_MIPMAP_NEAREST;

/**
 * game rendering settings
 *
 * Created by lgor on 16.01.2015.
 */
public class Settings {

    //shadows settings
    boolean shadowsEnabled = true;

    float shadowsEps = 0.001f;
    int depthTextureSize = 2048;

    //landscape rendering settings
    int cellSize = 32;
    int chunkSize = cellSize*64;

        /*
        float shadowsEps = 0.002f;
        int depthTextureSize = 1024;
        */

    public int landFilterMin = GL_NEAREST_MIPMAP_NEAREST, landFilterMag = GL_NEAREST;
    public int treesFilterMin = GL_NEAREST_MIPMAP_NEAREST, treesFilterMag = GL_LINEAR;
    public int buildingsFilterMin = GL_NEAREST_MIPMAP_NEAREST, buildingsFilterMag = GL_LINEAR;

    public static Settings getDefaultSettings(){
        return new Settings();
    }

    public static Settings getLowSettings(){
        Settings result = new Settings();
        result.shadowsEps = 0.01f;
        result.depthTextureSize = 1024;
        result.cellSize = 64;
        result.chunkSize = 64*32;
        return result;
    }
}
