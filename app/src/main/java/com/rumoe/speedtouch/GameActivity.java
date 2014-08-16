package com.rumoe.speedtouch;

import android.app.Activity;
import android.os.Bundle;

import com.rumoe.speedtouch.gameboard.GameBoardFragment;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new GameBoardFragment())
                    .commit();
        }
    }


}
