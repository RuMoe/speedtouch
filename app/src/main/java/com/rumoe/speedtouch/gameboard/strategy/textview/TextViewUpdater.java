package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by jan on 30.08.2014.
 */
abstract class TextViewUpdater {

    private TextView textView;

    public TextViewUpdater(TextView textView) {
        this.textView = textView;
    }

    void updateText(String text) {
        textView.setText(text);
    }
}
