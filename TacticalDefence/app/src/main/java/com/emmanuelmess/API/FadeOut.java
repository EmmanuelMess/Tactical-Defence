package com.emmanuelmess.API;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Emmanuel
 *         on 2015-01-07, at 01:28 AM, at 22:27.
 */
public class FadeOut {

	//Calculate our alpha step from our fade parameters
	private final double ALPHA_STEP;
	//Initializes the alpha to 255
	private final Paint alphaPaint = new Paint();
	private final Chronometer cr = new Chronometer();
	private boolean faded = false;
	//Need to keep track of the current alpha value
	private int currentAlpha = 255;

	public FadeOut(int timeInMilliseconds) {
		ALPHA_STEP = timeInMilliseconds/255;
	}

	public void doFadeOut(Canvas canvas, Bitmap bitmap, int x, int y) {
		if (!cr.hasStarted() && !faded) cr.start();

		if (currentAlpha > 0 && cr.getElapsedTime() >= ALPHA_STEP) {
			// Draw your bitmap at the current alpha value
			canvas.drawBitmap(bitmap, x, y, alphaPaint);

			// Update alpha by a step
			alphaPaint.setAlpha(currentAlpha);
			currentAlpha -= ALPHA_STEP;
		} else if (currentAlpha <= 0) {
			faded = true;
			cr.stop();
		}
	}

	public void setAlpha(int alpha) {
		if (alpha > 255 || alpha < 0)
			throw new IllegalArgumentException("Alpha value must be less than 255 and more than 0!");
		currentAlpha = alpha;
	}

	public boolean isFaded() {
		return faded;
	}
}
