package com.rumoe.speedtouch.game.event;

import com.rumoe.speedtouch.game.gameboard.CellPosition;
import com.rumoe.speedtouch.game.gameboard.CellType;

/**
 * Created by jan on 30.08.2014.
 */
public class CellEvent {

    private final CellPosition cellPosition;
    private final EventType eventType;
    private final CellType cellType;
    private final long delay;
    private final long timeUntilDecay;


    public enum EventType {
        ACTIVATED, MISSED, TIMEOUT, TOUCHED;
    }

    private CellEvent(CellPosition pos, EventType eventType, CellType cellType, long delay, long timeUntilDecay) {
        this.cellPosition = pos;
        this.eventType = eventType;
        this.cellType = cellType;
        this.delay = delay;
        this.timeUntilDecay = timeUntilDecay;
    }

    public static CellEvent generateTouchedEvent(CellPosition pos, CellType cellType, long delay, long timeUntilDecay) {
        return new CellEvent(pos, EventType.TOUCHED, cellType, delay, timeUntilDecay);
    }

    public static  CellEvent generateMissedEvent(CellPosition pos, CellType cellType, long delay, long timeUntilDecay) {
        return new CellEvent(pos, EventType.TOUCHED.MISSED, cellType, delay, timeUntilDecay);
    }

    public static CellEvent generateActivatedEvent(CellPosition pos, CellType cellType, long timeUntilDecay) {
        return new CellEvent(pos, EventType.ACTIVATED, cellType, 0L, timeUntilDecay);
    }

    public static CellEvent generateTimeoutEvent(CellPosition pos, CellType cellType, long delay) {
        return new CellEvent(pos, EventType.TIMEOUT, cellType, delay, 0L);
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

    public long getTimeUntilDecay() {
        return timeUntilDecay;
    }

    public CellPosition getCellPosition() {return cellPosition;}
}
