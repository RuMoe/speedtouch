package com.rumoe.speedtouch.game;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.rumoe.speedtouch.game.event.GameEvent;
import com.rumoe.speedtouch.game.event.GameEventManager;
import com.rumoe.speedtouch.game.event.GameObserver;

public class EffectOverlay extends RelativeLayout implements GameObserver{

    public EffectOverlay(Context context) {
        super(context);
        GameEventManager.getInstance().register(this);
    }

    public EffectOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        GameEventManager.getInstance().register(this);
    }

    public EffectOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        GameEventManager.getInstance().register(this);
    }

    @Override
    public void notifyOnGameEvent(GameEvent e) {
        Log.e("test", "set");
    }
}
