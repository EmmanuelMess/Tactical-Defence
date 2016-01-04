package com.emmanuelmess.tacticaldefence.game.inanimate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.emmanuelmess.tacticaldefence.R;

/**
 * @author Emmanuel
 *         2015-03-22, at 13:17.
 */
public class Arrow {

	private final Bitmap[] arrow;
	private final Matrix matrix = new Matrix();
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
	private TYPES type;
	public Arrow(Context context) {
		arrow = new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.game_arrow),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_arrow_enhanced),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_arrow_thrower),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_arrow_thrower_enhanced)};

	}

	public static int getArrowVelocity(TYPES t) {
		switch (t) {
			case SIMPLE:
				return 100;
			case TOWER:
				return 80;
			case ARCHER:
				return 60;
			case ENHANCED:
				return 140;
			case THROWER:
				return 150;
			case TROWER_ENHANCED:
				return 200;
			default:
				throw new IllegalArgumentException();
		}
	}

	public static int getArrowDamage(TYPES t) {
		switch (t) {
			case SIMPLE:
				return 20;
			case TOWER:
				return 10;
			case ARCHER:
				return 2;
			case ENHANCED:
				return 40;
			case THROWER:
				return 100;
			case TROWER_ENHANCED:
				return 150;
			default:
				throw new IllegalArgumentException();
		}
	}

	public void drawArrow(float angle, float x, float y, Canvas canvas) {
		drawArrow(angle, x, y, paint, canvas);
	}

	private void drawArrow(float angle, float x, float y, Paint paint, Canvas canvas) {
		matrix.postRotate(angle);
		canvas.drawBitmap(Bitmap.createBitmap(getBitmap(), 0, 0, getBitmap().getWidth(), getBitmap().getHeight(), matrix, true), x, y, paint);
		matrix.reset();
	}

	public TYPES getArrowType() {
		return type;
	}

	public void setArrowType(TYPES t) {
		type = t;
	}

	private Bitmap getBitmap() {
		switch (type) {
			case SIMPLE:
			case TOWER:
			case ARCHER:
				return arrow[0];
			case ENHANCED:
				return arrow[1];
			case THROWER:
				return arrow[2];
			case TROWER_ENHANCED:
				return arrow[3];

			default:
				throw new IllegalArgumentException();
		}
	}

	public enum TYPES {SIMPLE, TOWER, ARCHER, ENHANCED, THROWER, TROWER_ENHANCED}

}
