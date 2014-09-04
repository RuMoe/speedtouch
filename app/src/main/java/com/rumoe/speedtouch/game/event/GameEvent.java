package com.rumoe.speedtouch.game.event;

import android.util.Log;

import com.rumoe.speedtouch.game.gameboard.CellPosition;

/**
 * Created by jan on 04.09.2014.
 */
public class GameEvent {

    public enum EventType {
        COUNTDOWN_START,
        GAME_START,
        SCORE_CHANGE,
        LIFE_CHANGE,
        GAME_OVER
    }

    private final CellPosition causingCell;
    private final EventType type;

    public GameEvent(EventType type, CellPosition causingCell) {
        this.causingCell = causingCell;
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public CellPosition getCausingCell() {
        return causingCell;
    }

    public boolean isLifecycleEvent() {
        switch (type) {
            case COUNTDOWN_START:
            case GAME_START:
            case GAME_OVER: return true;
            case SCORE_CHANGE:
            case LIFE_CHANGE: return false;
            default:
                // added this to make sure to not forget to update this method
                // in case of adding more event types
                Log.w("GameEvent", "Unknown EvenType as checking classification");
        }
        return false;
    }

    public boolean isStatEvent() {
        return !isLifecycleEvent();
    }
}

