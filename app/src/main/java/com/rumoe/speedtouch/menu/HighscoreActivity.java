package com.rumoe.speedtouch.menu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.rumoe.speedtouch.R;


public class HighscoreActivity extends Activity {

    public static final String INTENT_CURRENT_SCORE     = "CURRENT_SCORE";
    private static final int SCORE_LENGTH               = 8;

    private int currentScore = 0;
    private int bestScore    = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            currentScore = extras.getInt(INTENT_CURRENT_SCORE);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();

        TextView currentScoreTV = (TextView) findViewById(R.id.current_score_value);
        currentScoreTV.setText(getScoreString(currentScore));
    }

    private String getScoreString(int score) {
        return String.format("%0" + SCORE_LENGTH + "d", score);
    }
}