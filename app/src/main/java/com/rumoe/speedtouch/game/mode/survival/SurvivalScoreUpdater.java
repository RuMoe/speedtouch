package com.rumoe.speedtouch.game.mode.survival;

import android.app.Activity;
import android.util.Log;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameStatEvent;
import com.rumoe.speedtouch.game.mode.generic.GameScoreUpdater;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

/**
 * Created by jan on 30.08.2014.
 */
public class SurvivalScoreUpdater extends GameScoreUpdater {

        // Holds the current score of the player during the game.
    private int score;
       // as the game progresses, the multiplier will be increased
    private double currentMultiplier;

        // maximum possible score given when popping a cell
    private int     baseScore           = 100;
        // increment multiplier by this whenever a standard cell is popped
    private double  multiplierIncrement = 0.02;
        // reset multiplier to this value whenever an error is made
    private double  baseMultiplier      = 1.0;
        // percentage of popping a cell which will be used as punishment when popping a bad cell
    private double  badPenalty          = -0.5;

    public SurvivalScoreUpdater (Activity rootActivity) {
        super(rootActivity);
            // The game starts with a score of 0.
        score = 0;
        currentMultiplier = baseMultiplier;

        updateText(getScoreAsString());
    }

    public int getScore() {
        return score;
    }

    @Override
    void calculateNewScore(CellEvent event) {
        if (event.getEventType().equals(CellEvent.EventType.TOUCHED)) {
            /**
             * How fast the player touches the cell is important..
             * ratio timeToDecay / totalLifeTime is used multiplier for the baseScore
             */
            long totalTime = event.getDelay() + event.getTimeUntilDecay();
            int scoreGain = (int) (event.getTimeUntilDecay() * baseScore *
                    getEffectiveMultiplier() / totalTime );

            switch (event.getCellType()) {
                case BAD:
                    scoreGain *= badPenalty;
                        // don't go under 0
                    score = Math.max(scoreGain + score, 0);
                    break;
                case STANDARD:
                    score += scoreGain;
                    currentMultiplier += multiplierIncrement;
                    break;
                default:
                    Log.d("SurvivalScoreUpdater", "Unknown CellType touched");
            }
            GameEventManager.getInstance().notifyAll(
                    new GameStatEvent(
                            GameEvent.EventType.SCORE_CHANGE,
                            event.getCellPosition(),
                            scoreGain));
        }
        if (checkForMultiplierReset(event)) {
            currentMultiplier = baseMultiplier;
        }
    }

    @Override
    String getScoreAsString() {
        return String.format("%07d (x%.1f)",score, getEffectiveMultiplier());
    }

    private boolean checkForMultiplierReset(CellEvent event) {
        switch (event.getEventType()) {
            case TOUCHED:
                if (event.getCellType().equals(CellType.BAD))
                    return true;
                break;
            case MISSED:
                return true;
            case TIMEOUT:
                if (!event.getCellType().equals(CellType.BAD))
                    return true;
                break;
        }
        return false;
    }

    /**
     * Truncate the multiplier to on digit precision
     * @return multiplier truncated to one digit precision
     */
    private double getEffectiveMultiplier() {
        return Math.floor(currentMultiplier * 10) / 10;
    }
}
