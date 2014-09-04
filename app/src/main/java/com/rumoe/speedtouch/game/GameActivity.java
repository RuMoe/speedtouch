package com.rumoe.speedtouch.game;

import android.app.Activity;
import android.os.Bundle;

import com.rumoe.speedtouch.R;

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
