package com.rumoe.speedtouch.game.mode.survival;

import com.rumoe.speedtouch.game.mode.generic.GameThread;
import com.rumoe.speedtouch.game.ui.GameBoardFragment;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

/**
 * Created by jan on 13.01.2015.
 */
public class SurvivalGameThread extends GameThread {

    public SurvivalGameThread(final GameBoardFragment board) {
        super(board);
    }

    protected void nextGameThreadCycle() {
        if (activeCells < 5) {
            CellPosition randomCell;
            do {
                int randomCellNr = (int) (Math.random() * rows * columns);
                int row = randomCellNr / columns;
                int column = randomCellNr % columns;

                randomCell = new CellPosition(row, column);
            } while (board.isCellActive(randomCell));

            CellType nextType = CellType.STANDARD;
            if (Math.random() < 0.05) nextType = CellType.BAD;
            board.activateCellLifeCycle(randomCell, nextType);
        }
    }
}
