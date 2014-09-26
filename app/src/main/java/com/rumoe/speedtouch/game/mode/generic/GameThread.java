package com.rumoe.speedtouch.game.mode.generic;

import android.util.Log;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameObserver;
import com.rumoe.speedtouch.game.event.GameStatEvent;
import com.rumoe.speedtouch.game.ui.GameBoardFragment;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

public class GameThread implements Runnable, CellObserver, GameObserver {

    private Thread thread;

    /** Sleep time between to actions of the game thread in ms */
    private static final long CLOCK_RATE = 1000;
    private boolean stopped;

    private final GameBoardFragment board;

    private final int rows;
    private final int columns;

    private int activeCells;

    public GameThread(final GameBoardFragment board) {
        GameEventManager.getInstance().register(this);
        this.board = board;

        rows = board.getRowCount();
        columns = board.getColumnCount();

        activeCells = 0;
    }

    @Override
    public void run() {
        while (!stopped) {

            if (activeCells < 5) {
                CellPosition randomCell;
                do {
                    int randomCellNr = (int) (Math.random() * rows * columns);
                    int row = randomCellNr / columns;
                    int column = randomCellNr % columns;

                        randomCell = new CellPosition(row, column);
                }while(board.isCellActive(randomCell));

                CellType nextType = CellType.STANDARD;
                if (Math.random() < 0.05) nextType = CellType.BAD;
                board.activateCellLifeCycle(randomCell, nextType);
            }
            Log.d("thread", "thread is running");
            try {
                Thread.sleep(CLOCK_RATE);
            } catch (InterruptedException e) {
                Log.w("GameThread", "Sleep phase interrupted");
            }
        }
    }

    public void gameOver() {
        GameEventManager.getInstance().unregister(this);
        clearAndStop();
    }

    private void gameStart() {
        Log.d("thread", "thread is started");
        thread = new Thread(this);
        stopped = false;
        thread.start();
    }

    private void clearAndStop() {
        Log.d("thread", "thread is stopped");
        stopped = true;
        if (thread != null) thread.interrupt();
        clearAlLCells();
    }

    private void clearAlLCells() {
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int column = 0; column < board.getColumnCount(); column++) {
                CellPosition pos = new CellPosition(row, column);
                board.clearCell(pos);
            }
        }
    }

    @Override
    public void notifyOnActive(CellEvent event) {
        activeCells++;
    }

    @Override
    public void notifyOnTimeout(CellEvent event) {
        activeCells--;
    }

    @Override
    public void notifyOnTouch(CellEvent event) {
        activeCells--;
    }

    @Override
    public void notifyOnKill(CellEvent event) {activeCells--;}

    @Override
    public void notifyOnMissedTouch(CellEvent event) {}

    @Override
    public void notifyOnGameEvent(final GameEvent event) {
        switch (event.getType()) {
            case GAME_OVER:
                gameOver();
                break;
            case GAME_START:
                gameStart();
                break;
            case LIFE_LOST:
                // TODO temporary ... remove when real game threads implemented
                new Thread(){
                    public void run() {
                        clearAndStop();
                        CellPosition cp = ((GameStatEvent) event).getCausingCell();
                        board.blinkCell(cp);

                        try {
                            Thread.sleep(Cell.DEFAULT_BLINK_ANIMATION_DURATION + 500);
                        } catch (InterruptedException e) {

                        } finally {
                            gameStart();
                        }
                    }
                }.start();
                break;
        }
    }
}
