package com.rumoe.speedtouch.game.ui;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.rumoe.speedtouch.R;
import com.rumoe.speedtouch.game.event.CellObserver;
import com.rumoe.speedtouch.game.ui.gameboard.Cell;
import com.rumoe.speedtouch.game.ui.gameboard.CellPosition;
import com.rumoe.speedtouch.game.ui.gameboard.CellType;

public class GameBoardFragment extends Fragment implements SurfaceHolder.Callback {

    private int boardWidth;
    private int boardHeight;

    private Thread boardDrawThread;

    private static final int ROW_COUNT      = 5;
    private static final int COLUMN_COUNT   = 3;
    /** contains the cells of the board. Should never be accessed directly. Use getCell() instead. */
    private final Cell[][] cells;

    private SurfaceView gameBoard;

    public GameBoardFragment() {
        cells = new Cell[ROW_COUNT][COLUMN_COUNT];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_game, container, false);
        gameBoard       = (SurfaceView) rootView.findViewById(R.id.gameBoard);
        gameBoard.getHolder().addCallback(this);

        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                CellPosition pos = new CellPosition(r, c);
                cells[r][c] = new Cell(this.getActivity(), pos);
            }
        }

        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (boardDrawThread != null && boardDrawThread.isAlive()){
            boardDrawThread.interrupt();
        }
        boardWidth = width;
        boardHeight = height;
        boardDrawThread = new BoardDrawThread();
        boardDrawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameBoard.getHolder().removeCallback(this);
        if (boardDrawThread != null) {
            boardDrawThread.interrupt();
        }
        clearAllCells();
    }

    public void subscribeToCells(CellObserver... obs) {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].registerObserver(o);
                }
            }
        }
    }

    public void unsubscribeToCells(CellObserver... obs) {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                for (CellObserver o : obs) {
                    cells[i][j].removeObserver(o);
                }
            }
        }
    }

    /**
     * Can be used to determine if a cell is currently active.
     * @param pos The cell to be checked.
     * @return true iff the cell is active, false otherwise.
     */
    public boolean isCellActive(CellPosition pos) {
        return getCell(pos).isActive();
    }

    /**
     * Starts the lifecycle of the cell on a specified position. A lifecycle contains three stages:
     * grow, constant size and shrink.
     * Calling this method will use the default timing of the lifecycle.
     * When calling this method successfully the cell will emit an CellEvent.ACTIVATED event.
     * @param pos The position of the cell which will be activated.
     * @param type Type the cell will have.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean activateCellLifeCycle(CellPosition pos, CellType type) {
        return !isCellActive(pos) && getCell(pos).activateLifecycle(type);
    }

    /**
     * Starts the lifecycle of the cell on a specified position. A lifecycle contains three stages:
     * grow, constant size and shrink.
     * When calling this method successfully the cell will emit an CellEvent.ACTIVATED event.
     * @param pos The position of the cell which will be activated.
     * @param type Type the cell will have.
     * @param growTime Time in ms for the grow phase of the lifecycle.
     * @param stayTime Time in ms for the constant size phase of the lifecycle.
     * @param shrinkTime Time in ms for the shrink phase of the lifecycle.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean activateCellLifeCycle(CellPosition pos, CellType type,
                                         int growTime, int stayTime, int shrinkTime) {
       return !isCellActive(pos) && getCell(pos).activateLifecycle(type, growTime, stayTime, shrinkTime);
    }

    /**
     * Starts the default blink animation of the cell on the specified position.
     * @param pos The position of the cell which shall blink.
     * @return true iff animation could be applied successfully, false otherwise.
     */
    public boolean blinkCell(CellPosition pos) {
        if (isCellActive(pos)) return false;
        Cell c = getCell(pos);
        return c.blink(c.getType());
    }

    /**
     * Clears the cell at the specified position. That means its state is set to deactivated and
     * all animations are stopped.
     * @param pos The position of the cell we want to clear.
     * @return true iff clear was successful, false otherwise.
     */
    public boolean clearCell(CellPosition pos) {
        getCell(pos).deactivate();
        return true;
    }

    /**
     * Returns the position of the center a cell on the board (which is its position on the
     * surface of its SurfaceView)
     *
     * @param pos CellPosition of the cell the coordinates are returned
     * @return A int array of length 2 containing the coordinates from top left corner
     *      int[0] -> x coordinate
     *      int[1] -> y coordinate
     */
    public int[] getCellCenterBoardPosition(CellPosition pos) {
        int[] cellDimension = getCellDimensions();
        int xCoord = (int) (cellDimension[0] * (pos.getColumn() + 0.5));
        int yCoord = (int) (cellDimension[1] * (pos.getRow() + 0.5));
        return new int[]{xCoord, yCoord};
    }


    /**
     * Gets the total amount of rows of the game board.
     * @return rows of the game board.
     */
    public int getRowCount() {
        return ROW_COUNT;
    }

    /**
     * Gets the total amount of columns of the game board.
     * @return columns of the game board.
     */
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    /**
     * Clears the whole game board and deactivates all cells.
     */
    private void clearAllCells() {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                CellPosition pos = new CellPosition(i, j);
                clearCell(pos);
            }
        }
    }

    /**
     * Internal wrapper to make sure not to mess up rows and columns when retrieving a cell.
     * @param pos Position of the cell which will be retrieved.
     * @return The cell object at the requested position or null if the position does not exist.
     */
    private Cell getCell(CellPosition pos) {
        return getCell(pos.getRow(), pos.getColumn());
    }

    /**
     * Internal wrapper to make sure not to mess up rows and columns when retrieving a cell.
     * @param row Row of the cell which will be retrieved.
     * @param column Column of the cell which will be retrieved.
     * @return The cell object at the requested position or null if the position does not exist.
     */
    private Cell getCell(int row, int column) {
        if (row < 0 || row >= getRowCount() ||
                column < 0 || column >= getColumnCount()) {
            Log.e("GameBoardFragment" , String.format("Requested cell position is out of bounds. " +
                            "The board has dimension %d,%d. Requested cell was %d, %d",
                    getRowCount(), getColumnCount(), row,column));
        }
        return cells[row][column];
    }

    /**
     * Returns the dimensions of the cells of the board.
     * @return array of length two:
     *      int[0] - width of the cells
     *      int[1] - height of the cells
     */
    private int[] getCellDimensions() {
        return new int[]{ boardWidth / getColumnCount(), boardHeight / getRowCount(), };
    }

    /**
     * Calculates the actual visible radius of the circle.
     * @param radiusPercentage value between 0.0f and 1.0f. 1.0f means maximum radius.
     * @return radius of the cell in px.
     */
    private int getCellCircleRadius(float radiusPercentage) {
        int[] cellDimen = getCellDimensions();
        float maxRadius = Math.min(cellDimen[0], cellDimen[1]) / 2
                - 2 * getResources().getDimension(R.dimen.board_cell_padding);
        return (int) (maxRadius * radiusPercentage);
    }

    /**
     * This inner class is the draw thread of GameBoard. Draws at a constant frame rate the board
     * depending on the state of all cells.
     */
    class BoardDrawThread extends Thread {

        /** frame rate which is used to draw the board */
        private static final int FPS = 40;
        /** amount of ms to wait between each frame */
        private static final int MS_WAIT_PER_FRAME = 1000 / FPS;

        @Override
        public void run() {
            SurfaceHolder surfaceHolder = gameBoard.getHolder();

            while(!isInterrupted()) {
                long refreshStart = System.currentTimeMillis();

                if (surfaceHolder != null && surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    if (canvas != null ) {
                        canvas.drawColor(getResources().getColor(R.color.game_board_background));

                        int activeCellsDetected = 0;
                        for (int r = 0; r < getRowCount(); r++) {
                            for (int c = 0; c < getColumnCount(); c++) {
                                CellPosition pos = new CellPosition(r, c);
                                Cell cell = getCell(pos);
                                if (cell.getRadius() == 0.0f) continue;

                                activeCellsDetected++;

                                int[] cellCenter = getCellCenterBoardPosition(pos);
                                int radius = getCellCircleRadius(cell.getRadius());
                                canvas.drawCircle(cellCenter[0], cellCenter[1], radius, getPaint(cell.getType()));
                            }
                        }
                        Log.d("debug", "active cells in draw thread: " + activeCellsDetected);

                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } else {
                        Log.d("GameBoardFragment", "Draw canvas unavailable");
                    }
                }

                try {
                    long frameDelay = System.currentTimeMillis() - refreshStart;
                    Thread.sleep(Math.max(MS_WAIT_PER_FRAME - frameDelay, 0));
                } catch (InterruptedException e) {
                    Log.e("GameBoardFragment", "Draw thread interrupted");
                    break;
                }
            }
        }

        /**
         * Returns the Paint of the cell which decides is lock. The object which
         * is returned depends on the CellType passed last time the cell was activated.
         * @return Paint of the cell.
         */
        public Paint getPaint(CellType type) {
            Paint paint = new Paint();
            paint.setColor(CellType.getCellColor(type, GameBoardFragment.this.getActivity()));
            paint.setShadowLayer(15.0f, 0.0f, 0.0f,
                    CellType.getShadowColor(type, GameBoardFragment.this.getActivity()));
            return paint;
        }
    }
}