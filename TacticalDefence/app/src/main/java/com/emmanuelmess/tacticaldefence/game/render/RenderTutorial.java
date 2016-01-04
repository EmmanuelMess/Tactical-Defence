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
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.players.AI;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.players.Player.getPieceGroup;

/**
 * @author Emmanuel
 *         2015-04-03, at 14:25.
 */
public class RenderTutorial {

	private final Context context;
	private final Bitmap arrow;
	private final Bitmap arrowDown;
	private final Bitmap arrowLeft;
	private final Bitmap arrowRight;
	private final Bitmap swipeArrow;
	private final Bitmap swipeArrowDown;
	private final Paint paint = new Paint();
	private final Rect r = new Rect();
	private final StaticLayout swipeLayout;
	private boolean touched;

	public RenderTutorial(Context context) {
		this.context = context;

		arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_tutorial_arrow_touch);
		Matrix matrix = new Matrix();
		matrix.postRotate(180);
		arrowDown = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
		matrix.reset();
		matrix.postRotate(-90);
		arrowLeft = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
		matrix.reset();
		matrix.postRotate(90);
		arrowRight = Bitmap.createBitmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);

		swipeArrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_tutorial_arrow_swipe);

		matrix.reset();
		matrix.postRotate(180);
		swipeArrowDown = Bitmap.createBitmap(swipeArrow, 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);

		TextPaint swipePaint = new TextPaint();
		swipePaint.setTextSize(24);
		swipePaint.setColor(Color.WHITE);
		swipePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

		swipeLayout = new StaticLayout(SDK_INT < KITKAT? context.getResources().getString(R.string.swipe):context.getResources().getString(R.string.touch_menu),
				swipePaint, DD.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

		paint.setTextSize(24);
		paint.setColor(Color.WHITE);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
	}

	public int onDraw(Canvas canvas, int state, boolean menuOpen, int menuHeight, int selected, RectF moveButton, boolean moveSelected,
	                  RectF attackButton, boolean attack, float centerXheal, int menu) {
		switch (state) {
			case -1:
				paint.setColor(Color.BLACK);
				canvas.drawRect(0, 0, DD.getWidth(), DD.getHeight(), paint);
				paint.setColor(Color.WHITE);
				canvas.drawBitmap(arrow, DD.getWidth()/4f - arrow.getWidth()/2f, DD.getHeight()/4f - arrow.getHeight()/2f, null);
				canvas.drawText(context.getResources().getString(R.string.arrow_explanation), DD.getWidth()/4f + arrow.getWidth(), DD.getHeight()/4f, paint);
				canvas.drawBitmap(swipeArrow, DD.getWidth()/4f - swipeArrow.getWidth()/2f, DD.getHeight()/4f*3f - swipeArrow.getHeight()/2f, null);
				canvas.drawText(context.getResources().getString(R.string.black_arrow_explanation), DD.getWidth()/4f + swipeArrow.getWidth(), DD.getHeight()/4f*3f, paint);
				if (touched)
					state = 0;
				break;
			case 0:
				openMenu(canvas);
				if (menuOpen) state = 1;
				break;
			case 1:
				if (!menuOpen) state = 0;
				else if (selected != 10) {
					canvas.drawBitmap(arrow, arrow.getWidth()/2, menuHeight - 10f, null);
					canvas.drawText(context.getResources().getString(R.string.touch), arrow.getWidth(), menuHeight + arrow.getHeight()/2f, paint);
				} else state = 2;
				break;
			case 2:
				canvas.drawBitmap(arrowLeft, Game.getAllyCastle().getRectF().width(), DD.getHeight()/2, null);
				if (getPieceGroup().getAll().length > getPieceGroup().getAmountDead().size())
					state = 3;
				else if (!menuOpen) state = 0;
				break;
			case 3:
				canvas.drawBitmap(arrowRight, moveButton.left - arrowRight.getWidth(), moveButton.centerY() - arrowRight.getHeight()/2f, null);
				if (moveSelected) state = 4;
				else if (getPieceGroup().getAmountDead().size() == getPieceGroup().getAll().length)
					state = 0;
				break;
			case 4:
				Piece p = null;

				for (Piece pi : AI.getPieceGroup().getAll())
					if (pi.getState() != Piece.state.DEAD) {
						p = pi;
						break;
					}
				if (p == null) state = 7;
				else if (p.getCoordinates()[1] < menuHeight && menuOpen) {
					if (SDK_INT < KITKAT) {
						canvas.drawBitmap(swipeArrow, DD.getWidth()/2f - swipeArrow.getWidth()/2f, DD.getHeight()/2f - swipeArrow.getHeight()/2f, null);

						canvas.save();
						canvas.translate(DD.getWidth()/2f + swipeArrow.getWidth()/2, DD.getHeight()/2f - swipeArrow.getHeight()/4f);
					} else {
						canvas.drawBitmap(arrow, DD.getWidth()/2f - arrow.getWidth()/2f, 50, null);

						canvas.save();
						canvas.translate(DD.getWidth()/2f + arrow.getWidth()/2f, 50 + arrow.getHeight()/4f);
					}
					canvas.restore();
				} else {
					canvas.drawBitmap(arrowDown, p.getCoordinates()[0] - arrowDown.getWidth()/2, p.getCoordinates()[1] - arrowDown.getHeight(),
							null);
					Piece piece = getPieceGroup().getPieceByIdentifier(getPieceGroup().getSelected());
					if (piece != null && piece.isBeingAttacked() != null)
						state = 5;
				}
				break;
			case 5:
				canvas.drawBitmap(arrowDown, attackButton.centerX() - arrowDown.getWidth()/2, attackButton.top - arrowDown.getHeight(), null);
				if (attack) state = 6;
				else if (getPieceGroup().getAmountDead().size() == getPieceGroup().getAll().length)
					state = 0;
				break;
			case 6:
				p = null;

				for (Piece pi : AI.getPieceGroup().getAll())
					if (pi.getState() != Piece.state.DEAD) {
						p = pi;
						break;
					}
				if (p == null) state = 7;
				else {
					canvas.drawBitmap(arrowDown, p.getCoordinates()[0] - arrowDown.getWidth()/2, p.getCoordinates()[1] - arrowDown.getHeight(), null);
					if (getPieceGroup().getAmountDead().size() == getPieceGroup().getAll().length)
						state = 0;
				}
				break;
			case 7:
				openMenu(canvas);
				String s = context.getResources().getString(R.string.money_added);

				paint.getTextBounds(s, 0, s.length(), r);
				canvas.drawText(s, DD.getWidth()/6, DD.getHeight() - r.height(), paint);
				if (menuOpen) state = 8;
				break;
			case 8:
				if (!menuOpen) state = 7;

				Piece mem = getPieceGroup().getPieceByIdentifier(getPieceGroup().getSelected());

				if (mem != null) {
					if (mem.getCoordinates()[0] < menuHeight && menuOpen) {
						if (!moveSelected)
							canvas.drawBitmap(arrowRight, moveButton.left - arrowRight.getWidth(), moveButton.centerY() - arrowRight.getHeight()/2f, null);
						else
							canvas.drawBitmap(arrowDown, mem.getCoordinates()[0], menuHeight + 20, null);
					} else if (mem.getHealth() != mem.getMaxHealth()) {
						if (menu == 0)
							canvas.drawBitmap(arrow, centerXheal - arrow.getWidth()/2, menuHeight, null);
						else if (menu == 2)
							canvas.drawBitmap(arrow, arrow.getWidth()/2f, menuHeight - 10f, null);
						if (selected == 20) state = 9;
					}
				} else state = 10;
				break;
			case 9:
				p = getPieceGroup().getPieceByIdentifier(getPieceGroup().getSelected());
				float[] f = p.getCoordinates();
				canvas.drawBitmap(arrowDown, f[0] - arrowDown.getWidth()/2, f[1] - p.height()/2 - arrowDown.getHeight()/2, null);
				if (p.getHealth() == p.getMaxHealth())
					state = 10;
				break;
			case 10:
				p = null;
				if (AI.getPieceGroup().getAll().length != AI.getPieceGroup().getAmountDead().size()) {
					for (Piece pi : AI.getPieceGroup().getAll())
						if (pi.getState() != Piece.state.DEAD) {
							p = pi;
							break;
						}

					if (p != null)
						canvas.drawBitmap(arrowDown, p.getCoordinates()[0] - arrowDown.getWidth()/2,
								p.getCoordinates()[1] - AI.getPieceGroup().getAll()[0].height()/2 - arrowDown.getHeight()/2, null);
				} else
					canvas.drawBitmap(arrowRight, Game.getEnemyCastle().getRectF().left - arrowRight.getWidth(), Game.getEnemyCastle().getRectF().centerY() - arrowRight.getHeight()/2,
							null);
				// canvas.drawText(context.getResources().getString(R.string.attack), Game.getEnemyCastle().getRectF().left - arrowRight.getWidth(),
				//					   Game.getEnemyCastle().getRectF().centerY() - arrowRight.getHeight(), paint);
				break;
		}
		return state;
	}

	public void onTouchEvent(MotionEvent e) {
		if (!touched && e.getAction() == MotionEvent.ACTION_UP) touched = true;
	}

	private void openMenu(Canvas canvas) {
		if (SDK_INT < KITKAT) {
			canvas.drawBitmap(swipeArrowDown, DD.getWidth()/2f - swipeArrowDown.getWidth()/2f, DD.getHeight()/2f - swipeArrowDown.getHeight()/2f, null);

			canvas.save();
			canvas.translate(DD.getWidth()/2f + swipeArrowDown.getWidth()/2, DD.getHeight()/2f - swipeArrowDown.getHeight()/4f);
		} else {
			canvas.drawBitmap(arrow, DD.getWidth()/2f - arrow.getWidth()/2f, 50, null);

			canvas.save();
			canvas.translate(DD.getWidth()/2f + arrow.getWidth()/2f, 50 + arrow.getHeight()/4f);
		}
		swipeLayout.draw(canvas);
		canvas.restore();
	}

}
