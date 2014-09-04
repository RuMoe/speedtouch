package com.rumoe.speedtouch.game.event;

/**
 * Created by jan on 04.09.2014.
 */
abstract class GameEvent {

    public enum EventType {

        COUNTDOWN_START(true),
        GAME_START(true),
        SCORE_CHANGE(false),
        LIFE_CHANGE(false),
        GAME_OVER(true);

        private boolean lifeCycleEvent;

        EventType(boolean lifeCycleEvent) {
            this.lifeCycleEvent = lifeCycleEvent;
        }

        public boolean isLifeCycleEvent() {
            return lifeCycleEvent;
        }
    }

    private final EventType type;

    GameEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}

