package com.rumoe.speedtouch.gameboard;

public interface CellObserver {

    void notifyOnActive(int xPos, int yPos, CellType type);

    void notifyOnTimeout(int xPos, int yPos, CellType type);

    void notifyOnTouch(int xPos, int yPos, CellType type);

    void notifyOnMissedTouch(int xPos, int yPos, CellType type);
}
