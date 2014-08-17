package com.rumoe.speedtouch.gameboard.strategy.cellradius;

public class LinearGrowthStrategy implements CellRadiusCalcStrategy {

    @Override
    public float calculateRadius(float targetRadius, float currentRadius,
                                 int stepNumber, int totalNumberOfSteps) {

        int remainingSteps = totalNumberOfSteps - stepNumber + 1;

        float radiusDif = targetRadius - currentRadius;
        return  currentRadius + radiusDif / remainingSteps;
    }
}
