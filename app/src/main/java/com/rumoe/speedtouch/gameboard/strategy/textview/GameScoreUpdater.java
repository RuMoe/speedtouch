package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.widget.TextView;

import com.rumoe.speedtouch.gameboard.CellEvent;
import com.rumoe.speedtouch.gameboard.CellType;

/**
 * Created by jan on 30.08.2014.
 */
public abstract class GameScoreUpdater extends TextViewUpdater {

    public GameScoreUpdater (TextView scoreTextView) {
        super(scoreTextView);
    }

    public void updateScore(CellEvent event) {
        updateText(generateScore(event));
    }

    abstract String generateScore(CellEvent event);
}
