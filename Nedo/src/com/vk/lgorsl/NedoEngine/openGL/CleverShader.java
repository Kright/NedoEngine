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
 * also it can enable and disable all vertexAttribArrays in one single method
 * <p/>
 * на самом деле оказалось, что выигрыш не такой уж и большой :(
 * померял производительность на Sony Xperia ZL для какого-то простого шейдера
 * В секунду можно сделать 3*10^6 вызовов get(name)
 * или 4*10^5 вызовов glGetAttributeLocation(name)
 * т.е., если вызывать 100 раз за кадр, то падение производительности будет всего лишь порядка 1%
 * но раз уж класс уже написан и работает всё-таки раз в 10 быстрее - наверно, лучше использовать его
 * <p/>
 * Created by lgor on 29.11.2014.
 */
public class CleverShader extends Shader {

    private final HashMap<String, Integer> locations = new HashMap<>(16);
    private final List<Integer> attributes = new ArrayList<>(8);

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

    /**
     * It parses code, gets names of uniforms and attributes and saves them
     * @param code code of this shader
     */
    public void addLocationsFromCode(String code) {
        GLHelper.checkError();
        String[] ss = code.split("[; \n]+");
        for (int i = 0; i < ss.length; i++) {
            int pos = ss[i].indexOf("//", 0);
            if (pos==0) continue;
            if (pos > 0) {
                ss[i] = ss[i].substring(0, pos);
            }
            if (ss[i].equals("uniform")) {
                i += 2;
                locations.put(ss[i], super.getUniformLocation(ss[i]));
            } else if (ss[i].equals("attribute")) {
                i += 2;
                int loc = super.getAttributeLocation(ss[i]);
                locations.put(ss[i], loc);
                attributes.add(loc);
            }
        }
        GLHelper.checkError();
    }

    @Override
    public int getAttributeLocation(String name) {
        if (locations.containsKey(name)) {
            return locations.get(name);
        }
        int id = super.getAttributeLocation(name);
        locations.put(name, id);
        attributes.add(id);
        return id;
    }

    public void addAttributeLocations(String... names) {
        for (String s : names) {
            getAttributeLocation(s);
        }
    }

    @Override
    public int getUniformLocation(String name) {
        if (locations.containsKey(name)) {
            return locations.get(name);
        }
        int id = super.getUniformLocation(name);
        locations.put(name, id);
        return id;
    }

    public void addUniformLocations(String... names) {
        for (String s : names) {
            getUniformLocation(s);
        }
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
     *
     * @param name of attribute or uniform
     * @return it's location
     */
    public int get(String name) {
        Integer n = locations.get(name);
        if (n == null) throw new NedoException("unknown name : \"" + name + "\"");
        return n;
    }
}
