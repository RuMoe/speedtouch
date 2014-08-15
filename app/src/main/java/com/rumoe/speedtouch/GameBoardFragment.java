package com.rumoe.speedtouch;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameBoardFragment extends Fragment {

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;

    private View[][] cells;

    public GameBoardFragment() {
        cells = new View[ROW_COUNT][COLUMN_COUNT];
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
                TextView tv = new TextView(tr.getContext());
                tv.setText("test" + (i + j) + " ");

                tr.addView(tv, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, cellWidth));

                cells[i][j] = tv;
            }
        }

        return rootView;
    }
}