package com.rumoe.speedtouch.gameboard;

public interface CellObserver {

    void notifyOnActive(CellEvent event);

    void notifyOnTimeout(CellEvent event);

    void notifyOnTouch(CellEvent event);

    void notifyOnMissedTouch(CellEvent event);
}
