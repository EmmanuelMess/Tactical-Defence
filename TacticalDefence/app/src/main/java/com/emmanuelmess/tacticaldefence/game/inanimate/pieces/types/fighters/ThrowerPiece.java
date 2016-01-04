package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.inanimate.Arrow;
import com.emmanuelmess.tacticaldefence.game.inanimate.Helper;
/**
 * @author Emmanuel
 *         on 2015-06-28, at 17:11.
 */
public class ThrowerPiece extends FighterPiece {

	private Chronometer attackChr = new Chronometer(), moveBalista = new Chronometer();
	private float moved = 0;
	private Matrix matrix = new Matrix();
	private Paint paint = new Paint();
	private Bitmap ballista;
	private float angle = 0;
	private boolean up;

	public ThrowerPiece(Context context, Types.TYPES type) {
		super(context, type);

		ballista = BitmapFactory.decodeResource(context.getResources(), R.drawable.thrower_balista);

		arrow.setArrowType(Arrow.TYPES.THROWER);
		FIRE_RANGE += 80;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		angle = pieceToAttack != null? (float) Helper.angle(getCoordinates()[0], getCoordinates()[1], pieceToAttack.getCoordinates()[0], pieceToAttack.getCoordinates()[1])
				:castleToAttack != null? (float) Helper.angle(getCoordinates()[0], getCoordinates()[1], (float) relativeP.x, (float) relativeP.y)
				:0f;

		if (!dead) {
			if (isAttacking() && angle != moved && (pieceToAttack == null || pieceToAttack.getState() != state.DEAD)) {
				if (!moveBalista.hasStarted()) moveBalista.start();

				if (Game.isPaused) moveBalista.pause();
				else if (moveBalista.isPaused()) moveBalista.resume();

				if ((int) moved != (int) angle) {
					moved -= (moved > angle? +1:-1)*(moveBalista.getElapsedTime()/1000f*10);
					if (moved > angle != up) moveBalista.stop();
					up = moved > angle;
				} else moved = angle;

				if (moved > 180 || moved < -180)
					throw new IllegalStateException("Impossible angle: " + moved);
			} else moveBalista.stop();
		}

		matrix.postRotate(moved, ballista.getWidth()/2f, ballista.getHeight()/2f);
		matrix.postTranslate(c[0] - ballista.getWidth()/2f, c[1] - ballista.getHeight()/2f);
		canvas.drawBitmap(ballista, matrix, paint);
		matrix.reset();
	}

	@Override
	public void attack(Canvas canvas) {
		if (!newArrow) super.attack(canvas);
		else if (attackChr.getElapsedTime() >= 200 && moved == angle) {
			super.attack(canvas);
			attackChr.stop();
		}

		if (!attackChr.hasStarted()) attackChr.start();

		if (Game.isPaused) attackChr.pause();
		else if (attackChr.isPaused()) attackChr.resume();
	}

	@Override
	public void move(float x, float y) {
		super.move(x, y);

		attackChr.stop();
	}
}
