package com.rumoe.speedtouch.game.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameLifecycleEvent;
import com.rumoe.speedtouch.game.event.GameObserver;
import com.rumoe.speedtouch.game.event.GameStatEvent;

public class EffectOverlay extends RelativeLayout implements GameObserver {

    private static final int LIFE_LOST_FLASH_DURATION = 500;

    private static final int SCORE_ANIM_DURATION = 600;
    private static final int SCORE_ANIM_DISTANCE = 50;

    public EffectOverlay(Context context) {
        super(context);
        checkValidContext(context);
        GameEventManager.getInstance().register(this);
    }

    public EffectOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkValidContext(context);
        GameEventManager.getInstance().register(this);
    }

    public EffectOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        checkValidContext(context);
        GameEventManager.getInstance().register(this);
    }

    private void checkValidContext(Context context) {
        if (!(context instanceof GameActivity)) {
            throw new ActivityNotFoundException("EffectOverlay needs instance of " +
                    "GameActivity as context");
        }
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
        final int endColor   = getResources().getColor(R.color.transparent);

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            public void run() {
                ObjectAnimator bgColorAnimator =
                        ObjectAnimator.ofObject(EffectOverlay.this, "backgroundColor",
                                new ArgbEvaluator(), startColor, endColor);
                bgColorAnimator.setInterpolator(new AccelerateInterpolator());
                bgColorAnimator.setDuration(LIFE_LOST_FLASH_DURATION);
                bgColorAnimator.start();
            }
        });
    }

    private void executeScoreAnimation(final int[] pos, int displayNumber) {
        final TextView scoreView = new TextView(getContext());

        // set look of animation
        scoreView.setText("" + displayNumber);
        if (displayNumber >= 0) {
            scoreView.setTextAppearance(getContext(), R.style.scoreAnimationText);
        } else {
            scoreView.setTextAppearance(getContext(), R.style.scoreAnimationTextNegative);
        }

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // positioning
                RelativeLayout.LayoutParams layoutParams =
                        (LayoutParams) generateDefaultLayoutParams();

                int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
                int heightMeasureSpec = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST);
                scoreView.measure(widthMeasureSpec, heightMeasureSpec);
                layoutParams.leftMargin = pos[0] - scoreView.getMeasuredWidth()  / 2;
                    // not divided by two because of padding
                layoutParams.topMargin  = pos[1] - scoreView.getMeasuredHeight();

                EffectOverlay.this.addView(scoreView, layoutParams);

                // animation
                final int startMargin   = layoutParams.topMargin;
                Animation fadeAnim = new Animation() {
                    @Override
                    protected  void applyTransformation(float interpolatedTime, Transformation t) {
                        LayoutParams lp = (LayoutParams) scoreView.getLayoutParams();
                        scoreView.setAlpha(1.0f - interpolatedTime);
                        lp.topMargin = startMargin - (int) (interpolatedTime * SCORE_ANIM_DISTANCE);
                        scoreView.setLayoutParams(lp);
                    }
                };
                fadeAnim.setDuration(SCORE_ANIM_DURATION);

                // remove view when finished
                fadeAnim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                        EffectOverlay.this.removeView(scoreView);
                    }
                    public void onAnimationStart(Animation animation) {}
                    public void onAnimationRepeat(Animation animation) {}
                });
                scoreView.startAnimation(fadeAnim);
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
            case SCORE_CHANGE:
                GameStatEvent gse = (GameStatEvent) e;
                int[] pos = ((GameActivity) getContext()).getCellCenterScreenPosition(gse.getCausingCell());
                executeScoreAnimation(pos, (int) gse.getQuantity());
                break;
        }
    }
}
