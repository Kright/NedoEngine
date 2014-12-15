package com.vk.lgorsl.cossacks.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

/**
 * main game render.
 *
 * Created by lgor on 13.12.2014.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private final FPSCounter clock;

    private final List<Renderable<LoadedData, RendererParams>> renderers = new ArrayList<>();
    private RendererParams params;

    public GameRenderer(Context context){
        setContext(context);
        clock = new FPSCounter();
    }

    public synchronized void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        NedoLog.log("surface Created");

        GLHelper.checkError();

        LoadedData data = new LoadedData(context.getResources());

        for(Renderable<LoadedData, RendererParams> rend : renderers){
            rend.load(data);
        }
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        NedoLog.log("surface Updated, w = " + width + ", h = " + height );
        glViewport(0, 0, width, height);

        GLHelper.checkError();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        clock.update();

        if (clock.framesCount() % 200 == 0) {
            NedoLog.log("fps = " + clock.fps());
        }

        float c = clock.framesCount()%2==0? 1 : 0;
        glClearColor(0.5f, c, 1-c, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        GLHelper.checkError();

        for(Renderable<LoadedData, RendererParams> rend : renderers){
            rend.render(params);
            GLHelper.checkError();
        }
    }

    public void setParams(RendererParams params) {
        this.params = params;
    }
}
