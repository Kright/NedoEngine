package com.vk.lgorsl.NedoEngine.openGL;

import android.content.res.Resources;
import com.vk.lgorsl.NedoEngine.utils.NedoException;

import java.util.HashMap;

/**
 * this shader saves attributes and uniforms locations for faster access;
 *
 * Created by lgor on 29.11.2014.
 */
public class CleverShader extends Shader{

    private final HashMap<String, Integer> locations = new HashMap<String, Integer>(16);

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

    public void addLocationsFromCode(String code){
        String[] ss = code.split("[; \n]+");
        for(int i=0; i<ss.length; i++){
            if (ss[i].equals("uniform") || ss[i].equals("attribute")){
                i+=2;
                addLocation(ss[i]);
            }
        }
    }

    public int addLocation(String s){
        if (locations.containsKey(s)){
            return locations.get(s);
        }
        int id;
        if ((id = getAttributeLocation(s)) == -1){
            id = getUniformLocation(s);
        }
        if (id==-1){
            throw new NedoException("wrong attribute or uniform name: " + s);
        }
        this.locations.put(s, id);
        return id;
    }

    public void addLocations(Iterable<String> ss){
        for(String s: ss){
            addLocation(s);
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

    /**
     *
     * @param name of uniform or tag
     * @return id
     */
    public int getLocation(String name){
        Integer id = locations.get(name);
        if (id != null){
            return id;
        }
        return addLocation(name);
    }
}
