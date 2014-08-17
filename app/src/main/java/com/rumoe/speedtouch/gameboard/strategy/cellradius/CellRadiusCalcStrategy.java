package com.rumoe.speedtouch.gameboard.strategy.cellradius;


public interface CellRadiusCalcStrategy {

    float calculateRadius(float startRadius, float targetRadius, float currentRadius,
                          int stepNumber, int totalNumberOfSteps);
}
