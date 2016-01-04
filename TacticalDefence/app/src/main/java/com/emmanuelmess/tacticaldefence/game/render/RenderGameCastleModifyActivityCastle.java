package com.emmanuelmess.tacticaldefence.game.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.activities.game.GameCastleModifyActivity;

import static com.emmanuelmess.API.twodmensions.ScaleBitmap.getResizedBitmap;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.DEFENSIVE_UPGRADES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.OFFENSIVE_UPGRADES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.getDefensivePoints;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.getOffensivePoints;

/**
 * @author Emmanuel
 *         on 2015-01-21, at 02:51 PM.
 */
public class RenderGameCastleModifyActivityCastle extends View {

	private final Context context;
	private final RectF[] castle;
	private final Bitmap[][] castleB;
	private final Chronometer chr = new Chronometer();
	private final float DIMENSION_CONST_X, DIMENSION_CONST_Y;
	private boolean setRedMoney = false;
	private GameCastleModifyActivity act;
	private Paint paint = new Paint();
	private PointF[] pt = {new PointF(), new PointF(), new PointF()};

	public RenderGameCastleModifyActivityCastle(Context context, AttributeSet attrSet) {
		super(context, attrSet);

		this.context = context;
		act = (GameCastleModifyActivity) context;
		DIMENSION_CONST_X = DD.getWidth()/480f;
		DIMENSION_CONST_Y = DD.getHeight()/320f;

		castleB = new Bitmap[][]{{BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_ally),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_tower_ally),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_walls_ally),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_walls_tower_ally),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_tower_ii_ally),
				BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_walls_tower_ii_ally)},
				{BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_rampart_ally),
						BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_tower_ally),
						BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_rampart_walls_ally),
						BitmapFactory.decodeResource(context.getResources(), R.drawable.game_castle_walls_tower_ally)}};

		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		castleB[0][0] = Bitmap.createBitmap(castleB[0][0], 0, 0, castleB[0][0].getWidth(), castleB[0][0].getHeight(), matrix, true);

		float[] d = {DD.getHeight()/5f*2f*castleB[0][0].getWidth()/castleB[0][0].getHeight(),
				DD.getHeight()*castleB[1][0].getWidth()/castleB[1][0].getHeight()};
		castle = new RectF[]{new RectF(DD.getWidth()/2f - d[0]/2f, DD.getHeight()/5f, DD.getWidth()/2f + d[0]/2f, DD.getHeight()/5f*3f),
				new RectF(DD.getWidth()/2f - d[1]/2f, DD.getHeight()/5f, DD.getWidth()/2f + d[1]/2f, DD.getHeight()/5f*4f)};

		/*no rampart*/
		{
			castleB[0][2] = Bitmap.createBitmap(castleB[0][2], 0, 0, castleB[0][2].getWidth(), castleB[0][2].getHeight(), matrix, true);
			castleB[0][4] = Bitmap.createBitmap(castleB[0][4], 0, 0, castleB[0][4].getWidth(), castleB[0][4].getHeight(), matrix, true);
			castleB[0][5] = Bitmap.createBitmap(castleB[0][5], 0, 0, castleB[0][5].getWidth(), castleB[0][5].getHeight(), matrix, true);

			castleB[0][0] = getResizedBitmap(castleB[0][0], castle[0].width(), castle[0].height());
			castleB[0][1] = getResizedBitmap(castleB[0][1], castle[0].width()/2.5f, castle[0].width()/2.5f);
			castleB[0][3] = getResizedBitmap(castleB[0][3], castleB[0][1].getHeight() + castleB[0][1].getHeight()/8f, castleB[0][1].getHeight() + castleB[0][1].getHeight()/8f);
			castleB[0][2] = getResizedBitmap(castleB[0][2], castle[0].width() + castleB[0][2].getHeight()/9.28f*2, castle[0].height() + castleB[0][2].getHeight()/9.28f);
			float wall = castleB[0][3].getHeight() - castleB[0][1].getHeight();
			castleB[0][4] = getResizedBitmap(castleB[0][4], castle[0].width()/2.5f, castle[0].width()/2.5f - wall);//TODO
			castleB[0][5] = getResizedBitmap(castleB[0][5], castleB[0][1].getHeight() + castleB[0][1].getHeight()/8f, castleB[0][1].getHeight() + castleB[0][1].getHeight()/8f - wall*1.5f);
		}

		/*there is rampart*/
		{
			castleB[1][0] = Bitmap.createBitmap(castleB[1][0], 0, 0, castleB[1][0].getWidth(), castleB[1][0].getHeight(), matrix, true);
			castleB[1][2] = Bitmap.createBitmap(castleB[1][2], 0, 0, castleB[1][2].getWidth(), castleB[1][2].getHeight(), matrix, true);

			castleB[1][0] = getResizedBitmap(castleB[1][0], castle[1].width(), castle[1].height());
			castleB[1][1] = getResizedBitmap(castleB[0][1], castle[0].width()/4.5f, castle[0].width()/4.5f);
			castleB[1][2] = getResizedBitmap(castleB[1][2], castle[1].width(), castle[1].height());
			castleB[0][3] = getResizedBitmap(castleB[0][3], castleB[0][1].getHeight() + castleB[0][1].getHeight()/8f, castleB[0][1].getHeight() + castleB[0][1].getHeight()/8f);
		}

	}

	protected void onDraw(Canvas canvas) {
		TextView money = GameCastleModifyActivity.getMoneyTextView(), offensivePointsText = GameCastleModifyActivity.getOffensivePoints(),
				defensivePointsText = GameCastleModifyActivity.getDefensivePoints();

		int offensivePoints = 0, defensivePoints = 0;

		setBackgroundColor(Color.GREEN);

		float w = castleB[0][1].getWidth()/2f;

		if (!this.isInEditMode()) {
			if (!act.showDefensive[2]) {
				float d = (castleB[0][3].getWidth() - castleB[0][1].getWidth())/2f;

				if (!act.showDefensive[0])
					canvas.drawBitmap(castleB[0][0], castle[0].left, castle[0].top, null);//TODO test
				else {
					canvas.drawBitmap(castleB[0][2], castle[0].left - (castleB[0][2].getHeight() - castleB[0][0].getHeight()), castle[0].top, null);

					defensivePoints += getDefensivePoints(DEFENSIVE_UPGRADES.WALL);
				}

				if (act.showOffensive[0]) {
					if (!act.showDefensive[0]) {
						canvas.drawBitmap(castleB[0][1], castle[0].left - w, castle[0].bottom - w, null);
						canvas.drawBitmap(castleB[0][1], castle[0].right - w, castle[0].bottom - w, null);
					} else {
						canvas.drawBitmap(castleB[0][3], castle[0].left - w - castleB[0][1].getHeight()/8f/2f, castle[0].bottom - w - castleB[0][1].getHeight()/8f/2f, null);
						canvas.drawBitmap(castleB[0][3], castle[0].right - w - castleB[0][1].getHeight()/8f/2f, castle[0].bottom - w - castleB[0][1].getHeight()/8f/2f, null);
					}

					offensivePoints += getOffensivePoints(OFFENSIVE_UPGRADES.TOWERS);
				}
				if (act.showOffensive[1]) {
					float wh = (castle[0].left + castle[0].width()/2f) - 42f;
					paint.setColor(Color.BLACK);
					for (int i = 0; i < 5; i++)
						canvas.drawCircle(wh + 21*i*DIMENSION_CONST_X, castle[0].bottom - 20*DIMENSION_CONST_Y, 7*DIMENSION_CONST_X, paint);

					offensivePoints += getOffensivePoints(OFFENSIVE_UPGRADES.ARCHERS);
				}
				if (act.showOffensive[2]) {
					if (!act.showDefensive[0])
						canvas.drawBitmap(castleB[0][4], DD.getWidth()/2f - castleB[0][1].getWidth()/2f, castle[0].top, null);
					else
						canvas.drawBitmap(castleB[0][5], DD.getWidth()/2f - castleB[0][1].getWidth()/2f - d, castle[0].top, null);

					offensivePoints += getOffensivePoints(OFFENSIVE_UPGRADES.TOWERS_II);
				}
			} else {
				float y = castle[0].top + castle[0].height()/2f - castleB[1][1].getHeight()/2f, d = (castleB[1][3].getWidth() - castleB[1][1].getWidth())/2f,
						x = DD.getWidth()/2f - castleB[1][1].getWidth()/2f;
				pt[0].set(castle[1].left - 12*DIMENSION_CONST_X, y);
				pt[1].set(castle[1].right - castleB[1][1].getWidth() + 12*DIMENSION_CONST_X, y);
				pt[2].set(x, castle[1].bottom - castleB[1][1].getHeight());

				if (!act.showDefensive[0])
					canvas.drawBitmap(castleB[1][0], castle[1].left, castle[1].top, null);
				else {
					canvas.drawBitmap(castleB[1][2], castle[1].left, castle[1].top, null);

					defensivePoints += getDefensivePoints(DEFENSIVE_UPGRADES.WALL);
				}

				defensivePoints += getDefensivePoints(DEFENSIVE_UPGRADES.RAMPART);

				if (act.showOffensive[0]) {
					if (!act.showDefensive[0]) {
						canvas.drawBitmap(castleB[1][1], pt[0].x, pt[0].y, null);
						canvas.drawBitmap(castleB[1][1], pt[1].x, pt[1].y, null);
					} else {
						canvas.drawBitmap(castleB[1][3], pt[0].x - d, pt[0].y - d, null);
						canvas.drawBitmap(castleB[1][3], pt[1].x - d, pt[1].y - d, null);
					}

					offensivePoints += getOffensivePoints(OFFENSIVE_UPGRADES.TOWERS);
				}
				if (act.showOffensive[1]) {
					paint.setColor(Color.BLACK);
					canvas.drawCircle(DD.getWidth()/2f, y - 7*DIMENSION_CONST_Y, 7*DIMENSION_CONST_X, paint);

					/*
					float d02 = (float) sqrt(pow(pt[2].x - pt[0].x, 2) + pow(pt[2].y - pt[0].y, 2)), m = castleB[1][1].getWidth()/2f,
							Ax = pt[0].x + (pt[2].x - pt[0].x)*(d02/2f + m)/d02;

					canvas.drawCircle(Ax, pt[0].y + (pt[2].y-pt[0].y)*(d02/2f + m)/d02, 7*DIMENSION_CONST_X, paint);
					canvas.drawCircle(Ax, pt[1].y + (pt[2].y-pt[1].y)*(d02/2f + m)/d02, 7*DIMENSION_CONST_X, paint);

					Ax = pt[0].x + (pt[2].x - pt[0].x)*(d02/2f - m)/d02;
					canvas.drawCircle(Ax, pt[0].y + (pt[2].y-pt[0].y)*(d02/2f - m)/d02, 7*DIMENSION_CONST_X, paint);
					canvas.drawCircle(Ax, pt[1].y + (pt[2].y-pt[1].y)*(d02/2f - m)/d02, 7*DIMENSION_CONST_X, paint);
					*/

					offensivePoints += getOffensivePoints(OFFENSIVE_UPGRADES.ARCHERS);
				}
				if (act.showOffensive[2]) {
					if (!act.showDefensive[0])
						canvas.drawBitmap(castleB[1][1], pt[2].x, pt[2].y, null);
					else canvas.drawBitmap(castleB[1][3], pt[2].x - d, pt[2].y - d, null);

					offensivePoints += getOffensivePoints(OFFENSIVE_UPGRADES.TOWERS_II);
				}
			}

			String moneyS = "$" + act.money + (act.spent != 0? (" - $" + act.spent):"");

			money.setText(moneyS);
			money.setTextSize(42);
			if (setRedMoney && !chr.hasStarted()) {
				money.setTextColor(Color.RED);
				chr.start();
				invalidate();
			}

			if ((chr.hasStarted() && chr.getElapsedTime() >= 250) || !setRedMoney) {
				money.setTextColor(Color.BLACK);
				chr.stop();
				setRedMoney = false;
				invalidate();
			} else invalidate();

			String offensivePointsTextString = context.getResources().getText(R.string.offPoints) + ": +" + offensivePoints;
			offensivePointsText.setText(offensivePointsTextString);
			offensivePointsText.setTextSize(24);

			String defensivePointsTextString = context.getResources().getText(R.string.defPoints) + ": +" + defensivePoints;
			defensivePointsText.setText(defensivePointsTextString);
			defensivePointsText.setTextSize(24);
		}
	}

	public void setRedMoney(boolean isRed) {
		setRedMoney = isRed;
		postInvalidate();
	}

}