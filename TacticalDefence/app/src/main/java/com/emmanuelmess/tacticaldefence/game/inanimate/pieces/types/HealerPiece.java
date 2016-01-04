package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.IO.audio.sonification.Sonification;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Emmanuel
 *         on 2015-03-03, at 10:55 AM.
 */
public class HealerPiece extends Piece {

	private final Chronometer chr = new Chronometer();
	private final int maxHPtoGive;
	private final int rate;
	private final DrawHelper dH;
	private final Sonification a;
	private Piece whomToHeal = null;
	private int HPtoGive;
	private boolean isHealing, beat;
	private Bitmap heal;
	private Paint paint = new Paint();

	public HealerPiece(Context context, Types.TYPES type) {
		super(context, type);

		dH = new DrawHelper();
		a = new Sonification(context);
		heal = BitmapFactory.decodeResource(context.getResources(), R.drawable.anim_heal);

		maxHPtoGive = 100;
		HPtoGive = 100;
		rate = 10;
	}

	public boolean heal() {
		if (canHeal()) {
			isHealing = true;
			if (!chr.hasStarted()) chr.start();

			if (chr.getElapsedTime() >= (1000/rate)) {
				chr.stop();
				a.heal(getCoordinates()[0]*100/DD.getWidth());
				HPtoGive -= rate;

				chr.start();
				return whomToHeal.setHealth(rate);
			}


		} else {
			isHealing = false;
			whomToHeal = null;
		}
		return false;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (getState() != DEAD)
			dH.drawHealthBar(getCoordinates()[0] - getBitmap().getWidth()/2 + 10, getCoordinates()[1] + getBitmap().getHeight()/2 + 10, getBitmap().getWidth() - 20, 5,
					HPtoGive*100/maxHPtoGive, Color.RED, Color.BLUE, canvas);

		if (isHealing) {
			paint.setARGB(255, 34, 139, 34);
			paint.setStrokeWidth(beat? 1:3);
			canvas.drawLine(getCoordinates()[0], getCoordinates()[1], whomToHeal.getCoordinates()[0], whomToHeal.getCoordinates()[1], paint);
			canvas.drawBitmap(heal, (getCoordinates()[0] + whomToHeal.getCoordinates()[0])/2f - heal.getWidth()/2f,
					(getCoordinates()[1] + whomToHeal.getCoordinates()[1])/2f - heal.getHeight()/2f, null);
			beat = !beat;
		}
	}

	public boolean canHeal() {
		if (whomToHeal != null && HPtoGive > 0 && whomToHeal.getHealth() < whomToHeal.getMaxHealth()
				&& whomToHeal.getState() != Piece.state.DEAD && this.getState() != Piece.state.DEAD) {
			double distanceToPiece = sqrt(pow(whomToHeal.getCoordinates()[0] - this.getCoordinates()[0], 2) +
					pow(whomToHeal.getCoordinates()[1] - this.getCoordinates()[1], 2));
			if (distanceToPiece <= height()*1.5)
				return true;
		}
		return false;
	}

	public Piece getWhomToHeal() {
		return whomToHeal;
	}

	public void setWhomToHeal(@Nullable Piece p) {
		whomToHeal = p;
	}

	public boolean isDry() {
		return HPtoGive <= 0;
	}

	public int getGivableLife() {
		return HPtoGive;
	}

	public void setGivableLife(int givableLife) {
		this.HPtoGive = givableLife;
	}

}
