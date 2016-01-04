package com.emmanuelmess.tacticaldefence.game.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;

import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.Game;

import java.util.Calendar;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
/**
 * @author Emmanuel
 *         on 2015-05-27, at 22:59.
 */
public class RenderBirthdayPresent {

	private boolean stillDraw = true;
	private Rect bdayRect = new Rect(), presentRect = new Rect();
	private Paint paint = new Paint();
	private String b, p;
	private Bitmap cake;

	public RenderBirthdayPresent(Context context) {
		b = context.getResources().getString(R.string.birthday) + " " + birthday() + "!";
		cake = BitmapFactory.decodeResource(context.getResources(), R.drawable.birthday);
		p = context.getResources().getString(R.string.present);
	}

	public static boolean isBirthdayToday() {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return (month == Calendar.JUNE && day == 9) || (month == Calendar.JANUARY && day == 7);
	}

	public void draw(Canvas canvas) {
		paint.setTextSize(26);
		paint.setTypeface(Typeface.create("", Typeface.BOLD));
		paint.getTextBounds(b, 0, b.length(), bdayRect);
		canvas.drawText(b, DD.getWidth()/2f - bdayRect.right/2f, 45, paint);

		canvas.drawBitmap(cake, DD.getWidth()/2f - cake.getWidth()/2f, DD.getHeight()/2f - cake.getHeight()/2f, null);

		paint.setTextSize(24);
		paint.getTextBounds(p, 0, p.length(), presentRect);
		canvas.drawText(p, DD.getWidth()/2f - presentRect.right/2f, DD.getHeight() - presentRect.height(), paint);
	}

	public void onTouchEvent(MotionEvent event, int level) {
		if (event.getAction() == MotionEvent.ACTION_UP && stillDraw) {
			Game.money += present(level);
			stillDraw = false;
		}
	}

	public boolean stillDraw() {
		return stillDraw;
	}

	private String birthday() {
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

		if (isBirthdayToday()) {
			if (day == 9) return "Emmanuel";
			if (day == 7) return "Mauricio";
		}

		return null;
	}

	private int present(int level) {
		return (int) (50*Math.pow((1 + Math.E), level));
	}

}
