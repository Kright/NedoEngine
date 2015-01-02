package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.NedoEngine.math.Matrix4_4f;
import com.vk.lgorsl.NedoEngine.math.Point2i;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;

/**
 * view for light calculations
 *
 * Created by lgor on 02.01.2015.
 */
public class LightningView extends MapView {

    private final Matrix4_4f anotherProjection = new Matrix4_4f();
    private final Matrix4_4f multiplier = new Matrix4_4f();

    public LightningView(Point2i center, WorldMetrics metrics) {
        super(center, metrics);
        multiplier.set(
                0.5f, 0, 0, 0.5f,
                0, 0.5f, 0, 0.5f,
                0, 0, 0.5f, 0.5f,
                0, 0, 0, 1);
    }

    @Override
    public Matrix4_4f anotherProjection() {
        return anotherProjection;
    }

    @Override
    protected void constructMatrix() {
        super.constructMatrix();
        anotherProjection.multiplication(multiplier, matrix);
    }
}
