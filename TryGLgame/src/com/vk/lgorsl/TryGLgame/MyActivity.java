package com.vk.lgorsl.TryGLgame;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.vk.lgorsl.NedoEngine.openGL.ConfigChooser;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String s = ConfigChooser.printAllConfigs();
        Log.d("com.vk.lgorsl.TryGLgame", s);

        setContentView(R.layout.main);
    }
}
