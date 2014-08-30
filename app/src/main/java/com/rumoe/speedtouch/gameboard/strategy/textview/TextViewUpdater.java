package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.app.Activity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by jan on 30.08.2014.
 */
abstract class TextViewUpdater {

    private TextView textView;
    private Activity rootActivity;

    public TextViewUpdater(Activity rootActivity, TextView textView) {
        this.rootActivity = rootActivity;
        this.textView = textView;
    }

    void updateText(final String text) {
        // Only the original thread that created a view hierarchy can touch its views.
        rootActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
}
