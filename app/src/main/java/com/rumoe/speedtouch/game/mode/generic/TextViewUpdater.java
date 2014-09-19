package com.rumoe.speedtouch.game.mode.generic;

import android.app.Activity;
import android.widget.TextView;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.CellObserver;

/**
 * Created by jan on 30.08.2014.
 */
abstract class TextViewUpdater implements CellObserver {

    private TextView textView;
    private Activity rootActivity;

    protected TextViewUpdater(Activity rootActivity, TextView textView) {
        this.rootActivity = rootActivity;
        this.textView = textView;
    }

    protected void updateText(final String text) {
        // Only the original thread that created a view hierarchy can touch its views.
        rootActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    // per default do nothing
    public void notifyOnActive(CellEvent event) {};

    public void notifyOnTimeout(CellEvent event) {};

    public void notifyOnTouch(CellEvent event) {};

    public void notifyOnMissedTouch(CellEvent event) {};
}
