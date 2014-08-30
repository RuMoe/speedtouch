package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.app.Activity;
import android.util.Log;

import com.rumoe.speedtouch.gameboard.CellEvent;

/**
 * Created by jan on 30.08.2014.
 */
public class SurvivalScoreUpdater extends GameScoreUpdater {

    // Holds the current score of the player during the game.
    private int score;

    public SurvivalScoreUpdater (Activity rootActivity) {
        super(rootActivity);
            // The game starts with a score of 0.
        score = 0;
    }

    @Override
    void calculateNewScore(CellEvent event) {
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
    }

    @Override
    String getScoreAsString() {
        return Integer.toString(score);
    }
}
