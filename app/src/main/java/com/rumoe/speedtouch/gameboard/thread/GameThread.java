package com.rumoe.speedtouch.gameboard.thread;

import android.util.Log;

import com.rumoe.speedtouch.gameboard.Cell;
import com.rumoe.speedtouch.gameboard.CellObserver;

public class GameThread implements Runnable, CellObserver{

    private Thread thread;

    /** Sleep time between to actions of the game thread in ms */
    private static final long CLOCK_RATE = 400;
    private boolean stopped;

    private Cell[][] board;
    private int rows;
    private int columns;
    private int activeCells;

    public GameThread(Cell[][] board) {
        this.board = board;
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
    }

    @Override
    public void run() {
        subscribeToCells();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {}

        while (!stopped) {

            if (activeCells <= 3) {
                Cell randomCell;
                do {
                    int randomCellNr = (int) (Math.random() * rows * columns);
                    int row = randomCellNr / rows;
                    int column = randomCellNr % columns;
                    randomCell = board[row][column];
                }while(randomCell.isActive());

                randomCell.activate();
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
                cell.registerObserver(this);
            }
        }
    }

    @Override
    public void notifyOnActive(Cell c) {
        activeCells++;
    }

    @Override
    public void notifyOnTimeout(Cell c) {
        activeCells--;
    }

    @Override
    public void notifyOnTouch(Cell c) {
        activeCells--;
    }

    @Override
    public void notifyOnMissedTouch(Cell c) {}
}
