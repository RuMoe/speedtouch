package com.rumoe.speedtouch.game.ui.gameboard;

/**
 * Created by jan on 04.09.2014.
 */
public class CellPosition {

    private final int x;
    private final int y;

    public CellPosition(int x, int y) {this.x = x; this.y = y;}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}