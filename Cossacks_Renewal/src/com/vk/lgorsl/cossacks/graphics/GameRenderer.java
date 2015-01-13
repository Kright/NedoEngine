package com.vk.lgorsl.cossacks.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;
import com.vk.lgorsl.cossacks.Renewal;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.realizations.LightView;
import com.vk.lgorsl.cossacks.world.realizations.MapView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

/**
 * main game renderer.
 * <p>
 * Created by lgor on 13.12.2014.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private final List<GameRenderable> renderers = new ArrayList<>();

    private RendererParams rendererParams;

    public GameRenderer(Context context) {
        long time = System.currentTimeMillis();
        rendererParams = new RendererParams(context.getResources());

        rendererParams.world = new WorldInstance();
        rendererParams.world.load();
        rendererParams.mapView = new MapView(new Point2i().set(32, 32), rendererParams.world.metrics);

        rendererParams.lightView = new LightView(rendererParams.world.metrics.maxHeight(),
                rendererParams.world.metrics.meterSize() * 3);
        NedoLog.log("GameRenderer creating spent " + (System.currentTimeMillis() - time) + "ms");
    }

    private long creatinTime;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        NedoLog.log("surface Created");
        creatinTime = System.currentTimeMillis();

        renderers.add(new LightRenderer());
        renderers.add(new ScreenColorDepthCleaner());
        //renderers.add(new LandMeshRenderer());
        renderers.add(new LandscapeRenderer(rendererParams.settings.cellSize, rendererParams.settings.chunkSize));
        renderers.add(new TreesRender());
        renderers.add(new BuildingsRenderer());
        //renderers.add(new DepthTextureRenderer());

        for (GameRenderable rend : renderers) {
            rend.load(rendererParams);
        }

        Shader.releaseCompiler();

        Runtime.getRuntime().gc();
        printMemory();
    }

    private static void printMemory() {
        NedoLog.log("free = " + Runtime.getRuntime().freeMemory() + ", total " + Runtime.getRuntime().totalMemory() +
                ", max memory = " + Runtime.getRuntime().maxMemory());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        NedoLog.log("Surface creating spent " + (System.currentTimeMillis() - creatinTime)
                + "ms\n surface Updated, w = " + width + ", h = " + height);
        rendererParams.defaultViewportSize.set(width, height);
        glViewport(0, 0, width, height);
        rendererParams.mapView.setAspectRatio((float) width / height);

        GLHelper.checkError();
    }

    private long time;
    long first = 0;

    @Override
    public void onDrawFrame(GL10 gl) {
        FPSCounter clock = rendererParams.clock;
        clock.update();
        if (first == 0) {
            first = clock.getTime();
        }

        if (clock.framesCount() % 500 == 0) {
            long now = clock.getTime();
            NedoLog.log("fps = " + rendererParams.clock.fps());
            NedoLog.log("average :fps = " + rendererParams.clock.framesCount() * 1000 / (now - first) +
                    " , ms per frame:" + (now - first) / rendererParams.clock.framesCount());
            time = now;
        }

        rendererParams.settings.shadowsEnabled = Renewal.shadows;

        glFrontFace(GL_CCW);
        glCullFace(GL_FRONT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        GLHelper.checkError();

        float t = (rendererParams.clock.framesCount() % (628 * 2)) / 100f;
        t *= 2;
        iRectangle2i mapSize = rendererParams.world.metrics.mapSize();
        Point2i position = new Point2i().set(mapSize.xCenter(), mapSize.yCenter());

        rendererParams.mapView.setDirectionOfView(FloatMath.sin(t), FloatMath.cos(t));
        rendererParams.mapView.setInclination(30 + FloatMath.sin(t + 0.345f));
        rendererParams.mapView.setCenterPosition(position);
        rendererParams.mapView.setScale((1.0f + 0.7f * FloatMath.sin(t)) / 20);

        rendererParams.lightView.setViewDirection(FloatMath.sin(t / 2), FloatMath.cos(t / 2));
        rendererParams.lightView.setInclination(30 + 0 * FloatMath.cos(t / 2));
        rendererParams.lightView.setToCover(rendererParams.mapView.viewBounds());

        for (GameRenderable rend : renderers) {
            rend.render(rendererParams);
            GLHelper.checkError();
        }
    }
}
