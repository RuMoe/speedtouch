package com.rumoe.speedtouch.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameLifecycleEvent;
import com.rumoe.speedtouch.game.event.GameObserver;
import com.rumoe.speedtouch.game.gameboard.Cell;
import com.rumoe.speedtouch.game.gameboard.CellPosition;
import com.rumoe.speedtouch.game.strategy.textview.GameLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.GameScoreUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalLifeUpdater;
import com.rumoe.speedtouch.game.strategy.textview.SurvivalScoreUpdater;
import com.rumoe.speedtouch.menu.HighscoreActivity;
import com.rumoe.speedtouch.menu.TempStart;

public class GameActivity extends Activity implements GameObserver {

    private boolean gameOverTriggered = false;

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
        gameThread = new GameThread(gameBoard);

        //TODO temporary clusterfuck
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1500);
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
        if (!gameOverTriggered)
            transitionToMenu();
    }

    @Override
    public void notifyOnGameEvent(GameEvent e) {
        switch (e.getType()) {
            case COUNTDOWN_END:
                startGame();
                break;
            case GAME_OVER:
                gameOverTriggered = true;
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {}
                        transitionToHighscore();
                    }
                }.start();
                break;
        }
    }

    private void startGame() {
        gameBoard.subscribeToCells(scoreUpdater, lifeUpdater, gameThread);

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

    /**
     * Returns the position of the center a cell on the screen.
     *
     * @param pos CellPosition of the cell the coordinates are returned
     * @return A int array of length 2 containing the coordinates from top left corner
     *      int[0] -> x coordinate
     *      int[1] -> y coordinate
     */
    public int[] getCellCenterScreenPosition(CellPosition pos) {
        Cell c = gameBoard.getCell(pos);

        int[] posHolder = new int[2];
        c.getLocationOnScreen(posHolder);
        posHolder[0] += c.getWidth() / 2;
        posHolder[1] += c.getHeight() / 2;
        return posHolder;
    }

    private void transitionToMenu() {
        Intent intent = new Intent(this, TempStart.class);
        startActivity(intent);
    }

    private void transitionToHighscore() {
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.to_game_enter, R.anim.to_game_exit);
    }
}
