package com.rumoe.speedtouch.game.strategy.cellradius;

import android.view.animation.Interpolator;

/**
 * Created by jan on 06.09.2014.
 */
public class BlinkInterpolator implements Interpolator {

    private static final int NUMBER_OF_BLINKS = 3;

    @Override
    public float getInterpolation(float input) {
        int numberOfSteps = NUMBER_OF_BLINKS * 2;
            // the first step is 1
        int currentStep = (int) (input * numberOfSteps) + 1;

        return (currentStep % 2 == 1)? 1f : 0f;
    }
}
