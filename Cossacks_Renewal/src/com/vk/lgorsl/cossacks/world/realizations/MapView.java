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
    private float scale = 0.001f;
    private float inclinationAngle = 60;

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
        this.scale = scale;
        updateMatrixAndBounds();
    }

    @Override
    public void setDirectionOfView(float dx, float dy) {
        directionOfView.set(dx, dy).normalize();
        updateMatrixAndBounds();
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
        matrix4_4f.makeIdentity();
        float[] array = matrix4_4f.getArray();
        final float sin = FloatMath.sin(inclinationAngle);
        final float cos = FloatMath.cos(inclinationAngle);
        Vect2f vi = new Vect2f().set(directionOfView.y, directionOfView.x);
        Vect2f vj = new Vect2f().set(-directionOfView.x, directionOfView.y);

        vi.mul(scale);
        vj.mul(scale);

        array[0] = vi.x;
        array[4] = vj.x;

        array[1] = vi.y*sin;
        array[5] = vj.y*sin;
        array[9] = cos*scale;

        //TODO calculate coefficient
        float k=1f;
        array[2] = vi.y*k;
        array[6] = vj.y*k;

        array[12] = - (center.x * vi.x + center.y * vj.x);
        array[13] = - (center.x * vi.y + center.y * vj.y);

        /**
         *  i.x     j.x     0       dx
         *  i.y*sin j.y*sin cos     dy
         *  k*vi.y  k*vj.y  0       0
         *  0       0       0       1
         *
         *  and scaled
         */

        //TODO calculating bounds!
        viewBounds.set(metrics.mapSize());
    }
}
