package com.rumoe.speedtouch.game.gameboard;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.strategy.cellradius.BlinkStategy;
import com.rumoe.speedtouch.game.strategy.cellradius.CellRadiusCalcStrategy;
import com.rumoe.speedtouch.game.strategy.cellradius.ExponentialStrategy;
import com.rumoe.speedtouch.game.strategy.cellradius.LinearStrategy;

public class CellAnimation{

    private Context context;
    private CellType cellType;

    private SurfaceHolder cellSurface;
    private Paint cellPaint;

    private float currentCellRadius;
    private float minCellRadius;
    private float maxCellRadius;
    private float cellXCenter;
    private float cellYCenter;

    private Animation cellAnim;
    private boolean isAnimationRunning = false;

    public CellAnimation(SurfaceHolder surface, CellType cellType, Context context) {
        this.context = context;
        this.cellType = cellType;

        cellSurface = surface;

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

    public boolean setDefaultGrowAnimation(int duration) {
        return setAnimation(new LinearStrategy(), duration, currentCellRadius, maxCellRadius);
    }

    public boolean setDefaultShrinkAnimation(int duration) {
        return setAnimation(new ExponentialStrategy(), duration, currentCellRadius, minCellRadius);
    }

    public boolean setDefaultBlinkAnimation(int duration) {
        return setAnimation(new BlinkStategy(), duration, maxCellRadius, minCellRadius);
    }

    public boolean setAnimation(CellRadiusCalcStrategy strategy, int duration,
                                final float startSize, final float targetSize) {
        isAnimationRunning = true;

        cellAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // calculate new circle size
                currentCellRadius = startSize + interpolatedTime * (targetSize - startSize);
                drawCurrentCellState();
            }
        };
        cellAnim.setDuration(duration);
     //   cellAnim.setInterpolator(strategy);

        cellAnim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                isAnimationRunning = false;

                synchronized (cellAnim) {
                    cellAnim.notifyAll();
                }
            }

            // Do not need these
            public void onAnimationStart(Animation animation) {

            }
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return true;
    }

    private void drawCurrentCellState() {
        if (cellSurface != null && cellSurface.getSurface().isValid()) {
            Canvas canvas = cellSurface.lockCanvas();

            // over-paint everything from previous frame
            canvas.drawColor(context.getResources().getColor(R.color.game_board_background));
            canvas.drawCircle(cellXCenter, cellYCenter, currentCellRadius, cellPaint);

            cellSurface.unlockCanvasAndPost(canvas);
        } else {
            Log.e("CellAnimation", "Cell surface invalid while attempting to draw");
        }
    }

    /**
     * Clear the content of the cell and stop all animations.
     * @return true
     */
    public boolean clearCell() {
        currentCellRadius = 0;
        clearBackground();
        return true;
    }

    /**
     * Returns if the cell animation is currently running.
     * @return true iff animation is running, false otherwise.
     */
    public boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    /**
     * Prematurely ends the animation of the cell.
     */
    public void stopAnimation() {
        cellAnim.cancel();
    }

    /**
     * Locks the calling thread until the current animation has ended.
     * @return false if isAnimationRunning() returns false or an exception has
     *      occurred while locking, true otherwise.
     */
    public boolean waitUntilAnimationEnded() {
        if (!isAnimationRunning) return false;

        try {
            synchronized (cellAnim) {
                // as per java doc, there is an possibility of an spurious wakeup which wakes
                // up the thread without notify been called. I don't want hard to detect bugs.
                while (isAnimationRunning)
                    cellAnim.wait();
            }
        } catch (InterruptedException e) {
            return false;
        }
        return true;
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
}
