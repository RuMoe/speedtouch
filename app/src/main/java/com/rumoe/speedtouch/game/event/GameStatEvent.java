package com.rumoe.speedtouch.game.event;

import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;

/**
 * Created by jan on 05.09.2014.
 */
public class GameStatEvent extends GameEvent {

    private final CellPosition causingCell;
        // amount the game stat changed
    private final double quantity;

    public GameStatEvent(EventType type, CellPosition cause, double quantity) {
        super(type);
        if (type.isLifeCycleEvent()) {
            throw new IllegalArgumentException("Event of type " + type +
                    " is a lifecycle event");
        }
        causingCell = cause;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public CellPosition getCausingCell() {
        return causingCell;
    }
}
