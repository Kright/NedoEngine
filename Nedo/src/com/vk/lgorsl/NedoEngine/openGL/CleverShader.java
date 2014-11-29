package com.vk.lgorsl.NedoEngine.openGL;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Build;
import com.vk.lgorsl.NedoEngine.utils.NedoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * this shader saves attributes and uniforms locations for faster access;
 * also it can enable and disable all vertexAttribArrays
 * <p/>
 * Created by lgor on 29.11.2014.
 */
public class CleverShader extends Shader {

    private final HashMap<String, Integer> locations = new HashMap<String, Integer>(16);
    private final List<Integer> attributes = new ArrayList<Integer>(8);

    public CleverShader(int vertexId, int pixelId) {
        super(vertexId, pixelId);
    }

    public CleverShader(Resources resources, int resId) {
        super(resources, resId);
    }

    public CleverShader(String vertexShaderCode, String pixelShaderCode) {
        super(vertexShaderCode, pixelShaderCode);
        addLocationsFromCode(vertexShaderCode);
        addLocationsFromCode(pixelShaderCode);
    }

    public CleverShader(String[] vertexAndPixelCode) {
        super(vertexAndPixelCode);
        addLocationsFromCode(vertexAndPixelCode[0]);
        addLocationsFromCode(vertexAndPixelCode[1]);
    }

    public void addLocationsFromCode(String code) {
        String[] ss = code.split("[; \n]+");
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].equals("uniform") || ss[i].equals("attribute")) {
                i += 2;
                addName(ss[i]);
            }
        }
    }

    private int find(String s) {
        Integer i = locations.get(s);
        return i != null ? i : -1;
    }

    private int addName(String s) {
        int id = find(s);
        if (id != -1) {
            return id;
        }
        if ((id = getAttributeLocation(s)) != 1) {
            locations.put(s, id);
            attributes.add(id);
            return id;
        }
        if ((id = getUniformLocation(s)) != 1) {
            locations.put(s, id);
            return id;
        }
        throw new NedoException("wrong attribute or uniform name: " + s);
    }

    public void addLocation(String... names) {
        for (String s : names) {
            addName(s);
        }
    }

    @Override
    public int getAttributeLocation(String name) {
        return getLocation(name);
    }

    @Override
    public int getUniformLocation(String name) {
        return getLocation(name);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public void enableAllVertexAttribArray() {
        for (Integer n : attributes) {
            GLES20.glEnableVertexAttribArray(n);
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public void disableAllVertexAttribArray() {
        for (Integer n : attributes) {
            GLES20.glDisableVertexAttribArray(n);
        }
    }

    /**
     * @param name of uniform or tag
     * @return id
     */
    public int getLocation(String name) {
        return addName(name);
    }
}
