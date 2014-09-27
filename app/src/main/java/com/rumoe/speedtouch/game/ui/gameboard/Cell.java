package com.rumoe.speedtouch.game.ui.gameboard;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.ui.gameboard.anim.BlinkInterpolator;

import java.util.ArrayList;

public class Cell {

    private static final Interpolator GROW_INTERPOLATOR     = new LinearInterpolator();
    private static final Interpolator SHRINK_INTERPOLATOR   = new AccelerateInterpolator(3.5f);
    private static final Interpolator BLINK_INTERPOLATOR    = new BlinkInterpolator();

    public static final int DEFAULT_WAIT_BEFORE_SHRINK_TIME     = 1000;
    public static final int DEFAULT_GROW_ANIMATION_DURATION     = 100;
    public static final int DEFAULT_SHRINK_ANIMATION_DURATION   = 2000;
    public static final int DEFAULT_BLINK_ANIMATION_DURATION    = 1000;

    private ArrayList<CellObserver> observer;

    private Thread          lifecycle;
    private Context         context;
    private ValueAnimator   animator;

    private CellType        type;
    private CellPosition    pos;

    private volatile float   radius;
    private long    activationTime;
    private long    timeoutTime;

    public Cell(Context context, CellPosition pos) {
        this.context = context;
        this.pos = pos;

        observer = new ArrayList<CellObserver>(5);
        radius = 0.0f;
        type = CellType.STANDARD;
    }

    /**
     * Tells if the cell is currently executing an animation or its lifecycle thread.
     * @return isAnimationRunning() || isLifecycleRunning()
     */
    public boolean isActive() {
        return isAnimationRunning() || isLifecycleRunning();
    }

    /**
     * Tells if the radius change animation is running.
     * @return true iff animation is being executed, false otherwise.
     */
    public boolean isAnimationRunning() {
        return animator != null && animator.isRunning();
    }

    /**
     * Tells if the cell lifecycle is currently running.
     * @return true iff lifecycle is being executed, false otherwise.
     */
    public boolean isLifecycleRunning() {
        return lifecycle != null && lifecycle.isAlive();
    }

    /**
     * Returns the current CellType which was set the last time the cell was activated.
     * @return cellType
     */
    public CellType getType() {
        return type;
    }

    /**
     * Returns the current radius of the cell which is a value between 0.0f and 1.0f.
     * The value 1.0f means that the cell is at its maximum value.
     * The value is changed whenever an animation is running.
     * @return radius of the cell.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Gets the timestamp (ms since epoch) of the last activation of the cell.
     * @return activation time
     */
    public long getActivationTime() {
        return activationTime;
    }

    /**
     * Gets the timestamp (ms since epoch) at which the current cell lifecycle will end, or
     * -1 if no lifecycle is active.
     * @return timeout time.
     */
    public long getTimeoutTime() {
        return isActive()? timeoutTime : -1;
    }

    /* ---------------------------------------------------------------------------------------------
                            CELL ACTIVATION AND DEACTIVATION
    --------------------------------------------------------------------------------------------- */

    /**
     * Let the cell blink 3 times for the default duration.
     * This does not count as an activation and thus observers won't be notified.
     * The cell is blocked for further animations until blinking is done.
     * @param type New type of the cell.
     * @return true on success, false otherwise.
     */
    public synchronized boolean blink(CellType type) {
        return blink(type, DEFAULT_BLINK_ANIMATION_DURATION);
    }

    /**
     * Let the cell blink 3 times in given time interval. This does not count as an activation and
     * thus observers won't be notified.
     * The cell is blocked for further animations until blinking is done.
     * @param type New type of the cell.
     * @return true on success, false otherwise.
     */
    public synchronized boolean blink(CellType type, int duration) {
        return setAnimation(type, BLINK_INTERPOLATOR, duration, 0.0f, 1.0f);
    }

    /**
     * Activates the cell lifecycle with its default timing.
     * This notifies all observer thru the notifyOnActive method.
     * Ends the lifecycle and the cell is still active, all observer
     * will be notified thru the notifyOnTimeout method.
     * @param type New type of the cell.
     * @return true on success, false otherwise.
     */
    public synchronized boolean activateLifecycle(CellType type) {
        return activateLifecycle(type, DEFAULT_GROW_ANIMATION_DURATION,
                DEFAULT_WAIT_BEFORE_SHRINK_TIME, DEFAULT_SHRINK_ANIMATION_DURATION);
    }

    /**
     * Activates the cell lifecycle. This notifies all observer thru the notifyOnActive method.
     * Ends the lifecycle and the cell is still active, all observer
     * will be notified thru the notifyOnTimeout method.
     * @param type New type of the cell.
     * @param growTime Time in ms the cell needs to reach its full radius.
     * @param constantTime Time in ms the cell radius will stay constant.
     * @param shrinkTime Time in ms the cell needs to reach a radius of 0.0f again.
     * @return true on success, false otherwise.
     */
    public synchronized boolean activateLifecycle(final CellType type, final int growTime,
                                     final int constantTime, final int shrinkTime) {
        if (isActive()) return false;
        notifyAllOnActive();
        activationTime = System.currentTimeMillis();
        timeoutTime = activationTime + growTime + constantTime + shrinkTime;

        lifecycle = new Thread() {
            @Override
            public void run() {
                cycle :
                {
                    setAnimation(type, GROW_INTERPOLATOR, growTime, 0.0f, 1.0f);
                    if (!waitUntilAnimationEnded()) break cycle;
                    try {
                        Thread.sleep(constantTime);
                    } catch (InterruptedException e) {
                        Log.d("Cell", "Lifecycle-Thread interrupted");
                        break cycle;
                    }
                    setAnimation(type, SHRINK_INTERPOLATOR, shrinkTime, 1.0f, 0.0f);
                    if (!waitUntilAnimationEnded()) break cycle;
                    notifyAllOnTimeout();
                }
                clear();
            }
        };
        lifecycle.start();

        return false;
    }

    /**
     * Stops the cell animation and sets its radius to 0.0f.
     * The CellType will stay the same.
     */
    private synchronized void clear() {
        if (lifecycle != null) lifecycle.interrupt();
        stopAnimation();
    }

    /**
     * Stops the cell animation and sets its radius to 0.0f.
     * The CellType will stay the same.
     * Calling this method will notify all observer thru the notifyOnKill method is the cell was
     * active.
     */
    public synchronized void deactivate() {
        if (!isActive()) return;

        clear();
        notifyAllOnKill();
    }

    /* ---------------------------------------------------------------------------------------------
                           ANIMATION OF THE RADIUS
    --------------------------------------------------------------------------------------------- */

    /**
     * Starts a new Animation which will change the cell radius depending of the following parameter.
     * @param newType Type of the cell it will have during the animation.
     * @param animInterpolator Interpolator for the animation timing.
     * @param duration Duration of the animation.
     * @param startSize The start radius of the cell.
     * @param targetSize The end radius of the cell.
     * @return true iff animation could be successfully started, false otherwise
     * (e.g. an animation was already running)
     */
    private synchronized boolean setAnimation(CellType newType, Interpolator animInterpolator, int duration,
                                 final float startSize, final float targetSize) {
        if (isAnimationRunning()) return false;

        type = newType;

        animator = ValueAnimator.ofFloat(startSize, targetSize);
        animator.setInterpolator(animInterpolator);
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (Float) animation.getAnimatedValue();
                Log.d("debug", pos + " radius changed to " + radius);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationEnd(Animator animation) {
                // notify all waiting threads that the animation has stopped
                Log.d("debug", "at " + pos + " onAnimationEnd notification");
                synchronized (Cell.this) {
                    Cell.this.notifyAll();
                }
            }
            // those are not necessary.
            public void onAnimationStart(Animator animation) {}
            public void onAnimationCancel(Animator animation) {}
            public void onAnimationRepeat(Animator animation) {}

        });

        // Animators may only be run on Looper threads
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                Log.d("debug", "at " + pos + " start animation");
                animator.start();
            }
         });

        return true;
    }

    /**
     * Stops the currently running animation. And sets its radius to 0.0f
     */
    private synchronized void stopAnimation() {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                Log.d("debug", "at " + pos + " stop animation");
                if (animator != null) animator.cancel();
                radius = 0.0f;
            }
        });
    }

    /**
     * Locks the calling thread until the animation has ended. Instantly returns true if no
     * animation is running.
     * @return true no animation is running or waited until animation completed, false if
     * waiting was interrupted.
     */
    private synchronized boolean waitUntilAnimationEnded() {
        Log.d("debug", "cell at " + pos + " enter wait method");

        Log.d("debug", "cell at " + pos + " waits for anim end");

        try {
            Cell.this.wait();
        } catch (InterruptedException e) {
             return false;
        }

        Log.d("debug", "cell at " + pos + " is done waiting");
        return true;
    }

    /* ---------------------------------------------------------------------------------------------
                            OBSERVER NOTIFICATION
    --------------------------------------------------------------------------------------------- */

    /**
     * Adds an CellObserver to the cell which will be notified on multiple events:
     * <ul>
     *     <li>Cell is activated</li>
     *     <li>Cell is deactivated through timeout</li>
     *     <li>Cell is deactivated through touch</li>
     *     <li>Cell registers ACTION_DOWN MotionEvent outside its active area</li>
     *     <li>Cell is deactivated through a system event (clear is called)</li>
     * </ul>
     * @param obs The CellObserver to be added.
     * @return true
     */
    public synchronized boolean registerObserver(CellObserver obs) {
        observer.add(obs);
        return true;
    }

    /**
     * Removes an CellObserver. Is the same observer registered multiple times then it will
     * be removed only once.
     * @param obs The CellObserver to be removed
     * @return true iff the CellObserver was registered and removed, false otherwise
     */
    public synchronized boolean removeObserver(CellObserver obs) {
        for (int i = observer.size() - 1; i >= 0; i--) {
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
        CellEvent event = CellEvent.generateActivatedEvent(pos, type, timeoutTime);

        Log.d("debug", "Cell at " + pos.toString() + " notifies active");
        for (CellObserver obs : observer) {
            obs.notifyOnActive(event);
        }
    }

    /**
     * Notify all observer that the cell is now inactive due to timeout.
     */
    private void notifyAllOnTimeout() {
        CellEvent event = CellEvent.generateTimeoutEvent(pos, type,
                timeoutTime - activationTime);

        Log.d("debug", "Cell at " + pos.toString() + " notifies timeout");
        for (CellObserver obs : observer) {
            obs.notifyOnTimeout(event);
        }
    }

    /**
     * Notify all observer that the cell is now inactive due to touch event.
     */
    private void notifyAllOnTouch() {
        long time = System.currentTimeMillis();
        CellEvent event = CellEvent.generateTouchedEvent(pos, type, time - activationTime,
                timeoutTime);

        for (CellObserver obs : observer) {
            obs.notifyOnTouch(event);
        }
    }

    /**
     * Notify all observer that there was a touch event which did not hit a
     * target.
     */
    private void notifyAllOnMissedTouch() {
        long time = System.currentTimeMillis();
        CellEvent event = CellEvent.generateMissedEvent(pos, type, time - activationTime,
                timeoutTime);

        for (CellObserver obs : observer) {
            obs.notifyOnMissedTouch(event);
        }
    }

    /**
     * Notify all observer that the cell was deactivated by clear() call.
     */
    private void notifyAllOnKill() {
        CellEvent event = CellEvent.generateKilledEvent(pos, type);

        for (CellObserver obs: observer) {
            obs.notifyOnKill(event);
        }
    }
}
