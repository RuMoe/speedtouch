package com.rumoe.speedtouch.game.strategy.cellradius;


public interface CellRadiusCalcStrategy {

    float calculateRadius(float startRadius, float targetRadius, float currentRadius,
                          int stepNumber, int totalNumberOfSteps);
}
