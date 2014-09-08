package com.rumoe.speedtouch.game;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameObserver;

public class EffectOverlay extends RelativeLayout implements GameObserver{

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
            }
        }.start();
    }

    @Override
    public void notifyOnGameEvent(GameEvent e) {
        switch(e.getType()) {
            case COUNTDOWN_START:
                executeCountdown();
                break;
        }
    }
}
