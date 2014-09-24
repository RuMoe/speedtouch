package com.rumoe.speedtouch.game.event;

import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

public class CellEvent {

    private final CellPosition cellPosition;
    private final EventType eventType;
    private final CellType cellType;
    private final long delay;
    private final long decayTime;
    private final long eventTime;

    public enum EventType {
        ACTIVATED, MISSED, TIMEOUT, TOUCHED, KILLED
    }

    private CellEvent(CellPosition pos, EventType eventType, CellType cellType, long delay, long decayTime) {
        this.cellPosition = pos;
        this.eventType = eventType;
        this.cellType = cellType;
        this.delay = delay;
        this.decayTime = decayTime;
        eventTime = System.currentTimeMillis();
    }

    public static CellEvent generateTouchedEvent(CellPosition pos, CellType cellType, long delay, long decayTime) {
        return new CellEvent(pos, EventType.TOUCHED, cellType, delay, decayTime);
    }

    public static  CellEvent generateMissedEvent(CellPosition pos, CellType cellType, long delay, long decayTime) {
        return new CellEvent(pos, EventType.MISSED, cellType, delay, decayTime);
    }

    public static CellEvent generateKilledEvent(CellPosition pos, CellType cellType) {
        return new CellEvent(pos, EventType.KILLED, cellType, -1L, -1L);
    }

    public static CellEvent generateActivatedEvent(CellPosition pos, CellType cellType, long decayTime) {
        return new CellEvent(pos, EventType.ACTIVATED, cellType, 0L, decayTime);
    }

    public static CellEvent generateTimeoutEvent(CellPosition pos, CellType cellType, long delay) {
        return new CellEvent(pos, EventType.TIMEOUT, cellType, delay, System.currentTimeMillis());
    }

    public EventType getEventType() {
        return eventType;
    }

    public CellType getCellType() {
        return cellType;
    }

    public long getDelay() {
        return delay;
    }

    public long getEventTime() { return eventTime; }

    public long getDecayTime() {
        return decayTime;
    }

    public CellPosition getCellPosition() {return cellPosition;}
}
