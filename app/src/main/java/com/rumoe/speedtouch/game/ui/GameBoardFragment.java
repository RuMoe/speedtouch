package com.rumoe.speedtouch.game.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

public class GameBoardFragment extends Fragment {

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;

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

        return rootView;
    }

    public void subscribeToCells(CellObserver... obs) {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].registerObserver(o);
                }
            }
        }
    }

    public void unsubscribeToCells(CellObserver... obs) {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].removeObserver(o);
                }
            }
        }
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
     * Gets the total amount of rows of the game board.
     * @return rows of the game board.
     */
    public int getRowCount() {
        return ROW_COUNT;
    }

    /**
     * Gets the total amount of columns of the game board.
     * @return columns of the game board.
     */
    public int getColumnCount() {
        return COLUMN_COUNT;
    }
}