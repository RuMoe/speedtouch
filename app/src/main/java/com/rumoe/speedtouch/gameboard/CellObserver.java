package com.rumoe.speedtouch.gameboard;

//TODO think of useable method params
public interface CellObserver {

    void notifyOnActive(Cell c);

    void notifyOnTimeout(Cell c);

    void notifyOnTouch(Cell c);

    void notifyOnMissedTouch(Cell c);
}
