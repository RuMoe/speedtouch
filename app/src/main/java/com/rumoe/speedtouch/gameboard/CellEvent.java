package com.rumoe.speedtouch.gameboard;

/**
 * Created by jan on 30.08.2014.
 */
public class CellEvent {

    private final CellPosition cellPosition;
    private final EventType eventType;
    private final CellType cellType;
    private final long delay;
    private final long timeUntilDecay;

    public class CellPosition {
        public final int x;
        public final int y;
        CellPosition(int x, int y) {this.x = x; this.y = y;}
    }

    public enum EventType {
        ACTIVATED, MISSED, TIMEOUT, TOUCHED;
    }

    private CellEvent(int x, int y, EventType eventType, CellType cellType, long delay, long timeUntilDecay) {
        this.cellPosition = new CellPosition(x, y);
        this.eventType = eventType;
        this.cellType = cellType;
        this.delay = delay;
        this.timeUntilDecay = timeUntilDecay;
    }

    public static CellEvent generateTouchedEvent(int x, int y, CellType cellType, long delay, long timeUntilDecay) {
        return new CellEvent(x, y, EventType.TOUCHED, cellType, delay, timeUntilDecay);
    }

    public static  CellEvent generateMissedEvent(int x, int y, CellType cellType, long delay, long timeUntilDecay) {
        return new CellEvent(x, y, EventType.TOUCHED.MISSED, cellType, delay, timeUntilDecay);
    }

    public static CellEvent generateActivatedEvent(int x, int y, CellType cellType, long timeUntilDecay) {
        return new CellEvent(x, y, EventType.ACTIVATED, cellType, 0L, timeUntilDecay);
    }

    public static CellEvent generateTimeoutEvent(int x, int y, CellType cellType, long delay) {
        return new CellEvent(x, y, EventType.TIMEOUT, cellType, delay, 0L);
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
