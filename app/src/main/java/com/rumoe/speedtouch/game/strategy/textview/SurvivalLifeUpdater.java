package com.rumoe.speedtouch.game.strategy.textview;

import android.app.Activity;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameLifecycleEvent;
import com.rumoe.speedtouch.game.event.GameStatEvent;
import com.rumoe.speedtouch.game.gameboard.CellPosition;
import com.rumoe.speedtouch.game.gameboard.CellType;

/**
 * Created by jan on 31.08.2014.
 */
public class SurvivalLifeUpdater extends GameLifeUpdater {

    private int lifeCount;

    private static final String LIFE_SYMBOL = "\u2665";
    private final GameEventManager gameEventManager;

    public SurvivalLifeUpdater(Activity activity) {
        super(activity);

        gameEventManager = GameEventManager.getInstance();
        lifeCount = 3;
        updateText(getLifeAsString());
    }

    @Override
    void calculateNewLife(CellEvent event) {
        switch (event.getEventType()) {
            case TIMEOUT:
                if (!event.getCellType().equals(CellType.BAD)) {
                    decrementLife(event.getCellPosition());
                }
                break;
            case TOUCHED:
                if (event.getCellType().equals(CellType.BAD)) {
                  decrementLife(event.getCellPosition());
                }
                break;
            default:
                // do nothing otherwise
        }
    }

    private void decrementLife(CellPosition cause) {
       if (lifeCount > 0) {
            lifeCount--;
            gameEventManager.notifyAll(new GameStatEvent(GameEvent.EventType.LIFE_LOST,
                    cause, -1));
       } else {
           gameEventManager.notifyAll(new GameLifecycleEvent(GameEvent.EventType.GAME_OVER));
       }
    }

    @Override
    String getLifeAsString() {
            // seems a bit like an hack... repeat the life symbol lifeCount times
        return new String(new char[lifeCount]).replace("\0", LIFE_SYMBOL);
    }
}
