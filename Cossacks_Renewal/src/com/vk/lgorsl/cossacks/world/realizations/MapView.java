package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.*;
import com.vk.lgorsl.cossacks.world.interfaces.ViewBounds;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iMapView;

/**
 * realization of isometric camera view
 * <p>
 * Created by lgor on 15.12.2014.
 */
public class MapView implements iMapView {

    private Point2i center;
    private final WorldMetrics metrics;

    private final ViewBounds viewBounds = new ViewBounds();
    private final Matrix4_4f matrix = new Matrix4_4f();
    private final Vect2f directionOfView = new Vect2f().set(0, 1);

    private float scale = 0.001f;
    private float inclinationAngle = 60 / 180 * (float) Math.PI;

    private float aspectRatio = 1f;

    private boolean matrixUpdated = false;

    private final Vect3f
            xProjection = new Vect3f(),
            yProjection = new Vect3f(),
            upProjection = new Vect3f();

    public MapView(Point2i center, WorldMetrics metrics) {
        this.center = center;
        this.metrics = metrics;
    }

    @Override
    public iPoint2i center() {
        return center;
    }

    @Override
    public void setCenterPosition(iPoint2i newCenter) {
        if (metrics.mapSize().contains(newCenter)) {
            center.set(newCenter);
            matrixUpdated = false;
        }
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale / metrics.meterSize();
        matrixUpdated = false;
    }

    @Override
    public void setDirectionOfView(float dx, float dy) {
        directionOfView.set(dx, dy).normalize();
        matrixUpdated = false;
    }

    @Override
    public void setAspectRatio(float ratio) {
        this.aspectRatio = ratio;
        matrixUpdated = false;
    }

    @Override
    public void getViewDirection(Vect3f result) {
        final float sin = FloatMath.sin(inclinationAngle);
        final float cos = FloatMath.cos(inclinationAngle);
        result.set(directionOfView.x*cos, directionOfView.y*cos, -sin);
    }

    @Override
    public void setInclination(float angle) {
        inclinationAngle = angle / 180 * (float) Math.PI;
        matrixUpdated = false;
    }

    @Override
    public Matrix4_4f projection() {
        updateMatrixAndBounds();
        return matrix;
    }

    @Override
    public ViewBounds viewBounds() {
        updateMatrixAndBounds();
        return viewBounds;
    }

    private void updateMatrixAndBounds() {
        if (matrixUpdated) return;
        matrixUpdated = true;
        final float sin = FloatMath.sin(inclinationAngle);
        final float cos = FloatMath.cos(inclinationAngle);

        //TODO calculate coefficient
        float k=0.6f;

        xProjection.set(-directionOfView.y, directionOfView.x*sin, directionOfView.x*k*sin).mul(scale/aspectRatio);
        yProjection.set(directionOfView.x, directionOfView.y*sin, directionOfView.y*k*sin).mul(scale/aspectRatio);
        upProjection.set(0, cos*scale, 0);

        matrix.setColumn(0, xProjection, 0);
        matrix.setColumn(1, yProjection, 0);
        matrix.setColumn(2, upProjection, 0);

        matrix.setColumn(3,
                -(center.x * xProjection.x + center.y * yProjection.x),
                -(center.x * xProjection.y + center.y * yProjection.y),
                -(center.x * xProjection.y + center.y * yProjection.y) * k + (1 - k)*0.5f,
                1f);

        /**
         *  i.x     j.x     0       dx
         *  i.y*sin j.y*sin cos     dy
         *  -||-*k  -||-*k  0       dy*k+(1-k)
         *  0       0       0       1
         *
         *  and scaled
         */
        float[] arr = matrix.getArray();

        Matrix2_2f mat2 = new Matrix2_2f().set(arr[0], arr[4], arr[2], arr[6]);
        mat2.invert();

        Vect2f vCenter = new Vect2f().set(arr[12], arr[14]);
        mat2.mul(vCenter, vCenter);
        vCenter.mul(-1);


        Vect2f right = new Vect2f().set(1, 0);
        Vect2f forward = new Vect2f().set(0, 1);

        mat2.mul(right, right);
        mat2.mul(forward, forward);

        right.mul(1.1f);
        forward.mul(1.1f);

        viewBounds.rightUp.set(vCenter).add(right).add(forward);
        viewBounds.rightDown.set(vCenter).add(right).sub(forward);
        viewBounds.leftUp.set(vCenter).sub(right).add(forward);
        viewBounds.leftDown.set(vCenter).sub(right).sub(forward);

        //viewBounds.rightUp.set(vCenter);
    }
}
