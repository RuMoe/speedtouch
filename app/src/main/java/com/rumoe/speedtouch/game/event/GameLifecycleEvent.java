package com.rumoe.speedtouch.game.event;

/**
 * Created by jan on 05.09.2014.
 */
public class GameLifecycleEvent extends GameEvent {

    public GameLifecycleEvent (EventType type) {
        super(type);
        if (!type.isLifeCycleEvent())
            throw new IllegalArgumentException("Event of type " + type +
                    " is not a lifecycle event");
    }
}
