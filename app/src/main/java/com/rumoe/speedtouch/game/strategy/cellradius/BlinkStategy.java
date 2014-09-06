package com.rumoe.speedtouch.game.strategy.cellradius;

/**
 * Created by jan on 06.09.2014.
 */
public class BlinkStategy implements CellRadiusCalcStrategy {

    private static final int NUMBER_OF_BLINKS = 3;

    @Override
    public float calculateRadius(float startRadius, float targetRadius, float currentRadius,  int stepNumber, int totalNumberOfSteps) {
        if (stepNumber == totalNumberOfSteps) return targetRadius;

        return isTargetStep(stepNumber,totalNumberOfSteps) ? targetRadius : startRadius;
    }

    private boolean isTargetStep(int stepNumber, int totalNumberOfSteps) {
        int baudRateInv = totalNumberOfSteps / (NUMBER_OF_BLINKS * 2);  //inverse baud rate in steps

        return (stepNumber / baudRateInv) % 2 == 0;
    }
}
