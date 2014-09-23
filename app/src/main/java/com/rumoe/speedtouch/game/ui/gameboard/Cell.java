package com.rumoe.speedtouch.game.ui.gameboard;

import android.graphics.Paint;

public class Cell {

    public static final int DEFAULT_WAIT_BEFORE_SHRINK_TIME     = 1000;
    public static final int DEFAULT_GROW_ANIMATION_DURATION     = 100;
    public static final int DEFAULT_SHRINK_ANIMATION_DURATION   = 2000;
    public static final int DEFAULT_BLINK_ANIMATION_DURATION    = 1000;

    private Thread lifecycle;

    private CellType type;
    private boolean active;

    private long cellActivatedTime;
    private long cellTimeoutTime;

    public Cell() {
        type = CellType.STANDARD;
        active = false;
    }

    /**
     * Tells if the cell is currently executing an animation.
     * @return true iff an animation is executed, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the current CellType which was set the last time the cell was activated.
     * @return cellType
     */
    public CellType getType() {
        return type;
    }

    /**
     * Returns the Paint of the cell which decides is lock. The object which
     * is returned depends on the CellType passed last time the cell was activated.
     * @return Paint of the cell.
     */
    public Paint getPaint() {
        // TODO
        return null;
    }

    /**
     * Returns the current radius of the cell which is a value between 0.0f and 1.0f.
     * The value 1.0f means that the cell is at its maximum value.
     * The value is changed whenever an animation is running.
     * @return radius of the cell.
     */
    public float getRadius() {
        // TODO
        return 0.0f;
    }

    /**
     * Gets the timestamp (ms since epoch) of the last activation of the cell.
     * @return activation time
     */
    public long getActivationTime() {
        // TODO
        return 0;
    }

    /**
     * Gets the timestamp (ms since epoch) at which the current cell lifecycle will end, or
     * -1 if no lifecycle is active.
     * @return timeout time.
     */
    public long getTimeoutTime() {
        // TODO
        return 0;
    }

    /* ---------------------------------------------------------------------------------------------
                            CELL ACTIVATION AND DEACTIVATION
    --------------------------------------------------------------------------------------------- */

    /**
     * Activates the cell without a timeout and its default grow time.
     * @param type New type of the cell.
     * @return true on success, false otherwise.
     */
    public boolean activate(CellType type) {
        // TODO
        return false;
    }

    /**
     * Activates the cell without a timeout.
     * @param type New type of the cell.
     * @param growTime Time in ms the cell needs to reach its full radius.
     * @return true on success, false otherwise.
     */
    public boolean activate(CellType type, int growTime) {
        // TODO
        return false;
    }

    /**
     * Activates the cell lifecycle with its default timing.
     * @param type New type of the cell.
     * @return true on success, false otherwise.
     */
    public boolean activateLifecycle(CellType type) {
        // TODO
        return false;
    }

    /**
     * Activates the cell lifecycle,
     * @param type New type of the cell.
     * @param growTime Time in ms the cell needs to reach its full radius.
     * @param constantTime Time in ms the cell radius will stay constant.
     * @param shrinkTime Time in ms the cell needs to reach a radius of 0.0f again.
     * @return true on success, false otherwise.
     */
    public boolean activateLifecycle(CellType type, int growTime, int constantTime, int shrinkTime) {
        // TODO
        return false;
    }

    /**
     * Stops the cell animation and sets its radius to 0.0f.
     * The CellType will stay the same.
     */
    public void clearCell() {
        // TODO
    }
}
