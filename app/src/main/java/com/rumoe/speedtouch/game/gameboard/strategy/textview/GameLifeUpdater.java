package com.rumoe.speedtouch.game.gameboard.strategy.textview;

import android.app.Activity;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.gameboard.CellEvent;

/**
 * Created by jan on 31.08.2014.
 */
public abstract class GameLifeUpdater extends TextViewUpdater {

    private static final int LIFE_VIEW_ID = R.id.gameLifeView;

    GameLifeUpdater(Activity activity) {
        super(activity, (TextView) activity.findViewById(LIFE_VIEW_ID));
    }

    public synchronized void updateLife(CellEvent event) {
        calculateNewLife(event);
        updateText(getLifeAsString());
    }

    abstract void calculateNewLife(CellEvent event);

    abstract String getLifeAsString();

    public abstract boolean isGameOver();
}
