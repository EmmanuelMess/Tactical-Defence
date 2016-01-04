package com.emmanuelmess.tacticaldefence.game.inanimate.castle.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.Castle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleI;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;

/**
 * @author Emmanuel
 *         on 2014-12-15, at 07:01 AM.
 */
public class PlayerCastle extends Castle implements CastleI {

	private final DrawHelper dH;
	private final RectF[] allyCastle = new RectF[2];
	private final boolean[] offensiveCastleModify, defensiveCastleModify;
	private Canvas canvas;
	private Bitmap drawableCastle;

	public PlayerCastle(Context context, FileIOMemory f, boolean noHitbox) {
		super(context);
		dH = new DrawHelper();
		/*Bitmaps*/
		allyCastle[0] = new RectF(0, DD.getHeight()/5f, (DD.getWidth()/10f)*2f, (DD.getHeight()/5f)*4f);
		float tower1 = allyCastle[0].height()/3f;
		allyCastle[1] = new RectF(0, tower1/4f, DD.getWidth()/2f - tower1, DD.getHeight() - tower1/4f);
		offensiveCastleModify = f.getOffensiveCastleModifications();
		defensiveCastleModify = f.getDefensiveCastleModifications();

		drawableCastle = create(noHitbox, true, offensiveCastleModify, defensiveCastleModify);

		/*Health*/
		int mem = 0;
		for (int i = 0; i < defensiveCastleModify.length; i++)
			if (defensiveCastleModify[i])
				mem += CastleUpgrades.getDefensivePoints(CastleUpgrades.DEFENSIVE_UPGRADES.values()[i]);
		maxHP = 1000 + mem;
		hp = maxHP;
	}

	@Override
	public void draw(Canvas canvas) {
		this.canvas = canvas;

		canvas.drawBitmap(drawableCastle, 0, 0, null);
	}

	@Override
	public void attack(Canvas canvas) {
		super.attack(canvas, offensiveCastleModify, true);
	}

	@Override
	public void isAttacked(int damage, Piece p) {
		hp -= damage;
		if (hp <= 0) Game.lost = true;
	}

	public void drawLife() {
		if (canvas == null)
			throw new NullPointerException("Create the castle before it's life bar");

		float len = (allyCastle[0].bottom - allyCastle[0].top - 20)*(!defensiveCastleModify[2]? 1:0.6f);

		dH.setAngle(DrawHelper.RECT_ANGLE);
		dH.drawHealthBar(5, DD.getHeight()/2f - len/2f, (int) len, hp*100/maxHP, canvas);

	}

	public RectF getRectF() {
		return allyCastle[0];
	}

	public int getHP() {
		return hp;
	}

}
