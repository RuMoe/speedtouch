package com.rumoe.speedtouch.game.event;

import java.util.ArrayList;

/**
 * Created by jan on 05.09.2014.
 */
public class GameEventManager {

    private static GameEventManager instance;

    private ArrayList<GameObserver> observer;

    private GameEventManager() {
        observer = new ArrayList<GameObserver>(10);
    }

    public static GameEventManager getInstance() {
        if (instance == null) {
            instance = new GameEventManager();
        }
        return instance;
    }

    public synchronized void register(GameObserver obs) {
        observer.add(obs);
    }

    public synchronized void unregister(GameObserver obs) {
        observer.remove(obs);
    }

    public synchronized void unregisterAll() {
        observer.clear();
    }

    public synchronized void notifyAll(GameEvent event) {
        for (int i = observer.size() - 1; i >= 0; i--) {
            if (observer.get(i) == null) {
                observer.remove(i);
            } else {
                observer.get(i).notifyOnGameEvent(event);
            }
        }
    }
}
