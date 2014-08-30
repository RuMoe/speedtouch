package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.widget.TextView;

import com.rumoe.speedtouch.gameboard.CellEvent;

/**
 * Created by jan on 30.08.2014.
 */
public class SurvivalScoreUpdater extends GameScoreUpdater {

    private int score;

    public SurvivalScoreUpdater (TextView scoreTextView) {
        super(scoreTextView);
        score = 0;
    }

    @Override
    String generateScore(CellEvent event) {
        switch (event.getEventType()) {
            case TIMEOUT:
                score--;
                break;
            case TOUCHED:
                score++;
            default:    // to nothing
                break;
        }

        return Integer.toString(score);
    }
}
