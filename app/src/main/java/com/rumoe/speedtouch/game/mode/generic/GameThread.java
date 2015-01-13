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

public abstract class GameThread implements Runnable, CellObserver, GameObserver {

    private Thread thread;

    /** Sleep time between to actions of the game thread in ms */
    private static final long CLOCK_RATE = 1000;
    private boolean gameOver;

    protected GameBoardFragment board;

    protected int rows;
    protected int columns;

    // The number of currently active cells.
    protected int activeCells;
    // The number of active cells seen during the whole game.
    protected int totalCellsActivated;
    // The time of the last activated cell;
    protected long lastCellActivationTime;

    public GameThread(final GameBoardFragment board) {
        GameEventManager.getInstance().register(this);
        gameOver = false;
        this.board = board;

        rows = board.getRowCount();
        columns = board.getColumnCount();

        activeCells = 0;
    }

    @Override
    public void run() {
        Log.d("GameThread", "GameThread run loop started");
        while (!thread.isInterrupted()) {
            nextGameThreadCycle();

            try {
                Thread.sleep(CLOCK_RATE);
            } catch (InterruptedException e) {
                Log.d("GameThread", "Sleep phase interrupted");
                break; // duh the interrupted state clears when the InterruptedException is thrown
            }
        }
        Log.d("GameThread", "GameThread run loop exited");
    }

    protected abstract void nextGameThreadCycle();

    public void gameOver() {
        GameEventManager.getInstance().unregister(this);
        Log.i("GameThread", "Game over");
        clearAndStop();
        gameOver = true;
    }

    private void gameStart() {
        // prevent starting multiple threads
        if (thread != null && thread.isAlive()) return;
        Log.i("GameThread", "Game thread started");
        thread = new Thread(this);
        thread.start();
    }

    private void gameContinue() {
        if (gameOver) return;
        Log.i("GameThread", "Game continue");
        gameStart();
    }

    private void clearAndStop() {
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
        totalCellsActivated++;
        lastCellActivationTime = System.currentTimeMillis();
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
                            gameContinue();
                        }
                    }
                }.start();
                break;
        }
    }
}
