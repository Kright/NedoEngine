package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.Helper;
import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Vect2f;
import com.vk.lgorsl.NedoEngine.math.Vect3f;
import com.vk.lgorsl.cossacks.world.interfaces.ViewBounds;
import com.vk.lgorsl.cossacks.world.interfaces.iLightView;

/**
 * it creates projection, which covers view of player camera
 * <p>
 * Created by lgor on 03.01.2015.
 */
public class LightView implements iLightView {

    private float inclinationCos;
    private float inclinationSin;

    private final Vect3f viewDirection = new Vect3f();

    private final Vect2f direction = new Vect2f();
    private final Vect2f rightDir = new Vect2f();

    private final Matrix4_4f projection = new Matrix4_4f();
    private final Matrix4_4f another = new Matrix4_4f();
    private final Matrix4_4f multiplier = new Matrix4_4f();
    private final Matrix4_4f constMatrix = new Matrix4_4f();

    private final float maxHeight;

    private final float delta;

    private final ViewBounds viewBounds = new ViewBounds();

    public LightView(int maxHeight, int bounds) {
        this.maxHeight = maxHeight;
        this.delta = bounds;
    }

    {
        multiplier.set(
                0.5f, 0, 0, 0.5f,
                0, 0.5f, 0, 0.5f,
                0, 0, 0.5f, 0.5f,
                0, 0, 0, 1);

        constMatrix.set(
                1, -1, -1, 0,
                1, 1, -1, 0,
                1, 1, -1, 2,
                1, 1, 1, 0);
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

        rightDir.set(-direction.y, direction.x);

        float maxF = maxDot(direction, bounds) + delta;
        float minF = minDot(direction, bounds) - delta;
        float maxR = maxDot(rightDir, bounds) + delta;
        float minR = minDot(rightDir, bounds) - delta;

        minF -= maxHeight * inclinationCos / inclinationSin;

        viewBounds.rightUp.x = direction.x*maxF + rightDir.x*maxR;
        viewBounds.rightUp.y = direction.y*maxF + rightDir.y*maxR;

        viewBounds.leftUp.x = direction.x*maxF + rightDir.x*minR;
        viewBounds.leftUp.y = direction.y*maxF + rightDir.y*minR;

        viewBounds.leftDown.x = direction.x*minF + rightDir.x*minR;
        viewBounds.leftDown.y = direction.y*minF + rightDir.y*minR;

        viewBounds.rightDown.x = direction.x*minF + rightDir.x*maxR;
        viewBounds.rightDown.y = direction.y*minF + rightDir.y*maxR;

        viewBounds.update();

        projection.setColumn(0, viewBounds.rightUp.x, viewBounds.rightUp.y, maxHeight, 1);
        projection.setColumn(1, viewBounds.leftUp.x, viewBounds.leftUp.y, maxHeight, 1);
        projection.setColumn(2, viewBounds.leftDown.x, viewBounds.leftDown.y, 0, 1);
        projection.setColumn(3, direction.x*inclinationCos*(maxF-minF), direction.y*inclinationCos*(maxF-minF), -inclinationSin*(maxF-minF), 0);

        projection.invert();

        projection.multiplication(constMatrix, projection);
        another.multiplication(multiplier, projection);
    }

    private float maxDot(Vect2f r, ViewBounds bounds){
        float dot, max;
        max = bounds.leftDown.dot(r);
        if ((dot = bounds.leftUp.dot(r)) > max) max = dot;
        if ((dot = bounds.rightDown.dot(r)) > max) max = dot;
        if ((dot = bounds.rightUp.dot(r)) > max) max = dot;
        return max;
    }

    private float minDot(Vect2f r, ViewBounds bounds){
        float dot, min;
        min = bounds.leftDown.dot(r);
        if ((dot = bounds.leftUp.dot(r)) < min) min = dot;
        if ((dot = bounds.rightDown.dot(r)) < min) min = dot;
        if ((dot = bounds.rightUp.dot(r)) < min) min = dot;
        return min;
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
