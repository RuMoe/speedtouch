package com.rumoe.speedtouch.gameboard.strategy.textview;

import android.app.Activity;

import com.rumoe.speedtouch.gameboard.CellEvent;
import com.rumoe.speedtouch.gameboard.CellType;

/**
 * Created by jan on 31.08.2014.
 */
public class SurvivalLifeUpdater extends GameLifeUpdater {

    private int lifeCount;
    private static final String LIFE_SYMBOL = "\u2665";

    public SurvivalLifeUpdater(Activity activity) {
        super(activity);
        // start the game with 3 lives;
        lifeCount = 3;
    }

    @Override
    void calculateNewLife(CellEvent event) {
        switch (event.getEventType()) {
            case TIMEOUT:
                if (!event.getCellType().equals(CellType.BAD)) {
                    decrementLife();
                }
                break;
            case TOUCHED:
                if (event.getCellType().equals(CellType.BAD)) {
                  decrementLife();
                }
                break;
            default:
                // do nothing otherwise
        }
    }

    private void decrementLife() {
       if (lifeCount > 0) {
            lifeCount--;
       }
    }

    @Override
    String getLifeAsString() {
            // seems a bit like an hack... repeat the life symbol lifeCount times
        return new String(new char[lifeCount]).replace("\0", LIFE_SYMBOL);
    }

    @Override
    public boolean isGameOver() {
        return lifeCount <= 0;
    }
}
