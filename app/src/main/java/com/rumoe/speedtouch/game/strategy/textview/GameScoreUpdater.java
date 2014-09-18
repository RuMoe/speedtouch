package com.rumoe.speedtouch.game.strategy.textview;

import android.app.Activity;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellEvent;

/**
 * Created by jan on 30.08.2014.
 */
public abstract class GameScoreUpdater extends TextViewUpdater {

    private static final int SCORE_VIEW_ID = R.id.gameScoreView;

    GameScoreUpdater (Activity rootActivity) {
        super(rootActivity, (TextView) rootActivity.findViewById(SCORE_VIEW_ID));
    }

    public synchronized void updateScore(CellEvent event) {
        calculateNewScore(event);
        updateText(getScoreAsString());
    }

    public abstract int getScore();

    abstract void calculateNewScore(CellEvent event);

    abstract String getScoreAsString();

    @Override
    public void notifyOnTimeout(CellEvent event) {
        updateScore(event);
    }

    @Override
    public void notifyOnTouch(CellEvent event) {
        updateScore(event);
    }

    @Override
    public void notifyOnMissedTouch(CellEvent event) {
        updateScore(event);
    }
}
