package com.rumoe.speedtouch.game;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.gameboard.Cell;

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
        TableLayout gameBoard   = (TableLayout) rootView.findViewById(R.id.gameBoard);

        float cellHeight    = 1.0f / ROW_COUNT;
        float cellWidth     = 1.0f / COLUMN_COUNT;

        for (int i = 0; i < ROW_COUNT; i++) {
            TableRow tr = new TableRow(gameBoard.getContext());
            gameBoard.addView(tr, new TableLayout.LayoutParams(0,0, cellHeight));

            for (int j = 0; j < COLUMN_COUNT; j++) {
                Cell cell = new Cell(tr.getContext(), j, i);
                tr.addView(cell, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, cellWidth));

                cells[i][j] = cell;
            }
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

    public Cell[][] getCells() {
        return cells;
    }
}