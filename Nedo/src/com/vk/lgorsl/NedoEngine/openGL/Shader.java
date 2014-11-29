package com.vk.lgorsl.NedoEngine.openGL;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Build;
import com.vk.lgorsl.NedoEngine.utils.NedoException;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;

import java.util.regex.Pattern;

import static android.opengl.GLES20.*;

/**
 * на самом деле, скомпилированная шейдерная программа
 *
 * Created by lgor on 18.11.2014.
 */
public class Shader {

    public final int id, vertexId, pixelId;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public Shader(int vertexId, int pixelId){
        id = glCreateProgram();
        if (id==0) throw new NedoException("can't create empty shader");
        this.vertexId = vertexId;
        this.pixelId = pixelId;
        glAttachShader(id, vertexId);
        glAttachShader(id, pixelId);

        glLinkProgram(id);
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(id, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            throw new NedoException("shader not compiled, info :\n"+GLES20.glGetProgramInfoLog(id));
        }
    }

    public Shader(Resources resources, int resId){
        this(GLHelper.loadRawFileAsOneString(resources, resId, "\n").
                split(Pattern.quote("[FRAGMENT]")));
    }

    public Shader(String vertexShaderCode, String pixelShaderCode) {
        this(compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode),
                compileShader(GLES20.GL_FRAGMENT_SHADER, pixelShaderCode));
    }

    public Shader(String[] vertexAndPixelCode){
        this(vertexAndPixelCode[0], vertexAndPixelCode[1]);
    }

    public void useProgram(){
        glUseProgram(id);
    }

    /*
    Имя не может быть структурой или частью вектора или матрицы.
    Структуры нужно задавать по членам, например Light.Force
    лучше вызывать один раз после создания - номер не изменится
    */
    public int getAttributeLocation(String name){
        int result = glGetAttribLocation(id, name);
        if (result == -1) throw new NedoException(
                "name : \"" + name + "\" is not an active attribute");
        return result;
    }

    public int getUniformLocation(String name) {
        int result = glGetUniformLocation(id, name);
        if (result == -1) throw new NedoException(
                "name : \"" + name + "\" is not an active uniform");
        return result;
    }

    public boolean validateProgram() {
        return validateProgram(id);
    }

    public void delete(){
        glDeleteShader(vertexId);
        glDeleteShader(pixelId);
        //it will be flagged for deletion, but it will not be
        //deleted until it is no longer attached to any program object
        glDeleteProgram(id);
    }

    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */
    public static boolean validateProgram(int id){
        glValidateProgram(id);
        int[] validateStatus = new int[1];
        glGetProgramiv(id, GL_VALIDATE_STATUS, validateStatus, 0);
        if (validateStatus[0] == 0){
            NedoLog.logError("shader validate error" +
                    glGetProgramInfoLog(id));
        }
        return validateStatus[0]!=0;
    }

    /**
     *
     * @param shaderType GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @param code vertex or fragment shader code
     * @return id of created shader or 0 if errors
     */
    public static int compileShader(int shaderType, String code){
        int id = glCreateShader(shaderType);
        if (id == 0){
            NedoLog.logError("can't create empty shader :(");
            return 0;
        }
        glShaderSource(id, code);
        glCompileShader(id);

        int[] compiled = new int[1];
        glGetShaderiv(id, GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0]==0){
            NedoLog.logError("shader compilation error, code:\n"
                    + code + "\n" + glGetShaderInfoLog(id));
            glDeleteShader(id);
            return 0;
        }
        return id;
    }

    /**
     * после загрузки всех шейдеров неплохо бы выгрузить компилятор
     */
    public static void releaseCompiler(){
        glReleaseShaderCompiler();
    }
}
