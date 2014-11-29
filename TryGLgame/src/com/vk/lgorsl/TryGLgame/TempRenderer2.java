package com.vk.lgorsl.TryGLgame;

import android.content.Context;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.openGL.*;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;
import com.vk.lgorsl.NedoEngine.utils.NedoLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * used for experiments
 *
 * Created by lgor on 29.11.2014.
 */
public class TempRenderer2 implements GLSurfaceView.Renderer {

    final FPSCounter counter = new FPSCounter();
    final Context context;


    Squad squad;
    FontTexture font;
    final Matrix4_4f matrix4_4f;
    FontRenderer fontRenderer;

    public TempRenderer2(Context context) {
        this.context = context;
        matrix4_4f = new Matrix4_4f();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        font = FontTexture.load(
                Typeface.createFromAsset(context.getAssets(), "newcourier.ttf"),
                1024, 64,
                FontTexture.ENGLISH_LOWERCASE +
                        FontTexture.ENGLISH_UPPERCASE +
                        FontTexture.SYMBOLS +
                        FontTexture.RUSSIAN_LOWERCASE +
                        FontTexture.RUSSIAN_UPPERCASE);

        squad = new Squad(font);

        GLHelper.checkError();

        fontRenderer = new FontRenderer(font);
        fontRenderer.setText("qwertyuiop\n" +
                "some epic text!\nи на русском тоже\n" +
                "И НАЖМЁМ CAPS LOCK!\n" +
                "wtf there?!\n...");
        fontRenderer.setColor(1f, 0, 1f, 0.5f, true);

        GLHelper.checkError();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        GLHelper.checkError();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        counter.update();
        if (counter.framesCount() % 200 == 0) {
            NedoLog.log("fps = " + counter.fps());
        }

        float c = counter.framesCount() % 2 == 0 ? 0 : 1;

        glClearColor(0.5f, c, c, 0);
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float t = (counter.framesCount() % 628) * 0.01f;

        float s = 0.2f + 0.1f*FloatMath.sin(t);
        matrix4_4f.makeIdentity().makeScale(s*0.5f*5, s*5, 1);

        GLHelper.checkError();

        squad.render(matrix4_4f);

        matrix4_4f.getArray()[0]*=0.2f;
        matrix4_4f.getArray()[5]*=0.3f;

        fontRenderer.setColor(FloatMath.sin(t), 0.0f, 0.5f, .7f+ 0.3f*FloatMath.cos(2*t+1), true);

        fontRenderer.render(matrix4_4f);

        GLHelper.checkError();
    }
}
