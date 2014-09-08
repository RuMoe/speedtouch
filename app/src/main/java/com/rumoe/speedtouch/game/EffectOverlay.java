package com.rumoe.speedtouch.game;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameLifecycleEvent;
import com.rumoe.speedtouch.game.event.GameObserver;

public class EffectOverlay extends RelativeLayout implements GameObserver {

    public EffectOverlay(Context context) {
        super(context);
        GameEventManager.getInstance().register(this);
    }

    public EffectOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        GameEventManager.getInstance().register(this);
    }

    public EffectOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        GameEventManager.getInstance().register(this);
    }

    private void executeCountdown() {
        final Activity rootActivity = (Activity) getContext();
        final TextView cdText = new TextView(rootActivity);

        new Thread() {
            public void run() {
                // add to overlay and apply styling
                rootActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        EffectOverlay.this.addView(cdText);
                        RelativeLayout.LayoutParams countdownLayout =
                                (RelativeLayout.LayoutParams) cdText.getLayoutParams();
                        countdownLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                        cdText.setTextAppearance(rootActivity, R.style.countDownLook);
                    }
                });

                // count down the numbers
                for (int countDown = 3; countDown > 0; countDown--) {
                    final int currentDigit = countDown;
                    rootActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            cdText.setText("" + currentDigit);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.w("EffectOverlay", "Count down unexpectedly interrupted");
                    }
                }

                // remove from overlay
                rootActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        cdText.setText("");
                        EffectOverlay.this.removeView(cdText);
                    }
                });

                // notify the game that the countdown is over
                GameEventManager.getInstance().notifyAll(new GameLifecycleEvent(GameEvent.EventType.COUNTDOWN_END));
            }
        }.start();
    }

    private void executeLifeLostEffect() {
        final int startColor = getResources().getColor(R.color.life_lost_flash);
        final int endColor   = getResources().getColor(R.color.life_lost_flash_end);

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            public void run() {
                ObjectAnimator bgColorAnimator =
                        ObjectAnimator.ofObject(EffectOverlay.this, "backgroundColor",
                                new ArgbEvaluator(), startColor, endColor);
                bgColorAnimator.setInterpolator(new DecelerateInterpolator());
                bgColorAnimator.setDuration(600);
                bgColorAnimator.start();
            }
        });
    }

    @Override
    public void notifyOnGameEvent(GameEvent e) {
        switch(e.getType()) {
            case COUNTDOWN_START:
                executeCountdown();
                break;
            case LIFE_LOST:
                executeLifeLostEffect();
                break;
        }
    }
}
