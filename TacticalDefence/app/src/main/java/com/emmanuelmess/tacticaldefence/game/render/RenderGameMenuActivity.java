package com.emmanuelmess.tacticaldefence.game.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.emmanuelmess.API.twodmensions.Contain;
import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.AICastle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.PlayerCastle;

import static android.view.GestureDetector.SimpleOnGestureListener;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity.getLevel;
import static com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity.getMaximumLevel;
import static com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity.lvlIndex;

/**
 * @author Emmanuel
 *         on 2015-01-21, at 13:28 PM.
 */
public class RenderGameMenuActivity extends View {

	private static boolean goLoading, mem;
	private final Context context;
	private final Paint paint = new Paint();
	private final RectF arrowUpBounds = new RectF();
	private final RectF arrowDownBounds = new RectF();
	private final Rect loadingBounds = new Rect();
	public PlayerCastle castle;
	SimpleOnGestureListener sgd = new SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() > DD.getWidth()/3f && e2.getX() > DD.getWidth()/3f && Math.abs(e1.getX() - e2.getX()) < 200
					&& Math.abs(e1.getY() - e2.getY()) > 50) {
				if (getLevel() != getMaximumLevel() && e1.getX() > e2.getX()) lvlIndex++;
				else if (getLevel() != 0 && e1.getX() < e2.getX()) lvlIndex--;
				postInvalidate();
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};
	private Bitmap arrow = null;
	private Bitmap arrowDown = null;
	//private RenderLoad load = new RenderLoad(); TODO: 2015-10-24
	private GestureDetector g;
	private AICastle aiCastle;

	public RenderGameMenuActivity(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		aiCastle = ((GameMenuActivity) context).getAICastle();
		g = new GestureDetector(context, sgd);

		arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_main_arrow);

		Matrix matrix = new Matrix();
		matrix.postRotate(180);
		arrowDown = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (!goLoading && event.getAction() == MotionEvent.ACTION_UP) {
			if (getLevel() != getMaximumLevel() && Contain.isContainedInRectF(arrowUpBounds, event))
				lvlIndex++;
			else if (getLevel() != 0 && Contain.isContainedInRectF(arrowDownBounds, event))
				lvlIndex--;
		}
		g.onTouchEvent(event);

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {//TODO bitmaps on top and bottom
		String lvl = context.getResources().getString(R.string.level) + ": " + (lvlIndex == 0? context.getResources().getString(R.string.tutorial):lvlIndex);
		if (!goLoading) {
			setBackgroundColor(Color.GREEN);

			//Player
			if (!isInEditMode()) castle.draw(canvas);

			//Enemy
			aiCastle.setLevel(lvlIndex);
			aiCastle.draw(canvas);

			if (lvlIndex != getMaximumLevel()) {
				arrowUpBounds.set(aiCastle.getRectF().centerX() - arrow.getWidth(), 0,
						aiCastle.getRectF().centerX() + arrow.getWidth(), DD.getHeight()/2f);

				DrawHelper.drawBitmap(arrow, arrowUpBounds, canvas);
			}
			if (lvlIndex != 0) {
				arrowDownBounds.set(aiCastle.getRectF().centerX() - arrowDown.getWidth(), DD.getHeight()/2f,
						aiCastle.getRectF().centerX() + arrowDown.getWidth(), DD.getHeight());

				DrawHelper.drawBitmap(arrowDown, arrowDownBounds, canvas);
			}

			GameMenuActivity.getLevelTextView().setText(lvl);
			invalidate(DD.getWidth()/2, 0, DD.getWidth(), DD.getHeight());
		} else {
			String l = context.getResources().getString(R.string.loading);

			setBackgroundColor(Color.BLACK);

			paint.setColor(Color.RED);
			paint.setTextSize(56);
			paint.getTextBounds(l, 0, l.length(), loadingBounds);
			canvas.drawText(l, DD.getWidth()/2f - loadingBounds.width()/2f, DD.getHeight()/2f - loadingBounds.height()/2f, paint);
			loadingBounds.setEmpty();

			paint.setTextSize(30);
			paint.getTextBounds(lvl, 0, lvl.length(), loadingBounds);
			canvas.drawText(lvl, DD.getWidth()/2f - loadingBounds.width()/2f, 0f, paint);
			loadingBounds.setEmpty();

			invalidate();
		}
	}

	public void goLoading(boolean b) {
		goLoading = b;
		//postInvalidate();
	}

}