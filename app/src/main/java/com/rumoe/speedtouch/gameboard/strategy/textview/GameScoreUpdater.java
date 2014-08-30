package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.app.Activity;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.gameboard.CellEvent;
import com.rumoe.speedtouch.gameboard.CellType;

/**
 * Created by jan on 30.08.2014.
 */
public abstract class GameScoreUpdater extends TextViewUpdater {

    private static final int SCORE_VIEW_ID = R.id.gameScoreView;

    public GameScoreUpdater (Activity rootActivity) {
        super(rootActivity, (TextView) rootActivity.findViewById(SCORE_VIEW_ID));
    }

    public void updateScore(CellEvent event) {
        updateText(generateScore(event));
    }

    abstract String generateScore(CellEvent event);
}
