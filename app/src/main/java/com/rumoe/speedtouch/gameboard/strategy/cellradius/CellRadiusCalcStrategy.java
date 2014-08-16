package com.rumoe.speedtouch.gameboard.strategy.cellradius;


public interface CellRadiusCalcStrategy {

    float calculateRadius(float targetRadius, float currentRadius,
                          int stepNumber, int remainingSteps);
}
