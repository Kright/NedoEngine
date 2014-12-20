package com.vk.lgorsl.cossacks.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.realizations.MapView;

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

    private final FPSCounter clock;

    private final List<GameRenderable> renderers = new ArrayList<>();

    private final WorldInstance world;
    private LoadedData loadedData;
    private RendererParams rendererParams;

    public GameRenderer(Context context){
        loadedData = new LoadedData(context.getResources());
        world = new WorldInstance();
        world.load();

        rendererParams = new RendererParams(world, new MapView(new Point2i().set(32, 32),world.metrics));

        clock = new FPSCounter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        NedoLog.log("surface Created");

        GLHelper.checkError();

        renderers.add(new LandMeshRenderer());
        for(GameRenderable rend : renderers){
            rend.load(loadedData);
        }
        Shader.releaseCompiler();
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

        float t = (clock.framesCount()%628)/100f;
        rendererParams.mapView.setDirectionOfView(FloatMath.sin(t), FloatMath.cos(t));
        rendererParams.mapView.setInclination(60 + 20 * FloatMath.sin(t));
        iRectangle2i mapSize = rendererParams.world.metrics.mapSize();
        Point2i position = new Point2i().set(mapSize.xCenter(), mapSize.yCenter());
        rendererParams.mapView.setCenterPosition(position);
        rendererParams.mapView.setScale((1.3f + FloatMath.sin(t)) / 1.3f / 2);
        rendererParams.mapView.setVerticalScale(1f);

        for(GameRenderable rend : renderers){
            rend.render(rendererParams);
            GLHelper.checkError();
        }
    }
}
