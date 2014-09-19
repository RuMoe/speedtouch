package com.rumoe.speedtouch.game.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;

public class GameBoardFragment extends Fragment {

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;

    private final Cell[][] cells;

    public GameBoardFragment() {
        cells = new Cell[ROW_COUNT][COLUMN_COUNT];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_game, container, false);
        final TableLayout gameBoard   = (TableLayout) rootView.findViewById(R.id.gameBoard);

        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                cells[i][j] = new Cell(getActivity(), j, i);
            }
        }

        final float cellHeight = 1.0f / ROW_COUNT;
        final float cellWidth = 1.0f / COLUMN_COUNT;

        for (int i = 0; i < ROW_COUNT; i++) {
            final TableRow tr = new TableRow(gameBoard.getContext());
            for (int j = 0; j < COLUMN_COUNT; j++) {
                tr.addView(cells[i][j], new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, cellWidth));
            }
            gameBoard.addView(tr, new TableLayout.LayoutParams(0, 0, cellHeight));
        }

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

    public Cell getCell(CellPosition pos) {
        return getCell(pos.getY(), pos.getX());
    }

    public Cell getCell(int row, int column) {
        return cells[row][column];
    }

    public int getRowCount() {
        return ROW_COUNT;
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }
}