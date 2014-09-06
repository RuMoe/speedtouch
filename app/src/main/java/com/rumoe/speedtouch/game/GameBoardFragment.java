package com.rumoe.speedtouch.game;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameLifecycleEvent;
import com.rumoe.speedtouch.game.event.GameObserver;
import com.rumoe.speedtouch.game.gameboard.Cell;
import com.rumoe.speedtouch.game.strategy.textview.GameLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.GameScoreUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalScoreUpdater;
import com.rumoe.speedtouch.menu.TempStart;

public class GameBoardFragment extends Fragment implements GameObserver {

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;

    private Cell[][] cells;
    private GameThread thread;
    private GameScoreUpdater scoreUpdater;
    private GameLifeUpdater lifeUpdater;

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

        GameEventManager.getInstance().register(this);

        //TODO for now scoreUpdater and lifeUpdater will be hardcoded.. change that at some point
        scoreUpdater = new SurvivalScoreUpdater(getActivity());
        lifeUpdater = new SurvivalLifeUpdater(getActivity());
        thread = new GameThread(cells);

        subscribeToCells(scoreUpdater, lifeUpdater, thread);

        //TODO temporary clusterfuck
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}

                GameEventManager.getInstance()
                    .notifyAll(new GameLifecycleEvent(GameEvent.EventType.GAME_START));
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribeToCells(scoreUpdater, lifeUpdater, thread);
        GameEventManager.getInstance().unregisterAll();
        thread.gameOver();
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

    private void unsubscribeToCells(CellObserver... obs) {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].removeObserver(o);
                }
            }
        }
    }

    @Override
    public void notifyOnGameEvent(GameEvent e) {
        if (e.getType().equals(GameEvent.EventType.GAME_OVER)) {
            transitionToMenu();
        }
    }

    private void transitionToMenu() {
        Intent intent = new Intent(this.getActivity(), TempStart.class);
        startActivity(intent);
    }
}