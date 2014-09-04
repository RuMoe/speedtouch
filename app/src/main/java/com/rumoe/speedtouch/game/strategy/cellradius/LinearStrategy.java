package com.rumoe.speedtouch.game.strategy.cellradius;

public class LinearStrategy implements CellRadiusCalcStrategy {

    @Override
    public float calculateRadius(float startRadius, float targetRadius, float currentRadius,
                                 int stepNumber, int totalNumberOfSteps) {

        int remainingSteps = totalNumberOfSteps - stepNumber + 1;

        float radiusDif = targetRadius - currentRadius;
        return  currentRadius + radiusDif / remainingSteps;
    }
}
