package com.rumoe.speedtouch.game;

import android.util.Log;

import com.rumoe.speedtouch.game.event.CellEvent;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.gameboard.Cell;
import com.rumoe.speedtouch.game.gameboard.CellType;
import com.rumoe.speedtouch.game.strategy.textview.GameLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.GameScoreUpdater;

// TODO stop gamethread if application is minimized
public class GameThread implements Runnable, CellObserver {

    private Thread thread;

    /** Sleep time between to actions of the game thread in ms */
    private static final long CLOCK_RATE = 1000;
    private boolean stopped;

    private final Cell[][] board;
    private final GameScoreUpdater scoreUpdater;
    private final GameLifeUpdater lifeUpdater;
    private final int rows;
    private final int columns;

    private int activeCells;

    public GameThread(final Cell[][] board, final GameScoreUpdater scoreUpdater,
                      final GameLifeUpdater lifeUpdater) {
        this.board = board;
        this.scoreUpdater = scoreUpdater;
        this.lifeUpdater = lifeUpdater;

        rows = board.length;
        columns = board[0].length;

        stopped = false;
        activeCells = 0;
    }

    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    public void resumeThread() {
        if (stopped) {
            stopped = false;
            startThread();
        }
    }

    public void stopThread() {
        stopped = true;
        thread.interrupt();
    }

    @Override
    public void run() {
        subscribeToCells();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {}

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

        unsubscribeToCells();
    }


    private void subscribeToCells() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                cell.registerObserver(this);
            }
        }
    }

    private void unsubscribeToCells() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                cell.removeObserver(this);
            }
        }
    }

    @Override
    public void notifyOnActive(CellEvent event) {
        activeCells++;
        scoreUpdater.updateScore(event);
        lifeUpdater.updateLife(event);

        checkGameOver();
    }

    @Override
    public void notifyOnTimeout(CellEvent event) {
        activeCells--;
        scoreUpdater.updateScore(event);
        lifeUpdater.updateLife(event);

        checkGameOver();
    }

    @Override
    public void notifyOnTouch(CellEvent event) {
        activeCells--;
        scoreUpdater.updateScore(event);
        lifeUpdater.updateLife(event);

        checkGameOver();
    }

    @Override
    public void notifyOnMissedTouch(CellEvent event) {
        scoreUpdater.updateScore(event);
        lifeUpdater.updateLife(event);

        checkGameOver();
    }

    private void checkGameOver() {
        if (lifeUpdater.isGameOver()) {
            initializeGameOver();
        }
    }

    private void initializeGameOver() {
        Log.i("GameThread", "Game Over!");
        for (Cell[] row : board) {
            for (Cell c : row) {
                c.clearCell();
            }
        }
        stopThread();
    }
}
