package com.emmanuelmess.tacticaldefence.game.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.GameThread;

/**
 * @author impaler
 *         This is the main surface that handles the ontouch events and draws
 *         the image to the screen.
 */
public class RenderGameActivity extends SurfaceView implements
		SurfaceHolder.Callback {

	private final Context context;
	private Game game;
	private GameThread thread;
	private MotionEvent e1;
	private float start;

	// the fps to be displayed
	private String avgFps;

	public RenderGameActivity(Context context) {
		super(context);
		this.context = context;
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create the game loop thread
		thread = new GameThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		game = new Game(context);
		thread.setName("TacticalDefence.game");
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		pauseGameThread();
		thread = null;
		game.destroy();
		game = null;
	}

	public void surfacePaused() {
		pauseGameThread();
	}

	public void render(Canvas canvas) {
		if (!game.isDestroying) {
			game.onDraw(canvas);
			// display fps
			if (TacticalDefence.isDebug()) displayFps(canvas, avgFps);
		}
	}

	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
		if (!game.isDestroying) {
			game.onUpdate();
		}
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			e1 = MotionEvent.obtain(event);
			start = System.currentTimeMillis()/1000;
		} else if (event.getActionMasked() == MotionEvent.ACTION_UP && e1 != null) {
			float velocityX = Math.abs((e1.getX() - event.getX())/start);
			float velocityY = Math.abs((e1.getX() - event.getY())/start);
			onFling(e1, event, velocityX, velocityY);
		}
		game.onTouchEvent(event);
		return true;
	}

	@SuppressWarnings("UnusedReturnValue")
	private boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return game.onFling(e1, e2, velocityX, velocityY);
	}

	public boolean onBackPressed() {
		return game.onBackPressed();
	}

	public Game getGame() {
		return game;
	}

	private void pauseGameThread() {
		if (thread != null) {
			thread.setRunning(false);

			// tell the thread to shut down and wait for it to finish
			// this is a clean shutdown
			boolean retry = true;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					// try again shutting down the thread
				}
			}
		}
	}

}
