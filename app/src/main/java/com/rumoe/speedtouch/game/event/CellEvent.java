package com.rumoe.speedtouch.game.event;

import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

public class CellEvent {

    private final CellPosition cellPosition;
    private final EventType eventType;
    private final CellType cellType;
    private final long cellActivationTime;
    private final long eventTime;
    private final long cellDecayTime;

    public enum EventType {
        ACTIVATED, MISSED, TIMEOUT, TOUCHED, KILLED
    }

    public CellEvent(Cell cell, EventType type) {
        this.cellPosition = cell.getPosition();
        this.eventType = type;
        this.cellType = cell.getType();

        this.cellActivationTime = cell.getActivationTime();
        eventTime = System.currentTimeMillis();
        this.cellDecayTime = cell.getTimeoutTime();
    }

    /**
     * Returns the type of the event. See CellEvent.EventType.
     * @return event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Returns the type of the cell at the time the event was created.
     * @return Type of the cell.
     */
    public CellType getCellType() {
        return cellType;
    }

    /**
     * The position of the cell on the board the event was created for.
     * @return Position of the cell.
     */
    public CellPosition getCellPosition() {return cellPosition;}

    /**
     * The time in ms since epoch the cell was activated.
     * @return Activation time.
     */
    public long getCellActivationTime() {
        return cellActivationTime;
    }

    /**
     * The time in ms since epoch the cell will be deactivated because of timeout. Depending
     * on the event it is possible that the cell will by deactivated before this time (e.g. touch)
     * @return Deactivation time of the cell due to timeout.
     */
    public long getCelLDecayTime() {
        return cellDecayTime;
    }

    /**
     * This time in ms since epoch the event was generated.
     * @return Time of the event.
     */
    public long getEventTime() { return eventTime; }
}
