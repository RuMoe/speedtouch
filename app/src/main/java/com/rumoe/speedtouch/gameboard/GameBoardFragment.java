package com.rumoe.speedtouch.gameboard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.gameboard.Cell;
import com.rumoe.speedtouch.gameboard.CellObserver;

public class GameBoardFragment extends Fragment implements CellObserver{

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;

    private Cell[][] cells;
    private int activeCells;

    public GameBoardFragment() {
        cells = new Cell[ROW_COUNT][COLUMN_COUNT];
        activeCells = 0;
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
                Cell cell = new Cell(tr.getContext());
                tr.addView(cell, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, cellWidth));

                cell.registerObserver(this);
                cells[i][j] = cell;
            }
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (Cell[] row : cells) {
            for (Cell c : row) {
                c.removeObserver(this);
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