package com.vk.lgorsl.cossacks.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;
import com.vk.lgorsl.cossacks.world.WorldInstance;
import com.vk.lgorsl.cossacks.world.realizations.LightningView;
import com.vk.lgorsl.cossacks.world.realizations.MapView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

/**
 * main game renderer.
 *
 * Created by lgor on 13.12.2014.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private final List<GameRenderable> renderers = new ArrayList<>();

    private RendererParams rendererParams;

    public GameRenderer(Context context){
        rendererParams = new RendererParams(context.getResources());

        rendererParams.world = new WorldInstance();
        rendererParams.world.load();
        rendererParams.mapView = new MapView(new Point2i().set(32,32), rendererParams.world.metrics);
        rendererParams.lightningView = new LightningView(new Point2i().set(32, 32), rendererParams.world.metrics);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        NedoLog.log("surface Created");

        GLHelper.checkError();

        renderers.add(new LightRenderer());
        renderers.add(new ScreenColorDepthCleaner());
        renderers.add(new LandMeshRenderer());
        renderers.add(new TreesRender());
        //renderers.add(new LandMeshGridRenderer());
        //renderers.add(new DepthTextureRenderer());
        //renderers.add(new BuildingsRenderer());

        for(GameRenderable rend : renderers){
            rend.load(rendererParams);
        }

        Shader.releaseCompiler();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        NedoLog.log("surface Updated, w = " + width + ", h = " + height );
        rendererParams.defaultViewportSize.set(width, height);
        glViewport(0, 0, width, height);
        rendererParams.mapView.setAspectRatio((float) width / height);

        GLHelper.checkError();
    }

    private long time = System.currentTimeMillis();

    @Override
    public void onDrawFrame(GL10 gl) {
        rendererParams.clock.update();

        if (rendererParams.clock.framesCount() % 100 == 0) {
            long now = rendererParams.clock.getTime();
            NedoLog.log("time per frame: " + (now-time)/100 + " ms, fps = " + rendererParams.clock.fps());
            time = now;
        }

        glFrontFace(GL_CCW);
        glCullFace(GL_FRONT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        GLHelper.checkError();

        float t = (rendererParams.clock.framesCount()%(628*2))/100f;
        t*=2;
        iRectangle2i mapSize = rendererParams.world.metrics.mapSize();
        Point2i position = new Point2i().set(mapSize.xCenter(), mapSize.yCenter());

        rendererParams.mapView.setDirectionOfView(FloatMath.sin(t), FloatMath.cos(t));
        rendererParams.mapView.setInclination(30+25*FloatMath.sin(t+0.345f));
        rendererParams.mapView.setCenterPosition(position);
        rendererParams.mapView.setScale((1.0f + 0.7f*FloatMath.sin(t)) / 20);

        rendererParams.lightningView.setDirectionOfView(FloatMath.sin(t / 2), FloatMath.cos(t / 2));
        rendererParams.lightningView.setInclination(30+0*FloatMath.cos(t/2));
        rendererParams.lightningView.setCenterPosition(position);
        rendererParams.lightningView.setScale(0.005f);
        rendererParams.lightningView.setAspectRatio(1f);

        for(GameRenderable rend : renderers){
            rend.render(rendererParams);
            GLHelper.checkError();
        }
    }
}
