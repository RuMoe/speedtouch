package com.rumoe.speedtouch.game.event;

/**
 * Implementing this interface will allow to subscribe to the game board for events regarding
 * its cells.
 */
public interface CellObserver {

    /**
     * Is called whenever a cell on the game board is activated.
     * @param event Emitted event.
     */
    void notifyOnActive(CellEvent event);

    /**
     * Is called whenever a cell is deactivated by timeout, which means the cell completed the
     * lifecycle without being touched by the player.
     * @param event Emitted event.
     */
    void notifyOnTimeout(CellEvent event);

    /**
     * Is called whenever a cell is deactivated by touch of the player.
     * @param event Emitted event.
     */
    void notifyOnTouch(CellEvent event);

    /**
     * Is called whenever the player touches the game board without hitting a cell. In this case
     * event.getCellPosition() will contain the nearest cell.
     * @param event Emitted event.
     */
    void notifyOnMissedTouch(CellEvent event);

    /**
     * Is called whenever a cell is deactivated due to an unnatural cause (e.g. Cell is cleared
     * because of game over)
     * @param event Emitted event.
     */
    void notifyOnKill(CellEvent event);
}
