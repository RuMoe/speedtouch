package com.rumoe.speedtouch.gameboard.thread;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.gameboard.strategy.cellradius.CellRadiusCalcStrategy;
import com.rumoe.speedtouch.gameboard.strategy.cellradius.LinearGrowthStrategy;

// TODO add event to tell cell that lifecycle ended
// TODO handle onpause and onresume
public class CellAnimation implements Runnable {

    private static final int DEFAULT_GROW_ANIMATION_DURATION    = 1000;
    private static final int DEFAULT_CONSTANT_SIZE_DURATION     = 1000;
    private static final int DEFAULT_SHRINK_ANIMATION_DURATION  = 1000;

    private Context context;

    private SurfaceHolder cellSurface;
    private final Paint cellPaint;
    private int backgroundColor;
    private int shadowColor;

    private float currentCellRadius;
    private float minCelLRadius;
    private float maxCellRadius;
    private float cellXCenter;
    private float cellYCenter;

    private Thread drawThread;
    private Thread lifecycleThread;
    private CellRadiusCalc calculationThread;
    private boolean animationRunning;

    public CellAnimation(SurfaceHolder surface, Context context) {
        this.context = context;

        cellSurface = surface;
        animationRunning = false;

        backgroundColor = context.getResources().getColor(R.color.game_board_background);
        shadowColor     = context.getResources().getColor(R.color.cell_shadow);

        cellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellPaint.setColor(context.getResources().getColor(R.color.cell_standard));
        cellPaint.setShadowLayer(15.0f, 0.0f, 0.0f, shadowColor);
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
        minCelLRadius = 0;

        cellXCenter = cellWidth / 2.0f;
        cellYCenter = cellHeight / 2.0f;
    }

    /**
     * Fills the cell with its background color.
     */
    public void clearBackground() {
        if (cellSurface.getSurface().isValid()) {
            Canvas canvas = cellSurface.lockCanvas();
            canvas.drawColor(backgroundColor);
            cellSurface.unlockCanvasAndPost(canvas);
        } else {
            Log.e("CellAnimation", "Surface invalid while clearing background");
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

    public boolean startLifecycle() {
        return startLifecycle(DEFAULT_GROW_ANIMATION_DURATION,
                DEFAULT_CONSTANT_SIZE_DURATION,
                DEFAULT_SHRINK_ANIMATION_DURATION);
    }

    public boolean startLifecycle(final int growTime, final int constantTime,final int shrinkTime) {
        if ((lifecycleThread != null && lifecycleThread.isAlive()) ||
                (calculationThread != null && calculationThread.isAlive())) return false;

        lifecycleThread = new Thread() {
            public void run() {
                growAnimation(growTime);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Log.w("CellAnimation", "Lifecycle thread interrupted");
                    return;
                }

                shrinkAnimation(shrinkTime);
            }
        };
        lifecycleThread.start();

        return true;
    }

    public boolean growAnimation() {
        return growAnimation(DEFAULT_GROW_ANIMATION_DURATION);
    }

    public boolean growAnimation(int duration) {
        return growAnimation(new LinearGrowthStrategy(), duration);
    }

    public boolean growAnimation(CellRadiusCalcStrategy strategy, int duration) {
       if (calculationThread != null && calculationThread.isAlive()) return false;

        calculationThread = new CellRadiusCalc(strategy, duration, maxCellRadius);
        calculationThread.start();

        drawThread = new Thread(this);
        drawThread.start();

        return true;
    }

    public boolean shrinkAnimation() {
        return shrinkAnimation(DEFAULT_SHRINK_ANIMATION_DURATION);
    }

    public boolean shrinkAnimation(int duration) {
        return shrinkAnimation(new LinearGrowthStrategy(), duration);
    }

    public boolean shrinkAnimation(CellRadiusCalcStrategy strategy, int duration) {
        if (calculationThread != null && calculationThread.isAlive()) return false;

        calculationThread = new CellRadiusCalc(strategy, duration, minCelLRadius);
        calculationThread.start();

        drawThread = new Thread(this);
        drawThread.start();

        return true;
    }

    /**
     * Clear the content of the cell and stop all animations.
     * @return true
     */
    public boolean clearCell() {
        if (lifecycleThread != null)
            lifecycleThread.interrupt();

        if (calculationThread != null)
            calculationThread.abortCalculations();

        animationRunning = false;

        currentCellRadius = 0;
        clearBackground();
        return true;
    }

    @Override
    /**
     * As long as the thread for recalculation of the cell radius works, update
     * the cell for the user in regular intervals.
     */
    public void run() {
        int framesDrawn = 0;
        while (animationRunning) {

            if (cellSurface.getSurface().isValid()) {
                Canvas canvas = cellSurface.lockCanvas();

                // over-paint everything from previous frame
                canvas.drawColor(backgroundColor);
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
        Log.d("CellAnimation", "" + framesDrawn + " frames during animation drawn.");
    }

    /**
     * This runnable' task is it to change the cell radius from zero - max or vise versa
     * in a given time with given number of steps.
     */
    private class CellRadiusCalc extends Thread {

        private CellRadiusCalcStrategy radiusCalcStrategy;

        private boolean abortCalc;

        private float targetRadius;
        private int numberOfSteps;
        private int duration;

        CellRadiusCalc (CellRadiusCalcStrategy strategy, int duration, float targetRadius) {
            this(strategy, duration, duration / 2, targetRadius);
        }

        CellRadiusCalc (CellRadiusCalcStrategy strategy, int duration, int numberOfSteps,
                        float targetRadius) {
            this.radiusCalcStrategy = strategy;
            this.duration = duration;
            this.numberOfSteps = numberOfSteps;
            this.targetRadius = targetRadius;

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

                currentCellRadius = radiusCalcStrategy.calculateRadius(targetRadius,
                        currentCellRadius, currentStep, remainingSteps);

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
