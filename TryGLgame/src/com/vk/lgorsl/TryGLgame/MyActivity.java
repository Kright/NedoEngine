package com.vk.lgorsl.TryGLgame;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d("mylog", "I'm working");
    }
}
