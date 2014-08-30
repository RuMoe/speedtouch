package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.app.Activity;
import android.graphics.AvoidXfermode;
import android.util.Log;
import android.widget.TextView;

import com.rumoe.speedtouch.gameboard.CellEvent;
import com.rumoe.speedtouch.gameboard.CellType;

/**
 * Created by jan on 30.08.2014.
 */
public class SurvivalScoreUpdater extends GameScoreUpdater {

    private int score;

    public SurvivalScoreUpdater (Activity rootActivity) {
        super(rootActivity);
        score = 0;
    }

    @Override
    String generateScore(CellEvent event) {
        if (event.getEventType().equals(CellEvent.EventType.TOUCHED)) {
            switch (event.getCellType()) {
                case BAD:
                    score--;
                    break;
                case STANDARD:
                    score++;
                    break;
                default:
                    Log.d("SurvivalScoreUpdater", "Unknown CellType touched");
            }
        }

        return Integer.toString(score);
    }
}
