package com.vk.lgorsl.TryGLgame;

import android.content.Context;
import android.graphics.Bitmap;

import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.openGL.GLHelper;
import com.vk.lgorsl.NedoEngine.openGL.Texture2D;
import com.vk.lgorsl.NedoEngine.openGL.TextureLoader;
import com.vk.lgorsl.NedoEngine.utils.FPSCounter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import static android.opengl.GLES20.*;

/**
 * it can draw sprites (now it id drawing alphabet)
 *
 * Created by lgor on 23.11.2014.
 */
public class TempRenderer implements GLSurfaceView.Renderer{

    final Context context;
    final FPSCounter counter;

    public TempRenderer(Context context){
        this.context = context;
        counter = new FPSCounter();
    }

    Texture2D font;
    SpriteShader shader;
    FloatBuffer fb;
    ShortBuffer sb;
    final int count = 100;
    Matrix4_4f matrix4_4f = new Matrix4_4f().makeScale(2/1080f, 2/1780f, 1f);

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Bitmap bm = GLHelper.loadBitmap(context.getResources(), R.drawable.font22cons);
        font = TextureLoader.loadTexture(bm,
                GL_NEAREST,
                GL_NEAREST,
                GL_CLAMP_TO_EDGE,
                GL_CLAMP_TO_EDGE,
                false);
        font.bind();
        shader = new SpriteShader(context.getResources(), R.raw.sprite);

        float s = 2;
        Random rnd = new Random(1234);
        float[] ff= new float[count*5*4];
        float[] dX = {16f*s, 1/32f};
        float[] dY = {32f*s, 1/16f};
        short[] ss = new short[6*count];
        for(int i=0; i<count; i++){
            ss[i*6] = (short)(0+4*i);
            ss[i*6+1] = (short)(1+4*i);
            ss[i*6+2] = (short)(3+4*i);
            ss[i*6+3] = (short)(0+4*i);
            ss[i*6+4] = (short)(3+4*i);
            ss[i*6+5] = (short)(2+4*i);
            float x = rnd.nextInt(1000)-500;
            float y = rnd.nextInt(1000)-500;
            float tx = rnd.nextInt(26)*dX[1];
            float ty = rnd.nextInt(4)*dY[1];
            float z = (rnd.nextInt(2)-0.5f)*(1f + rnd.nextFloat());
            int off = i*20;
            for(int j=0; j<2; j++){
                for(int k=0; k<2; k++){
                    ff[off++] = x + j*dX[0];
                    ff[off++] = y + k*dY[0];
                    ff[off++] = z;
                    ff[off++] = tx + k*dX[1];
                    ff[off++] = (ty + j*dY[1]);
                }
            }
        }
        fb = GLHelper.make(ff);
        sb = GLHelper.make(ss);

        /*
        fb = GLHelper.make(new float[]{
                -400, 400, 0, 0, 0,
                400, 400, 0, 1, 0,
                -400, -400, 0, 0, 1,
                400, -400, 0, 1, 1
        });
        sb = GLHelper.make(new short[]{
                0, 1, 3, 0, 3, 2
        });*/
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0 , width, height);

        shader.useProgram();
        font.use(0);

        glEnableVertexAttribArray(shader.aScreenPos);
        glEnableVertexAttribArray(shader.aTexturePos);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        counter.update();

        float t = 0.01f*(counter.framesCount() % 628);
        float ampX = 0.1f;
        float ampY = 0.2f;
        matrix4_4f.getArray()[11]=1;
        matrix4_4f.setTranslation(ampX*FloatMath.sin(t*2+0.2f), ampY*FloatMath.cos(5*t+0.6f), 0);

        float c = counter.framesCount() % 2 ==0 ? 0 : 1;

        glClearColor(0.5f, c, c, 0);
        glClearDepthf(1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        fb.position(0);
        glVertexAttribPointer(shader.aScreenPos, 3, GL_FLOAT, false, 4 * 5, fb);
        fb.position(3);
        glVertexAttribPointer(shader.aTexturePos, 2, GL_FLOAT, false, 4 * 5, fb);

        glUniformMatrix4fv(shader.uCamera, 0, false, matrix4_4f.getArray(), 0);
        glUniform1i(shader.uTexture, 0);

        glDrawElements(GL_TRIANGLES, count*6, GL_UNSIGNED_SHORT, sb);
        //glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, sb);
    }
}
