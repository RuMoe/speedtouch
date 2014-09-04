package com.rumoe.speedtouch.game.event;

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
}

