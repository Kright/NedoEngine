package com.vk.lgorsl.cossacks.world.realizations;

import android.util.FloatMath;
import com.vk.lgorsl.NedoEngine.math.*;
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

    private final Rectangle2i viewBounds = new Rectangle2i(0, 0, 0, 0);
    private final Matrix4_4f matrix4_4f = new Matrix4_4f();
    private final Vect2f directionOfView = new Vect2f().set(0, 1);

    private final float baseScale = 0.001f;

    private float scale = 0.001f;
    private float scaleVertical = 1f;
    private float inclinationAngle = 60 / 180 * (float) Math.PI;

    private final Vect3f
            xProjection = new Vect3f(),
            yProjection = new Vect3f(),
            upProjection = new Vect3f();

    public MapView(Point2i center, WorldMetrics metrics) {
        this.center = center;
        this.metrics = metrics;

        updateMatrixAndBounds();
    }

    @Override
    public iPoint2i center() {
        return center;
    }

    @Override
    public void setCenterPosition(iPoint2i newCenter) {
        if (metrics.mapSize().contains(newCenter)) {
            center.set(newCenter);
            updateMatrixAndBounds();
        }
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale * baseScale;
        updateMatrixAndBounds();
    }

    @Override
    public void setVerticalScale(float scale) {
        scaleVertical = scale;
        updateMatrixAndBounds();
    }

    @Override
    public void setDirectionOfView(float dx, float dy) {
        directionOfView.set(dx, dy).normalize();
        updateMatrixAndBounds();
    }

    @Override
    public Vect3f getXProjection() {
        return xProjection;
    }

    @Override
    public Vect3f getYProjection() {
        return yProjection;
    }

    @Override
    public Vect3f getUpProjection() {
        return upProjection;
    }

    @Override
    public void setInclination(float angle) {
        inclinationAngle = angle / 180 * (float) Math.PI;
        updateMatrixAndBounds();
    }

    @Override
    public Matrix4_4f projection() {
        return matrix4_4f;
    }

    @Override
    public iRectangle2i boundingBox() {
        return viewBounds;
    }

    private void updateMatrixAndBounds() {
        final float sin = FloatMath.sin(inclinationAngle);
        final float cos = FloatMath.cos(inclinationAngle);

        //TODO calculate coefficient
        float k=0.8f;

        xProjection.set(directionOfView.y, directionOfView.x*sin, directionOfView.x*k*sin).mul(scale);
        yProjection.set(-directionOfView.x, directionOfView.y*sin, directionOfView.y*k*sin).mul(scale);
        upProjection.set(0, cos*scale*scaleVertical, 0);

        matrix4_4f.setColumn(0, xProjection, 0);
        matrix4_4f.setColumn(1, yProjection, 0);
        matrix4_4f.setColumn(2, upProjection, 0);

        matrix4_4f.setColumn(3,
                - (center.x * xProjection.x + center.y * yProjection.x),
                - (center.x * xProjection.y + center.y * yProjection.y),
                - (center.x * xProjection.y + center.y * yProjection.y)*k + (1-k),
                1f);

        /**
         *  i.x     j.x     0       dx
         *  i.y*sin j.y*sin cos     dy
         *  -||-*k  -||-*k  0       dy*k+(1-k)
         *  0       0       0       1
         *
         *  and scaled
         */

        //TODO calculating bounds!
        viewBounds.set(metrics.mapSize());
    }
}
