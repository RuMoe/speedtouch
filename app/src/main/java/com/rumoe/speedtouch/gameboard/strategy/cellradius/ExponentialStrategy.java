package com.rumoe.speedtouch.gameboard.strategy.cellradius;

public class ExponentialStrategy implements CellRadiusCalcStrategy {

    private static final double DEFAULT_EXPONENT = 3.5;
    private double exponent;

    public ExponentialStrategy() {
        this(DEFAULT_EXPONENT);
    }

    public ExponentialStrategy(double exponent) {
        this.exponent = exponent;
    }

    public float calculateRadius(float startRadius, float targetRadius, float currentRadius,
                          int stepNumber, int totalNumberOfSteps) {
            // prevent floating point errors in final result
        if (stepNumber == totalNumberOfSteps) return targetRadius;

        double progressInPercent = stepNumber / (double) totalNumberOfSteps;
        double changeInPercent = Math.pow(progressInPercent, exponent);

        return startRadius + ((float) ((targetRadius - startRadius) * changeInPercent));
    }
}
