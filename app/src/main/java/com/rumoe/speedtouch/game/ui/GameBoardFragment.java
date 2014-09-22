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

    public boolean isCellActive(CellPosition pos) {
        // TODO
        return true;
    }

    public boolean activateCell(CellPosition pos, CellType type) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    public boolean activateCellLifeCycle(CellPosition pos, CellType type) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    public boolean activateCellLifeCycle(CellPosition pos, CellType type,
                                         int growTime, int stayTime, int shrinkTime) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    public boolean blinkCell(CellPosition pos) {
        if (isCellActive(pos)) return false;
        // TODO
        return true;
    }

    public boolean clearCell(CellPosition pos) {
        // TODO
        return true;
    }

    public int getRowCount() {
        return ROW_COUNT;
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }
}