package com.rumoe.speedtouch.game.event;

import com.rumoe.speedtouch.game.gameboard.CellPosition;

/**
 * Created by jan on 05.09.2014.
 */
public class GameStatEvent extends GameEvent {

    private final CellPosition causingCell;

    public GameStatEvent(EventType type, CellPosition cause) {
        super(type);
        if (type.isLifeCycleEvent()) {
            throw new IllegalArgumentException("Event of type " + type +
                    " is a lifecycle event");
        }
        causingCell = cause;
    }

    public CellPosition getCausingCell() {
        return causingCell;
    }
}
