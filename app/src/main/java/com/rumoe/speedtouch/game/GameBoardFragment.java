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
import com.rumoe.speedtouch.game.strategy.textview.GameLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.GameScoreUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalScoreUpdater;

public class GameBoardFragment extends Fragment{

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;

    private Cell[][] cells;
    private GameThread thread;

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

    @Override
    public void onResume() {
        super.onResume();

        //TODO for now scoreUpdater and lifeUpdater will be hardcoded.. change that at some point
        GameScoreUpdater scoreUpdater = new SurvivalScoreUpdater(getActivity());
        GameLifeUpdater lifeUpdater = new SurvivalLifeUpdater(getActivity());
        thread = new GameThread(cells);

        subscribeToCells(scoreUpdater, lifeUpdater, thread);
        thread.startThread();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thread.stopThread();
    }

    private void subscribeToCells(CellObserver... obs) {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].registerObserver(o);
                }
            }
        }
    }
}