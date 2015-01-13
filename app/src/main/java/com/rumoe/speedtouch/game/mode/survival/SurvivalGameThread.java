package com.rumoe.speedtouch.game.mode.survival;

import com.rumoe.speedtouch.game.mode.generic.GameThread;
import com.rumoe.speedtouch.game.ui.GameBoardFragment;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

/**
 * Created by jan on 13.01.2015.
 */
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
        int tmp = 0;
        for (int i = 1; ; i++) {
            tmp += 10*i;
            if (tmp >= totalCellsActivated) {
                return i;
            }
        }
    }

    /**
     * Returns the number of milliseconds to wait in between two cell spawns based on the
     * number of already seen cells.
     * @return the number of milliseconds to wait in between two cell spawn.
     */
    private int minDelayTime() {
        return 0;
    }

    /**
     * Percentage of bad cells calculated based on the number of already seen cells. This is
     * a number between 0-1.
     * @return Percentage of bad cells.
     */
    private double badCellPercentage() {
        return 0.05;
    }

    /**
     * Calculates how long the grow animation of the cell should take based on the number of
     * already seen cells.
     * @return Duration of cell grow animation in ms
     */
    private int growTime() {
        return Cell.DEFAULT_GROW_ANIMATION_DURATION;
    }

    /**
     * Calculates how long the cell stays at full size based on the number of
     * already seen cells.
     * @return Duration of cell staying at full size.
     */
    private int stayTime() {
        return Cell.DEFAULT_WAIT_BEFORE_SHRINK_TIME;
    }

    /**
     * Calculates how long the shrink animation of the cell should take based on the number of
     * already seen cells.
     * @return Duration of cell shrink animation in ms
     */
    private int shrinkTime() {
        return Cell.DEFAULT_SHRINK_ANIMATION_DURATION;
    }
}
