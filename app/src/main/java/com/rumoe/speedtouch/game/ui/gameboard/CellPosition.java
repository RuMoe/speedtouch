package com.rumoe.speedtouch.game.ui.gameboard;

/**
 * This class is a container to prevent messing up rows and columns when trying to access cells.
 * This should be used whenever handling cells of the board.
 * Row and column are starting with 0.
 */
public class CellPosition {

    private final int row;
    private final int column;

    public CellPosition(int row, int column) {this.row = row; this.column = column;}

    /**
     * @return row of the cell starting with 0.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return column of the cell starting with 0.
     */
    public int getColumn() {
        return column;
    }
}