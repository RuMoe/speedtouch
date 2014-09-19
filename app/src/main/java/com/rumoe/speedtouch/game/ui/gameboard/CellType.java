package com.rumoe.speedtouch.game.ui.gameboard;

import android.content.Context;
import android.util.Log;

import com.rumoe.speedtouch.R;

public enum CellType {
    STANDARD,
    BAD;

    public static int getCellColor(CellType type, Context context) {
        switch (type) {
            case STANDARD:
                return context.getResources().getColor(R.color.cell_standard);
            case BAD:
                return context.getResources().getColor(R.color.cell_bad);
            default:
                Log.w("CellType", "Tried to access undefined cell color");
                return context.getResources().getColor(R.color.cell_standard);
        }
    }

    public static int getShadowColor(CellType type, Context context) {
        switch (type) {
            case STANDARD:
                return context.getResources().getColor(R.color.cell_standard_shadow);
            case BAD:
                return context.getResources().getColor(R.color.cell_bad_shadow);
            default:
                Log.w("CellType", "Tried to access undefined cell color");
                return context.getResources().getColor(R.color.cell_standard);
        }
    }
}
