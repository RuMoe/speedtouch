package com.rumoe.speedtouch.gameboard.thread;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.gameboard.CellType;
import com.rumoe.speedtouch.gameboard.strategy.cellradius.CellRadiusCalcStrategy;
import com.rumoe.speedtouch.gameboard.strategy.cellradius.ExponentialStrategy;
import com.rumoe.speedtouch.gameboard.strategy.cellradius.LinearStrategy;

public class CellAnimation implements Runnable {

    public static final int DEFAULT_GROW_ANIMATION_DURATION    = 100;
    public static final int DEFAULT_SHRINK_ANIMATION_DURATION  = 2000;

    private Context context;
    private CellType cellType;

    private SurfaceHolder cellSurface;
    private Paint cellPaint;

    private float currentCellRadius;
    private float minCellRadius;
    private float maxCellRadius;
    private float cellXCenter;
    private float cellYCenter;

    private Thread drawThread;
    private CellRadiusCalc calculationThread;
    private boolean animationRunning;

    public CellAnimation(SurfaceHolder surface, CellType cellType, Context context) {
        this.context = context;
        this.cellType = cellType;

        cellSurface = surface;
        animationRunning = false;

        updatePaint();
    }

    private void updatePaint() {
        cellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellPaint.setColor(CellType.getCellColor(cellType, context));
        cellPaint.setShadowLayer(15.0f, 0.0f, 0.0f, CellType.getShadowColor(cellType, context));
    }

    /**
     * This should be called whenever the dimensions of the cells are changed.
     * Updates the cell center as well as the maximal and minimal cell size.
     * @param cellWidth The width of the cell-surface
     * @param cellHeight The height of the cell-surface
     * @param cellPadding The padding of the cell
     */
    public void setDimensions(int cellWidth, int cellHeight, int cellPadding) {
        maxCellRadius = (Math.min(cellWidth, cellHeight) - 2 * cellPadding) * 0.5f;
        minCellRadius = 0;

        cellXCenter = cellWidth / 2.0f;
        cellYCenter = cellHeight / 2.0f;
    }

    /**
     * Sets the type of the cell. This will affect its color.
     */
    public void setCellType(CellType newType) {
        this.cellType = newType;
        updatePaint();
    }

    /**
     * Fills the cell with its background color.
     * @return true iif operation executed successfully, false otherwise
     */
    public boolean clearBackground() {
        try {
            if (cellSurface.getSurface().isValid()) {
                Canvas canvas = cellSurface.lockCanvas();
                canvas.drawColor(context.getResources().getColor(R.color.game_board_background));
                cellSurface.unlockCanvasAndPost(canvas);
                return true;
            } else {
                Log.e("CellAnimation", "Surface invalid while clearing background");
                return false;
            }
        } catch (NullPointerException e) {
            // we need this in case the surface is destroyed during drawing
            Log.e("CellAnimation", "Surface destroyed while trying to clear background");
            return false;
        }
    }

    /**
     * Check if given point lies in the currently displayed circle.
     * @param xCoord x-Coordinate of the point.
     * @param yCoord y-Coordinate of the point.
     * @return true iff the point is on or in the circle, false otherwise
     */
    public boolean isTargetHit(float xCoord, float yCoord) {
        float xDif = xCoord - cellXCenter;
        float yDif = yCoord - cellYCenter;

        return xDif*xDif + yDif*yDif <= currentCellRadius*currentCellRadius;
    }

    public boolean isAnimationRunning() {
        return (drawThread != null && drawThread.isAlive());
    }

    public boolean growAnimation() {
        return growAnimation(DEFAULT_GROW_ANIMATION_DURATION);
    }

    public boolean growAnimation(int duration) {
        return growAnimation(new LinearStrategy(), duration);
    }

    public boolean growAnimation(CellRadiusCalcStrategy strategy, int duration) {
       if (isAnimationRunning()) return false;

        calculationThread = new CellRadiusCalc(strategy, duration, currentCellRadius, maxCellRadius);
        calculationThread.start();

        drawThread = new Thread(this);
        drawThread.start();

        return true;
    }

    public boolean shrinkAnimation() {
        return shrinkAnimation(DEFAULT_SHRINK_ANIMATION_DURATION);
    }

    public boolean shrinkAnimation(int duration) {
        return shrinkAnimation(new ExponentialStrategy(), duration);
    }

    public boolean shrinkAnimation(CellRadiusCalcStrategy strategy, int duration) {
        if (isAnimationRunning()) return false;

        calculationThread = new CellRadiusCalc(strategy, duration, currentCellRadius, minCellRadius);
        calculationThread.start();

        drawThread = new Thread(this);
        drawThread.start();

        return true;
    }

    /**
     * Blocks the calling thread until the animation is ended.
     * @return false iff block was interrupted, true otherwise.
     */
    public boolean waitUntilAnimationEnded() {
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * Clear the content of the cell and stop all animations.
     * @return true
     */
    public boolean clearCell() {
        stopAnimation();

        currentCellRadius = 0;
        clearBackground();
        return true;
    }

    /**
     * Stop all running threads.
     */
    public void stopAnimation() {
        animationRunning = false;

        if (calculationThread != null) {
            calculationThread.abortCalculations();
        }

        if (drawThread != null) {
            drawThread.interrupt();
        }
    }

    @Override
    /**
     * As long as the thread for recalculation of the cell radius works, update
     * the cell for the user in regular intervals.
     */
    public void run() {
        int framesDrawn = 0;
        try {
            // the variable oneMore is necessary the make sure that no radius changes are made
            // which are not drawn.
            // Without it, the radius calc thread could finish and set animationRunning to false
            // in between the canvas draw and the next iteration --> the last changes are lost which
            // would lead to an inconsistent cell-size.
            boolean oneMore = true;
            while (oneMore) {
                oneMore = animationRunning;

                if (cellSurface.getSurface().isValid()) {
                    Canvas canvas = cellSurface.lockCanvas();

                    // over-paint everything from previous frame
                    canvas.drawColor(context.getResources().getColor(R.color.game_board_background));
                    canvas.drawCircle(cellXCenter, cellYCenter, currentCellRadius, cellPaint);

                    cellSurface.unlockCanvasAndPost(canvas);
                } else {
                    Log.e("CellAnimation", "Cell surface invalid while attempting to draw");
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Log.w("CellAnimation", "Sleep in cell draw thread interrupted");
                    return;
                }
                framesDrawn++;
            }
        } catch (NullPointerException e) {
            // we need this in case the surface is destroyed during drawing
            Log.e("CellAnimation", "Surface destroyed while trying to draw the animation ");
            // end the thread because we cannot do anything anymore
            stopAnimation();
            return;
        }

        Log.d("CellAnimation", "" + framesDrawn + " frames during animation drawn.");
    }

    /**
     * This runnable' task is it to change the cell radius from zero - max or vise versa
     * in a given time with given number of steps.
     */
    private class CellRadiusCalc extends Thread {

        private CellRadiusCalcStrategy radiusCalcStrategy;

        private boolean abortCalc;

        private float startRadius;
        private float targetRadius;
        private int numberOfSteps;
        private int duration;

        CellRadiusCalc (CellRadiusCalcStrategy strategy, int duration, float startRadius, float targetRadius) {
            this(strategy, duration, duration / 2, startRadius, targetRadius);
        }

        CellRadiusCalc (CellRadiusCalcStrategy strategy, int duration, int numberOfSteps,
                        float startRadius, float targetRadius) {
            this.radiusCalcStrategy = strategy;
            this.duration = duration;
            this.numberOfSteps = numberOfSteps;
            this.targetRadius = targetRadius;
            this.startRadius = startRadius;

            abortCalc = false;
            animationRunning = true;
        }

        void abortCalculations() {
            abortCalc = true;
            interrupt();
        }

        @Override
        public void run() {
            int currentStep = 1;

            long start = System.currentTimeMillis();

            while (currentStep < numberOfSteps) {
                int remainingSteps = numberOfSteps - currentStep + 1;

                if (abortCalc) return;

                currentCellRadius = radiusCalcStrategy.calculateRadius(startRadius, targetRadius,
                        currentCellRadius, currentStep, numberOfSteps);

                if (abortCalc) return;
                try {
                    long remainingTime = Math.abs(System.currentTimeMillis() - start - duration);
                    float time = ((float) remainingTime) / remainingSteps;
                    Thread.sleep((int) time, ((int) (time * 1000000)) % 1000000);
                } catch (InterruptedException e) {
                    Log.w("CellAnimation", "Sleep in cell radius calculation thread interrupted");
                    return;
                }
                currentStep++;
            }

            long end = System.currentTimeMillis();

            if (end - start - duration > 10) {
                Log.w("CellAnimation", "Animation took longer than expected: " + (end-start) +
                        "ms. Expected " + duration);
            } else {
                Log.d("CellAnimation", "Animation duration: " + (end-start) +
                        "ms. Expected " + duration);
            }

            animationRunning = false;
        }
    }
}
