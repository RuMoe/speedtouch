package com.rumoe.speedtouch.game.mode.survival;

import android.util.Log;

import com.rumoe.speedtouch.game.mode.generic.GameThread;
import com.rumoe.speedtouch.game.ui.GameBoardFragment;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;


public class SurvivalGameThread extends GameThread {

    public SurvivalGameThread(final GameBoardFragment board) {
        super(board);
    }

    @Override
    protected void nextGameThreadCycle() {
        long currentTime = System.currentTimeMillis();
        if (activeCells < calculateMaxActiveCells() &&
                lastCellActivationTime + minDelayTime() < currentTime) {

            // choose a cell randomly
            CellPosition randomCell;
            do {
                int randomCellNr = (int) (Math.random() * rows * columns);
                int row = randomCellNr / columns;
                int column = randomCellNr % columns;

                randomCell = new CellPosition(row, column);
            } while (board.isCellActive(randomCell));

            // choose type
            CellType nextType = Math.random() > badCellPercentage()? CellType.STANDARD : CellType.BAD;

            // spawn cell
            board.activateCellLifeCycle(randomCell, nextType, growTime(), stayTime(), shrinkTime());
        }
    }

    /**
     * Calculates the maximum number of cells to be one the board based on the number of
     * already seen cells (which is a measure for the progression)
     * @return Maximum number of active cells
     */
    private int calculateMaxActiveCells() {
        // The returned value is x in cellsSeen = 10x(x+1)/2
        // Maximum 5
        int tmp = 0;
        for (int i = 1; i < 5; i++) {
            tmp += 10*i;
            if (tmp >= totalCellsActivated) {
                return i;
            }
        }
        return 5;
    }

    /**
     * Returns the number of milliseconds to wait in between two cell spawns based on the
     * number of already seen cells.
     * @return the number of milliseconds to wait in between two cell spawn.
     */
    private int minDelayTime() {
        if (totalCellsActivated > 500) return 0;
        return Math.max(0, (int) (1500 -
                1500*Math.pow(totalCellsActivated/500.0, 0.2)));
    }

    /**
     * Percentage of bad cells calculated based on the number of already seen cells. This is
     * a number between 0-1.
     * @return Percentage of bad cells.
     */
    private double badCellPercentage() {
        // progress from 0.01 - 0.1. increasing all 15 cells by 0.15
        return Math.min(0.01 + ((totalCellsActivated / 15) / 100.0), 0.15);
    }

    /**
     * Calculates how long the grow animation of the cell should take based on the number of
     * already seen cells.
     * @return Duration of cell grow animation in ms
     */
    private int growTime() {
        if (totalCellsActivated > 500) return 100;
        return  (int) (1000 - 900 * Math.log10(1 + totalCellsActivated/50.0));
    }

    /**
     * Calculates how long the cell stays at full size based on the number of
     * already seen cells.
     * @return Duration of cell staying at full size.
     */
    private int stayTime() {
        if (totalCellsActivated > 500) return 750;
        return (int) (3000 - 2250 * Math.log10(1 + totalCellsActivated/50.0));
    }

    /**
     * Calculates how long the shrink animation of the cell should take based on the number of
     * already seen cells.
     * @return Duration of cell shrink animation in ms
     */
    private int shrinkTime() {
        if (totalCellsActivated > 500) return 100;
        return  (int) (2000 - 1900 * Math.log10(1 + totalCellsActivated/50.0));
    }
}
