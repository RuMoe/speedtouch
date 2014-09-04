package com.rumoe.speedtouch.game.gameboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.CellObserver;

import java.util.ArrayList;

public class Cell extends SurfaceView implements SurfaceHolder.Callback {

    public static final int DEFAULT_WAIT_BEFORE_SHRINK_TIME = 1000;
    public static final int DEFAULT_GROW_ANIMATION_DURATION    = 100;
    public static final int DEFAULT_SHRINK_ANIMATION_DURATION  = 2000;

    private CellAnimation animation;
    private Thread lifecycle;

    private CellType type;
    private int xPos;
    private int yPos;

    private ArrayList<CellObserver> observer;
    private boolean active;

    private long cellActivatedTime;
    private long cellTimeoutTime;

    public Cell(Context context, int xPos, int yPos) {
        super(context);

        type = CellType.STANDARD;
        this.xPos = xPos;
        this.yPos = yPos;

        active = false;
        animation = new CellAnimation(getHolder(), CellType.STANDARD, context);

        // a cell is part of one game board -> in most cases there is exactly one observer
        // (except multiplayer)
        observer = new ArrayList<CellObserver>(1);

        // registers the listener
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        animation.clearBackground();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("Cell", "surface destroyed");
        clearCell();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int cellPadding = (int) getResources().getDimension(R.dimen.board_cell_padding);
        animation.setDimensions(width, height, cellPadding);
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        super.onTouchEvent(e);

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (active) {
                if (animation.isTargetHit(e.getX(), e.getY())) {
                    deactivate();
                    animation.clearCell();

                    notifyAllOnTouch();
                } else {
                    notifyAllOnMissedTouch();
                }
            } else {
                notifyAllOnMissedTouch();
            }
        }
        return true;
    }

    /**
     * Deactivated the cells lifecycle and the cell animation.
     */
    private void deactivate() {
        if (lifecycle != null) lifecycle.interrupt();
        animation.stopAnimation();
        active = false;
    }

    public void clearCell() {
        deactivate();
        animation.clearCell();
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Checks if the cell is already active and a new action can be performed.
     * Also checks for consistency between cell state and animation state and tries
     * to fix any issues.
     * @return true iff activation is possible, false otherwise
     */
    private boolean checkActivatePossibility() {
        if (active || getHolder() == null || !getHolder().getSurface().isValid()) return false;

        if (animation.isAnimationRunning()) {
            Log.e("Cell", "Inconsistent cell state: Animation running but cell appears " +
                    "inactive");

            // try to kill every running thread to get an consistent state again
            deactivate();
            animation.clearCell();
        }
        return true;
    }

    public boolean activate(CellType type) {
        return activate(type, DEFAULT_GROW_ANIMATION_DURATION);
    }

    public boolean activate(CellType type, int growDuration) {
        if (!checkActivatePossibility()) return false;

        cellActivatedTime = System.currentTimeMillis();
        cellTimeoutTime = Long.MAX_VALUE;   // there is no timeout here

        this.type = type;
        animation.setCellType(type);
        animation.growAnimation(growDuration);
        active = true;
        notifyAllOnActive();

        return active;
    }

    public boolean activateLifecycle(CellType type) {
        return activateLifecycle(type,
                DEFAULT_GROW_ANIMATION_DURATION,
                DEFAULT_WAIT_BEFORE_SHRINK_TIME,
                DEFAULT_SHRINK_ANIMATION_DURATION);
    }

    public boolean activateLifecycle(CellType type, final int growDuration, final int waitDuration,
                                     final int shrinkDuration) {
        if (!checkActivatePossibility()) return false;

        cellActivatedTime = System.currentTimeMillis();
        cellTimeoutTime = cellActivatedTime +
                growDuration +
                waitDuration +
                shrinkDuration;

        this.type = type;
        animation.setCellType(type);
        active = true;
        notifyAllOnActive();

        lifecycle = new Thread() {
            @Override
            public void run() {
                animation.growAnimation(growDuration);
                if (!animation.waitUntilAnimationEnded()) return;
                try {
                    Thread.sleep(waitDuration);
                } catch (InterruptedException e) {
                    Log.d("Cell", "Lifecycle-Thread interrupted");
                    return;
                }
                animation.shrinkAnimation(shrinkDuration);
                if (!animation.waitUntilAnimationEnded()) return;

                // the cell is not visible anymore --> player didn't touch it in time
                notifyAllOnTimeout();
            }
        };
        startLifecycle();
        return true;
    }

    private void startLifecycle() {
        new Thread() {
            @Override
            public void run() {
                lifecycle.start();
                try {
                    lifecycle.join();
                } catch (InterruptedException e) {
                    // Does not matter why we don't wait anymore -
                    // deactivate the cell in any case.
                } finally {
                    deactivate();
                }
            }
        }.start();
    }

    /**
     * Adds an CellObserver to the cell which will be notified on multiple events:
     * <ul>
     *     <li>Cell is activated</li>
     *     <li>Cell is deactivated through timeout</li>
     *     <li>Cell is deactivated through touch</li>
     *     <li>Cell registers ACTION_DOWN MotionEvent outside its active area</li>
     * </ul>
     * @param obs The CellObserver to be added.
     * @return true
     */
    public boolean registerObserver(CellObserver obs) {
        observer.add(obs);
        return true;
    }

    /**
     * Removes an CellObserver. Is the same observer registered multiple times then it will
     * be removed only once.
     * @param obs The CellObserver to be removed
     * @return true iff the CellObserver was registered and removed, false otherwise
     */
    public boolean removeObserver(CellObserver obs) {
        for (int i = observer.size() - 1; i >= 0; i++) {
            if (obs.equals(observer.get(i))) {
                observer.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Notify all observer that the cell is now active.
     */
    private void notifyAllOnActive() {
        long time = System.currentTimeMillis();
        CellEvent event = CellEvent.generateActivatedEvent(xPos, yPos, type, time - cellTimeoutTime);

        for (CellObserver obs : observer) {
            obs.notifyOnActive(event);
        }
    }

    /**
     * Notify all observer that the cell is now inactive due to timeout.
     */
    private void notifyAllOnTimeout() {
        CellEvent event = CellEvent.generateTimeoutEvent(xPos, yPos, type,
                cellTimeoutTime - cellActivatedTime);

        for (CellObserver obs : observer) {
            obs.notifyOnTimeout(event);
        }
    }

    /**
     * Notify all observer that the cell is now inactive due to touch event.
     */
    private void notifyAllOnTouch() {
        long time = System.currentTimeMillis();
        CellEvent event = CellEvent.generateTouchedEvent(xPos, yPos, type, time - cellActivatedTime,
                cellTimeoutTime - time);

        for (CellObserver obs : observer) {
            obs.notifyOnTouch(event);
        }
    }

    /**
     * Notify all observer tht there was a touch event which did not hit a
     * target.
     */
    private void notifyAllOnMissedTouch() {
        long time = System.currentTimeMillis();
        CellEvent event = CellEvent.generateMissedEvent(xPos, yPos, type, time - cellActivatedTime,
                cellTimeoutTime - time);

        for (CellObserver obs : observer) {
            obs.notifyOnMissedTouch(event);
        }
    }
}
