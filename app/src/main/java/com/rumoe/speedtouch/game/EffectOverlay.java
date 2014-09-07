package com.rumoe.speedtouch.game;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        final TextView textView = new TextView(this.getContext());
        final Activity rootActivity = (Activity) getContext();

        new Thread() {
            public void run() {
                rootActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        EffectOverlay.this.addView(textView);
                    }
                });

                for (int countDown = 3; countDown > 0; countDown--) {
                    final int current = countDown;      // really?
                    rootActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            textView.setText("" + current);
                        }
                     });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        Log.w("EffectOverlay", "Countdown interrupted");
                        break;
                    }
                }

                rootActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        EffectOverlay.this.removeView(textView);
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
