package com.rumoe.speedtouch.game.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

public class GameBoardFragment extends Fragment implements SurfaceHolder.Callback {

    private int boardWidth;
    private int boardHeight;

    private Thread boardDrawThread;

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;
    /** contains the cells of the board. Should never be accessed directly. Use getCell() instead. */
    private final Cell[][] cells;

    private SurfaceView gameBoard;

    public GameBoardFragment() {
        cells = new Cell[ROW_COUNT][COLUMN_COUNT];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_game, container, false);
        gameBoard       = (SurfaceView) rootView.findViewById(R.id.gameBoard);
        gameBoard.getHolder().addCallback(this);

        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (boardDrawThread != null && boardDrawThread.isAlive()){
            boardDrawThread.interrupt();
        }
        boardWidth = width;
        boardHeight = height;
        boardDrawThread = new BoardDrawThread();
        boardDrawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameBoard.getHolder().removeCallback(this);
        if (boardDrawThread != null) {
            boardDrawThread.interrupt();
        }
        clearAllCells();
    }

    public void subscribeToCells(CellObserver... obs) {
     /*   for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].registerObserver(o);
                }
            }
        }*/
    }

    public void unsubscribeToCells(CellObserver... obs) {
       /* for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].removeObserver(o);
                }
            }
        } */
    }

    /**
     * Can be used to determine if a cell is currently active.
     * @param pos The cell to be checked.
     * @return true iff the cell is active, false otherwise.
     */
    public boolean isCellActive(CellPosition pos) {
        // TODO
        return true;
    }

    /**
     * Activated the cell on the specified position. In contrast to the lifecycle method, the cell
     * we be visible indefinitely.
     * When calling this method successfully the cell will emit an CellEvent.ACTIVATED event.
     * @param pos The position of the cell which will be activated.
     * @param type Type the cell will have.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean activateCell(CellPosition pos, CellType type) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    /**
     * Starts the lifecycle of the cell on a specified position. A lifecycle contains three stages:
     * grow, constant size and shrink.
     * Calling this method will use the default timing of the lifecycle.
     * When calling this method successfully the cell will emit an CellEvent.ACTIVATED event.
     * @param pos The position of the cell which will be activated.
     * @param type Type the cell will have.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean activateCellLifeCycle(CellPosition pos, CellType type) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    /**
     * Gets the total amount of rows of the game board.
     * @return rows of the game board.
     */
    public int getRowCount() {
        return ROW_COUNT;
    }

    /**
     * Starts the lifecycle of the cell on a specified position. A lifecycle contains three stages:
     * grow, constant size and shrink.
     * When calling this method successfully the cell will emit an CellEvent.ACTIVATED event.
     * @param pos The position of the cell which will be activated.
     * @param type Type the cell will have.
     * @param growTime Time in ms for the grow phase of the lifecycle.
     * @param stayTime Time in ms for the constant size phase of the lifecycle.
     * @param shrinkTime Time in ms for the shrink phase of the lifecycle.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean activateCellLifeCycle(CellPosition pos, CellType type,
                                         int growTime, int stayTime, int shrinkTime) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    /**
     * Starts the default blink animation of the cell on the specified position.
     * @param pos The position of the cell which shall blink.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean blinkCell(CellPosition pos) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    /**
     * Clears the cell at the specified position. That means its state is set to deactivated and
     * all animations are stopped.
     * @param pos The position of the cell we want to clear.
     * @return true iff clear was successful, false otherwise.
     */
    public boolean clearCell(CellPosition pos) {
        // TODO
        return true;
    }

    /**
     * Returns the position of the center a cell on the board (which is its position on the
     * surface of its SurfaceView)
     *
     * @param pos CellPosition of the cell the coordinates are returned
     * @return A int array of length 2 containing the coordinates from top left corner
     *      int[0] -> x coordinate
     *      int[1] -> y coordinate
     */
    public int[] getCellCenterBoardPosition(CellPosition pos) {
        // TODO
        return new int[]{0, 0};
    }

    /**
     * Gets the total amount of columns of the game board.
     * @return columns of the game board.
     */
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    /**
     * Clears the whole game board and deactivates all cells.
     */
    private void clearAllCells() {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                CellPosition pos = new CellPosition(i, j);
                clearCell(pos);
            }
        }
    }

    /**
     * Internal wrapper to make sure not to mess up rows and columns when retrieving a cell.
     * @param pos Position of the cell which will be retrieved.
     * @return The cell object at the requested position or null if the position does not exist.
     */
    private Cell getCell(CellPosition pos) {
        return getCell(pos.getRow(), pos.getColumn());
    }

    /**
     * Internal wrapper to make sure not to mess up rows and columns when retrieving a cell.
     * @param row Row of the cell which will be retrieved.
     * @param column Column of the cell which will be retrieved.
     * @return The cell object at the requested position or null if the position does not exist.
     */
    private Cell getCell(int row, int column) {
        if (row < 0 || row >= getRowCount() ||
                column < 0 || column >= getColumnCount()) {
            Log.e("GameBoardFragment" , String.format("Requested cell position is out of bounds. " +
                            "The board has dimension %d,%d. Requested cell was %d, %d",
                    getRowCount(), getColumnCount(), row,column));
        }
        return cells[row][column];
    }

    class BoardDrawThread extends Thread {

        private static final int FPS = 40;
        private static final int MS_WAIT_PER_FRAME = 1000 / FPS;

        @Override
        public void run() {
            while(!isInterrupted()) {
                long refreshStart = System.currentTimeMillis();


                try {
                    long frameDelay = System.currentTimeMillis() - refreshStart;
                    Thread.sleep(Math.max(MS_WAIT_PER_FRAME - frameDelay, 0));
                } catch (InterruptedException e) {
                    Log.e("GameBoardFragment", "Draw thread interrupted");
                    break;
                }
            }
        }
    }
}