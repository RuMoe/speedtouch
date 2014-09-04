package com.rumoe.speedtouch.game;

import android.util.Log;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameObserver;
import com.rumoe.speedtouch.game.gameboard.Cell;
import com.rumoe.speedtouch.game.gameboard.CellType;

// TODO stop gamethread if application is minimized
public class GameThread implements Runnable, CellObserver, GameObserver {

    private Thread thread;

    /** Sleep time between to actions of the game thread in ms */
    private static final long CLOCK_RATE = 1000;
    private boolean stopped;

    private final Cell[][] board;

    private final int rows;
    private final int columns;

    private int activeCells;

    public GameThread(final Cell[][] board) {
        GameEventManager.getInstance().register(this);
        this.board = board;

        rows = board.length;
        columns = board[0].length;

        activeCells = 0;
    }

    @Override
    public void run() {
        while (!stopped) {

            if (activeCells < 5) {
                Cell randomCell;
                do {
                    int randomCellNr = (int) (Math.random() * rows * columns);
                    int row = randomCellNr / columns;
                    int column = randomCellNr % columns;

                    randomCell = board[row][column];
                }while(randomCell.isActive());

                CellType nextType = CellType.STANDARD;
                if (Math.random() < 0.05) nextType = CellType.BAD;
                randomCell.activateLifecycle(nextType);
            }

            try {
                Thread.sleep(CLOCK_RATE);
            } catch (InterruptedException e) {
                Log.w("GameThread", "Sleep phase interrupted");
            }
        }
    }

    private void gameOver() {
        GameEventManager.getInstance().unregister(this);
        stopped = true;
        thread.interrupt();
    }

    private void gameStart() {
        thread = new Thread(this);
        stopped = false;
        thread.start();
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
    public void notifyOnMissedTouch(CellEvent event) {}

    @Override
    public void notifyOnGameEvent(GameEvent event) {
        switch (event.getType()) {
            case GAME_OVER:
                gameOver();
                break;
            case GAME_START:
                gameStart();
                break;
        }
    }
}
