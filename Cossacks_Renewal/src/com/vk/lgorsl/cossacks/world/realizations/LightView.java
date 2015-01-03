package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Helper;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Vect2f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.NedoEngine.utils.NedoException;
import com.vk.lgorsl.cossacks.world.interfaces.ViewBounds;
import com.vk.lgorsl.cossacks.world.interfaces.iLightView;

/**
 * it creates projection, which covers view of player camera
 *
 * Created by lgor on 03.01.2015.
 */
public class LightView implements iLightView {

    private float inclinationCos;
    private float inclinationSin;

    private final Vect3f viewDirection = new Vect3f();

    private final Vect2f direction = new Vect2f();
    private final Vect2f rightDir = new Vect2f();

    private final Vect2f[] verts = new Vect2f[4];

    private final Matrix4_4f projection = new Matrix4_4f();
    private final Matrix4_4f another = new Matrix4_4f();
    private final Matrix4_4f multiplier = new Matrix4_4f();

    private final Vect3f maxHeight = new Vect3f();

    private final float delta;

    private final ViewBounds viewBounds = new ViewBounds();

    public LightView(int maxHeight, int bounds){
        this.maxHeight.set(0, 0, maxHeight);
        this.delta = bounds;
    }

    {
        multiplier.set(
                0.5f, 0, 0, 0.5f,
                0, 0.5f, 0, 0.5f,
                0, 0, 0.5f, 0.5f,
                0, 0, 0, 1);

        for (int i = 0; i < verts.length; i++) {
            verts[i] = new Vect2f();
        }
    }

    @Override
    public void setInclination(float angleInDegrees) {
        float angle = Helper.radiansInDegree * angleInDegrees;
        inclinationCos = FloatMath.cos(angle);
        inclinationSin = FloatMath.sin(angle);
    }

    @Override
    public void setViewDirection(float x, float y) {
        direction.set(x, y).normalize();
    }

    @Override
    public void setToCover(ViewBounds bounds) {
        verts[0].set(bounds.leftDown);
        verts[1].set(bounds.leftUp);
        verts[2].set(bounds.rightDown);
        verts[3].set(bounds.rightUp);

        rightDir.set(-direction.y, direction.x);

        float maxF = verts[0].dot(direction);
        float minF = verts[0].dot(direction);
        float maxR = verts[0].dot(rightDir);
        float minR = verts[0].dot(rightDir);

        for(int i=1; i< verts.length; i++){
            maxF = Math.max(maxF, verts[i].dot(direction));
            minF = Math.min(minF, verts[i].dot(direction));
            maxR = Math.max(maxR, verts[i].dot(rightDir));
            minR = Math.min(minR, verts[i].dot(rightDir));
        }

        maxF += delta;
        minF -= delta;
        maxR += delta;
        minR -= delta;

        minF -= maxHeight.z * inclinationCos / inclinationSin;

        Vect3f forward = new Vect3f().set(direction.x, direction.y, 0);
        Vect3f right = new Vect3f().set(rightDir.x, rightDir.y, 0);

        Matrix4_4f temp = new Matrix4_4f();
        Vect3f rightUpFar = new Vect3f().madd(forward, maxF).madd(right, maxR).add(maxHeight);
        Vect3f leftUpFar = new Vect3f().madd(forward, maxF).madd(right, minR).add(maxHeight);
        Vect3f leftDownNear = new Vect3f().madd(forward, minF).madd(right, minR);

        Vect3f rightNear = new Vect3f().madd(forward, minF).madd(right, maxR).add(maxHeight);

        viewBounds.rightUp.set(rightUpFar.x, rightUpFar.y);
        viewBounds.leftUp.set(leftUpFar.x, leftUpFar.y);
        viewBounds.leftDown.set(leftDownNear.x, leftDownNear.y);
        viewBounds.rightDown.set(rightNear.x, rightNear.y);

        Vect3f xz = new Vect3f().madd(viewDirection(), maxF-minF);

        temp.setColumn(0, rightUpFar, 1);
        temp.setColumn(1, leftUpFar, 1);
        temp.setColumn(2, leftDownNear, 1);
        temp.setColumn(3, xz, 0);   //null is feature

        if(!temp.invert()){
            throw new NedoException("Matrix wasn't inverted!");
        }

        Matrix4_4f temp2 = new Matrix4_4f().set(
                1, -1, -1, 0,
                1,  1, -1, 0,
                1,  1, -1, 2,
                1,  1,  1, 0);


        projection.multiplication(temp2, temp);

        another.multiplication(multiplier, projection);
    }

    @Override
    public Vect3f viewDirection() {
        return viewDirection.set(direction.x * inclinationCos, direction.y * inclinationCos, -inclinationSin);
    }

    @Override
    public Matrix4_4f projection() {
        return projection;
    }

    @Override
    public ViewBounds viewBounds() {
        return viewBounds;
    }

    @Override
    public Matrix4_4f anotherProjection() {
        return another;
    }
}
