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
    private RendererParams rendererParams;

    //4 debug
    Quad quad;

    public GameRenderer(Context context){
        world = new WorldInstance();
        world.load();

        //rendererParams = new RendererParams(world, new MapView(new Point2i().set(32, 32),world.metrics));
        rendererParams = new RendererParams(context.getResources());
        rendererParams.world = world;
        rendererParams.mapView = new MapView(new Point2i().set(32,32), world.metrics);

        rendererParams.lightningView = new MapView(new Point2i().set(32, 32),world.metrics);

        clock = new FPSCounter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        NedoLog.log("surface Created");

        GLHelper.checkError();

        renderers.add(new LightRenderer());
        renderers.add(new LandMeshRenderer());
        //renderers.add(new LandMeshGridRenderer());

        quad = new Quad(null);
        quad.load(null);

        for(GameRenderable rend : renderers){
            rend.load(rendererParams);
        }

        Shader.releaseCompiler();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        NedoLog.log("surface Updated, w = " + width + ", h = " + height );
        rendererParams.defaultViewPortSize.set(width, height);
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
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        GLHelper.checkError();

        glFrontFace(GL_CCW);
        glCullFace(GL_FRONT);
        glEnable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);

        GLHelper.checkError();

        float t = (clock.framesCount()%(628*2))/100f;
        t*=2;
        iRectangle2i mapSize = rendererParams.world.metrics.mapSize();
        Point2i position = new Point2i().set(mapSize.xCenter(), mapSize.yCenter());

        rendererParams.mapView.setDirectionOfView(FloatMath.sin(t), FloatMath.cos(t));
        rendererParams.mapView.setInclination(40+20*FloatMath.sin(t+0.345f));
        rendererParams.mapView.setCenterPosition(position);
        rendererParams.mapView.setScale((1.0f + 0.5f*FloatMath.sin(t)) / 80);

        rendererParams.lightningView.setDirectionOfView(FloatMath.sin(t/2), FloatMath.cos(t/2));
        rendererParams.lightningView.setInclination(20+10*FloatMath.cos(t/2));
        rendererParams.lightningView.setCenterPosition(position);
        rendererParams.lightningView.setScale(0.01f);

        for(GameRenderable rend : renderers){
            rend.render(rendererParams);
            GLHelper.checkError();
        }

        /*
        glClearColor(0.5f, c, 1-c, 0);
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_CULL_FACE);

        Texture2D depthTexture = rendererParams.depthTexture;
        if (depthTexture!=null) {
            quad.setTexture(depthTexture);
            Matrix4_4f m = new Matrix4_4f();
            m.getArray()[5]=-1;
            quad.render(m);
        }
        */
    }
}
