package com.vk.lgorsl.NedoEngine.utils;

import android.annotation.TargetApi;
import android.os.Build;
import com.vk.lgorsl.NedoEngine.math.Vect2f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;

/**
 * loads Obj file
 *
 * Created by lgor on 02.01.2015.
 */
public class ObjLoader{

    public ObjLoader(){
        //nothing
    }

    public ObjFile load(String code) {
        return ObjLoader.simpleLoad(code);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static int safeReadInt(String[] s, int number){
        if (number >= s.length || s[number].isEmpty()) return 0;
        return Integer.parseInt(s[number]);
    }

    private static ObjFile.VertexData parseVd(String s){
        String[] ss = s.split("/");
        return new ObjFile.VertexData(safeReadInt(ss, 0), safeReadInt(ss, 1), safeReadInt(ss, 2));
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static ObjFile.Face parseFace(String[] s) {
        ObjFile.Face face = new ObjFile.Face();
        int count=0;
        while (count+1 < s.length && !s[count+1].isEmpty() &&s[count+1].charAt(0)!='#') ++count;
        face.data = new ObjFile.VertexData[count];
        for(int i=0; i<count; i++){
            face.data[i] = parseVd(s[i+1]);
        }
        return face;
    }

    protected static Vect3f parse3f(String[] s) {
        return new Vect3f().set(parseF(s[1]), parseF(s[2]), parseF(s[3]));
    }

    protected static Vect2f parse2f(String[] s) {
        return new Vect2f().set(parseF(s[1]), parseF(s[2]));
    }

    protected static float parseF(String s) {
        return Float.parseFloat(s);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static ObjFile simpleLoad(String code) {
        ObjFile file = new ObjFile();
        for (String line : code.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.charAt(0) == '#') continue;
            String[] s = line.split("[ ]+");
            try {
                switch (s[0]) {
                    case "v":
                        file.vertices.add(parse3f(s));
                        break;
                    case "vn":
                        file.normals.add(parse3f(s));
                        break;
                    case "vt":
                        Vect2f vt= parse2f(s);
                        vt.y = 1 - vt.y;
                        file.textureCoords.add(vt);
                        break;
                    case "f":
                        file.faces.add(parseFace(s));
                        break;
                    case "o":
                        file.name = s[1];
                        break;
                    default:
                        file.notParsed.add(line);
                        break;
                }
            } catch (NumberFormatException ex) {
                throw new NedoException(ex);
                //throw new NedoException("obj file : wrong line \"" + line + "\"");
            } catch (ArrayIndexOutOfBoundsException ex){
                throw new NedoException("obj file : wrong line \"" + line + "\"");
            }
        }
        return file;
    }
}
