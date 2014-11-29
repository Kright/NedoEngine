package com.vk.lgorsl.TryGLgame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import com.vk.lgorsl.NedoEngine.openGL.ConfigChooser;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;

import javax.microedition.khronos.egl.EGL10;

public class MyActivity extends Activity {

    public GLSurfaceView view;
    public TempRenderer2 renderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        NedoLog.log_string = "com.vk.lgorsl.TryGLgame";

        view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);

        ConfigChooser chooser = new ConfigChooser(new int[]{
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 1,
                EGL10.EGL_DEPTH_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_SAMPLE_BUFFERS, 0,    //true
                EGL10.EGL_NONE
        }).setConfigPrinting(true);

        view.setEGLConfigChooser(chooser);

        renderer = new TempRenderer2(this);
        view.setRenderer(renderer);
        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        view.setPreserveEGLContextOnPause(true);

        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    protected void onPause() {
        view.onPause();
        super.onPause();
    }
}
