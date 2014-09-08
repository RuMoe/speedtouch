package com.rumoe.speedtouch.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameLifecycleEvent;
import com.rumoe.speedtouch.game.event.GameObserver;
import com.rumoe.speedtouch.game.strategy.textview.GameLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.GameScoreUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalScoreUpdater;
import com.rumoe.speedtouch.menu.TempStart;

public class GameActivity extends Activity implements GameObserver {

    private GameThread          gameThread;
    private GameBoardFragment   gameBoard;
    private GameScoreUpdater    scoreUpdater;
    private GameLifeUpdater     lifeUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState == null) {
            gameBoard = new GameBoardFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, gameBoard)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GameEventManager.getInstance().register(this);

        //TODO for now scoreUpdater and lifeUpdater will be hardcoded.. change that at some point
        scoreUpdater = new SurvivalScoreUpdater(this);
        lifeUpdater = new SurvivalLifeUpdater(this);
        gameThread = new GameThread(gameBoard.getCells());

        gameBoard.subscribeToCells(scoreUpdater, lifeUpdater, gameThread);

        //TODO temporary clusterfuck
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}

                GameEventManager.getInstance()
                        .notifyAll(new GameLifecycleEvent(GameEvent.EventType.COUNTDOWN_START));

           }
        }.start();
    }

    @Override
    public void onBackPressed() {
        // disable the back button during the game
    }

    @Override
    public void onPause() {
        super.onPause();
        gameBoard.unsubscribeToCells(scoreUpdater, lifeUpdater, gameThread);
        GameEventManager.getInstance().unregisterAll();
        gameThread.gameOver();
        transitionToMenu();
    }

    @Override
    public void notifyOnGameEvent(GameEvent e) {
        switch (e.getType()) {
            case COUNTDOWN_END:
                startGame();
                break;
            case GAME_OVER:
                transitionToMenu();
                break;
        }
    }

    private void startGame() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // does not matter if it is interrupted here
                }
                GameEventManager.getInstance().notifyAll(new GameLifecycleEvent(GameEvent.EventType.GAME_START));
            }
        }.start();

    }

    private void transitionToMenu() {
        Intent intent = new Intent(this, TempStart.class);
        startActivity(intent);
    }
}
