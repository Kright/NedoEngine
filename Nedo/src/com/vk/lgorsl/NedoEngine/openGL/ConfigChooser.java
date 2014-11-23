package com.vk.lgorsl.NedoEngine.openGL;

import android.annotation.TargetApi;
import android.opengl.GLSurfaceView;
import android.os.Build;
import javax.microedition.khronos.egl.*;

/**
 * класс для того случая, когда хочется добавить сглаживания или ешё чего-нибудь
 * стандартным SurfaceView.view.setEGLConfigChooser(r,g,b,a, depth, stencil) так не получится
 *
 * помогла эта статья:
 * http://4pda.ru/forum/index.php?showtopic=418429&st=0#entry18734436
 * <p/>
 * eglChooseConfig specification:
 * https://www.khronos.org/registry/egl/sdk/docs/man/html/eglChooseConfig.xhtml
 * <p/>
 * Created by lgor on 18.11.2014.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class ConfigChooser implements GLSurfaceView.EGLConfigChooser {

    /*
    EGL_RED_SIZE - бит на красный канал
    EGL_GREEN_SIZE -бит на зеленый канал
    EGL_BLUE_SIZE - бит на синий канал
    EGL_ALPHA_SIZE - бит на альфа канал (default: 0)
    EGL_DEPTH_SIZE - глубина Z буфера
    EGL_RENDERABLE_TYPE - API поддерживаемые в данной конфигурации. Значение - битовая маска, так как одной и той же конфигурации может соответствовать несколько API. OpenGL ES 2.0 соответствует значение 4
    EGL_SAMPLE_BUFFERS - Поддержка антиалайзинга (0 or 1)
    EGL_SAMPLES - количество семплов на пиксель
    EGL_NONE - завершение списка

    EGL_ALPHA_MASK_SIZE
    EGL_BIND_TO_TEXTURE_RGB
    EGL_BIND_TO_TEXTURE_RGBA
    EGL_BUFFER_SIZE
    EGL_COLOR_BUFFER_TYPE
    EGL_CONFIG_CAVEAT
    EGL_CONFIG_ID
    EGL_CONFORMANT
    EGL_LEVEL
    EGL_LUMINANCE_SIZE
    EGL_MAX_PBUFFER_WIDTH
    EGL_MAX_PBUFFER_HEIGHT
    EGL_MAX_PBUFFER_PIXELS
    EGL_MAX_SWAP_INTERVAL
    EGL_MIN_SWAP_INTERVAL
    EGL_NATIVE_RENDERABLE
    EGL_NATIVE_VISUAL_ID
    EGL_NATIVE_VISUAL_TYPE
    EGL_STENCIL_SIZE
    EGL_SURFACE_TYPE
    EGL_TRANSPARENT_TYPE
    EGL_TRANSPARENT_RED_VALUE
    EGL_TRANSPARENT_GREEN_VALUE
    EGL_TRANSPARENT_BLUE_VALUE
    */

    public static final int[] RGB888MSAA2 = {
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_RENDERABLE_TYPE, 4,   //поддержка GLES20
            EGL10.EGL_SAMPLE_BUFFERS, 1,    //true
            EGL10.EGL_SAMPLES, 2,
            EGL10.EGL_NONE
    };

    private final int[] config1, config2;
    private boolean printAvailableConfigs = false;

    public ConfigChooser(int[] config) {
        config1 = config;
        config2 = null;
    }

    public ConfigChooser(int[] config, int[] reserveConfig) {
        config1 = config;
        config2 = reserveConfig;
    }

    public ConfigChooser setConfigPrinting(boolean printing) {
        printAvailableConfigs = printing;
        return this;
    }

    @Override
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        int[] value = new int[1];

        int[] configSpec = config1;

        if (!egl.eglChooseConfig(display, config1, null, 0, value)) {
            throw new IllegalArgumentException("EGLConfig choose failed");
        }

        int numConfigs = value[0];
        if (numConfigs <= 0) {
            if (config2 == null) {
                throw new IllegalArgumentException("We haven't reserve config");
            }
            configSpec = config2;
            if (!egl.eglChooseConfig(display, config2, null, 0, value)) {
                throw new IllegalArgumentException("Reserve eglChooseConfig choose failed");
            }
            numConfigs = value[0];
            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs :(");
            }
        }
        EGLConfig[] configs = new EGLConfig[numConfigs];

        egl.eglChooseConfig(display, configSpec, configs, numConfigs, value); // получаем массив конфигураций

        if (printAvailableConfigs) {
            GLHelper.log("EGL available configs, config[0] was chosen\n" + printConfigs(configs, display, egl));
        }

        return configs[0]; // возвращаем конфиг
    }

    private final static int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
    private final static int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

    public static String printConfigs(EGLConfig[] conf, EGLDisplay EglDisplay, EGL10 Egl) {
        String text = "";
        for (int i = 0; i < conf.length; i++) {
            int[] value = new int[1];
            if (conf[i] != null) {
                text += "==== Config №" + i + " ====\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_RED_SIZE, value);
                text += "EGL_(RED, GREEN, BLUE)_SIZE = (" + value[0] + ",";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_GREEN_SIZE, value);
                text += value[0] + ",";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_BLUE_SIZE, value);
                text += + value[0] + ")\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_ALPHA_SIZE, value);
                text += "EGL_ALPHA_SIZE = " + value[0] + "\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_DEPTH_SIZE, value);
                text += "EGL_DEPTH_SIZE = " + value[0] + "\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_STENCIL_SIZE, value);
                text += "EGL_STENCIL_SIZE = " + value[0] + "\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_SAMPLE_BUFFERS, value);
                text += "EGL_SAMPLE_BUFFERS = " + value[0] + "\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL10.EGL_SAMPLES, value);
                text += "EGL_SAMPLES = " + value[0] + "\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL_COVERAGE_BUFFERS_NV, value);
                text += "EGL_COVERAGE_BUFFERS_NV = " + value[0] + "\n";
                Egl.eglGetConfigAttrib(EglDisplay, conf[i], EGL_COVERAGE_SAMPLES_NV, value);
                text += "EGL_COVERAGE_SAMPLES_NV = " + value[0] + "\n\n";
            } else {
                break;
            }
        }
        return text;
    }

    public static String printAllConfigs() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay eglDisplay = egl.eglGetDisplay(EGL11.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        egl.eglInitialize(eglDisplay, version);
        int[] configSpec = {
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
        };
        int[] value = new int[1];
        egl.eglChooseConfig(eglDisplay, configSpec, null, 0, value);

        int numConfigs = value[0];
        EGLConfig[] configs = new EGLConfig[numConfigs];
        egl.eglChooseConfig(eglDisplay, configSpec, configs, numConfigs, value);

        return "version = " + version[0] + "." + version[1] +
                ConfigChooser.printConfigs(configs, eglDisplay, egl);
    }
}
