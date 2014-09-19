package com.rumoe.speedtouch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.ui.GameActivity;


public class TempStart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_start);

        final Button button = (Button) findViewById(R.id.tempStart);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.to_game_enter, R.anim.to_game_exit);
            }
        });
    }
}
