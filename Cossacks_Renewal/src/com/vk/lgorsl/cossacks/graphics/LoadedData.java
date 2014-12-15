package com.vk.lgorsl.cossacks.graphics;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.openGL.FontTexture;

/**
 * common data, which will be used by different renderers
 *
 * Created by lgor on 15.12.2014.
 */
public class LoadedData {

    public final Resources resources;

    public FontTexture texture;

    public LoadedData(Resources resources) {
        this.resources = resources;
    }
}
