package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.app.Activity;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.gameboard.CellEvent;

/**
 * Created by jan on 30.08.2014.
 */
public abstract class GameScoreUpdater extends TextViewUpdater {

    private static final int SCORE_VIEW_ID = R.id.gameScoreView;

    GameScoreUpdater (Activity rootActivity) {
        super(rootActivity, (TextView) rootActivity.findViewById(SCORE_VIEW_ID));
        updateText(getScoreAsString());
    }

    public void updateScore(CellEvent event) {
        calculateNewScore(event);
        updateText(getScoreAsString());
    }

    abstract void calculateNewScore(CellEvent event);

    abstract String getScoreAsString();
}
