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

    protected final Matrix4_4f matrix = new Matrix4_4f();

    private final WorldMetrics metrics;

    protected final ViewBounds viewBounds = new ViewBounds();
    protected final Point2i center = new Point2i();
    protected final Vect2f viewDirection = new Vect2f().set(0, 1);

    private boolean matrixUpdated = false;

    private float
            scale = 0.001f,
            aspectRatio = 1,
            inclinationCos = FloatMath.sqrt(2) / 2,
            inclinationSin = FloatMath.sqrt(2) / 2;

    public MapView(Point2i center, WorldMetrics metrics) {
        this.metrics = metrics;
        this.center.set(center);
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
        viewDirection.set(dx, dy).normalize();
        matrixUpdated = false;
    }

    @Override
    public void setAspectRatio(float ratio) {
        aspectRatio = ratio;
        matrixUpdated = false;
    }

    @Override
    public void getViewDirection(Vect3f result) {
        result.set(viewDirection.x * inclinationCos, viewDirection.y * inclinationCos, -inclinationSin);
    }

    @Override
    public void setInclination(float angleInDegrees) {
        float angle = Helper.radiansInDegree * angleInDegrees;
        inclinationCos = FloatMath.cos(angle);
        inclinationSin = FloatMath.sin(angle);
        matrixUpdated = false;
    }

    @Override
    public Matrix4_4f projection() {
        updateMatrixAndBounds();
        return matrix;
    }

    @Override
    public Matrix4_4f anotherProjection() {
        return matrix;
    }

    @Override
    public ViewBounds viewBounds() {
        updateMatrixAndBounds();
        return viewBounds;
    }

    protected void updateMatrixAndBounds() {
        if (matrixUpdated) return;
        matrixUpdated = true;

        constructMatrix();
        constructViewBounds();
    }

    protected void constructMatrix(){
        float maxHeight = metrics.maxHeight();
        float minHeight = -metrics.meterSize();

        float xz = 1f / scale / inclinationSin;
        float maxDepth = xz - minHeight * inclinationCos / inclinationSin;
        float minDepth = -xz - maxHeight * inclinationCos / inclinationSin;

        final float scaledX = viewDirection.x * scale;
        final float scaledY = viewDirection.y * scale;

        final float oldXx = -scaledY / aspectRatio;
        final float oldXy = scaledX * inclinationSin;
        final float oldYx = scaledX / aspectRatio;
        final float oldYy = scaledY * inclinationSin;

        final float oldZy = inclinationCos * scale;

        float rowZx = scaledX * inclinationCos;
        float rowZy = scaledY * inclinationCos;

        final float alpha = 2f / (maxDepth - minDepth) / (viewDirection.x * rowZx + viewDirection.y * rowZy);

        rowZx *= alpha;
        rowZy *= alpha;

        final float cx = center.x + maxDepth * viewDirection.x;
        final float cy = center.y + maxDepth * viewDirection.y;

        float[] arr = matrix.getArray();

        arr[0] = oldXx;
        arr[1] = oldXy;
        arr[2] = rowZx;
        arr[3] = 0;

        arr[4] = oldYx;
        arr[5] = oldYy;
        arr[6] = rowZy;
        arr[7] = 0;

        arr[8] = 0;
        arr[9] = oldZy;
        arr[10] = 0;
        arr[11] = 0;

        arr[12] = -oldXx * center.x - oldYx * center.y;
        arr[13] = -oldXy * center.x - oldYy * center.y;
        arr[14] = 1 - rowZx * cx - rowZy * cy;
        arr[15] = 1f;
    }

    protected void constructViewBounds(){
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
    }
}
