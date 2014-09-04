package com.rumoe.speedtouch.game.event;

public interface CellObserver {

    void notifyOnActive(CellEvent event);

    void notifyOnTimeout(CellEvent event);

    void notifyOnTouch(CellEvent event);

    void notifyOnMissedTouch(CellEvent event);
}
