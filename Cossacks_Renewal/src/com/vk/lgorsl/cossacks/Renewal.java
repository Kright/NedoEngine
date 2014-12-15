package com.vk.lgorsl.cossacks;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.vk.lgorsl.NedoEngine.openGL.ConfigChooser;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;
import com.vk.lgorsl.cossacks.graphics.GameRenderer;

public class Renewal extends Activity {

    GLSurfaceView view;
    GameRenderer renderer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        NedoLog.log_string = "com.vk.lgorsl.cossacks";
        NedoLog.log("onCreate");

        if (view == null) {
            view = new GLSurfaceView(this);
            view.setEGLContextClientVersion(2);

            view.setEGLConfigChooser(new ConfigChooser(ConfigChooser.RGBA8888DEPTH16).setConfigPrinting(true));

            renderer = new GameRenderer(this);
            view.setRenderer(renderer);

            view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            view.setPreserveEGLContextOnPause(true);
        }
        setContentView(view);
    }


    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
        NedoLog.log("onResume");
    }

    @Override
    protected void onPause() {
        NedoLog.log("onPause");
        view.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        NedoLog.log("onSaveInstanceState");
    }
}
