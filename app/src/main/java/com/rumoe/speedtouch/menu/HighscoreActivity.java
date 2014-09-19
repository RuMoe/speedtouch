package com.rumoe.speedtouch.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.TextView;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.GameActivity;


public class HighscoreActivity extends Activity {

    private static final String SCORE_FILE_NAME         = "speedtouch.stat";

    public static final String INTENT_CURRENT_SCORE     = "CURRENT_SCORE";
    private static final int SCORE_LENGTH               = 8;

    private boolean wasPaused = false;

    private int currentScore = 0;
    private int bestScore = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            currentScore = extras.getInt(INTENT_CURRENT_SCORE);
        }

        // wire the buttons
        Button btnMenu = (Button) findViewById(R.id.btn_back_to_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToMenu();
            }
        });
        Button btnRetry = (Button) findViewById(R.id.btn_try_again);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToGame();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();

            // we don't need to animate the score again
        if (wasPaused) return;

        bestScore = retrieveBestScore();

        final TextView bestScoreTV = (TextView) findViewById(R.id.high_score_value);
        bestScoreTV.setText(getScoreString(bestScore));
        final TextView currentScoreTV = (TextView) findViewById(R.id.current_score_value);

        Animation scoreCountAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int score = (int) (interpolatedTime * currentScore);
                currentScoreTV.setText(getScoreString(score));
                if (score > bestScore) {
                    bestScoreTV.setText(getScoreString(score));
                }
            }
        };

        scoreCountAnim.setStartOffset(1500);
        scoreCountAnim.setDuration(1500);
        scoreCountAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        currentScoreTV.startAnimation(scoreCountAnim);

        if (currentScore > bestScore) {
            saveBestScore(currentScore);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        wasPaused = true;
    }

    private String getScoreString(int score) {
        return String.format("%0" + SCORE_LENGTH + "d", score);
    }

    private int retrieveBestScore() {
        SharedPreferences scores = getSharedPreferences(SCORE_FILE_NAME, 0);
        return scores.getInt(buildScoreId(), 0);
    }

    private void saveBestScore(int scoreToSave) {
        SharedPreferences scores = getSharedPreferences(SCORE_FILE_NAME, 0);
        SharedPreferences.Editor scoreEdit = scores.edit();
        scoreEdit.putInt(buildScoreId(), scoreToSave);
        scoreEdit.commit();
    }

    //TODO do this based on game mode etc.
    private String buildScoreId() {
        return "survival";
    }

    private void transitionToGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.to_game_enter, R.anim.to_game_exit);
    }

    private void transitionToMenu() {
        Intent intent = new Intent(this, TempStart.class);
        startActivity(intent);
        overridePendingTransition(R.anim.to_game_enter, R.anim.to_game_exit);
    }
}