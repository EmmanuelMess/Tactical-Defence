package com.emmanuelmess.tacticaldefence.game.inanimate.castle.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.Castle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleI;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;

/**
 * @author Emmanuel
 *         2014-12-15, at 07:34 AM.
 */
public class AICastle extends Castle implements CastleI {

	private final DrawHelper dH;
	private Canvas canvas;
	private boolean[] offensiveConf;
	private RectF enemyCastle;
	private int level;
	private Bitmap[] castles;
	private Bitmap castle;

	public AICastle(Context context, boolean noHitbox) {
		super(context);
		dH = new DrawHelper();

		if (level >= 2)
			maxHP = 1000 + CastleUpgrades.getDefensivePoints(CastleUpgrades.DEFENSIVE_UPGRADES.values()[0]);
		else maxHP = 1000;
		hp = maxHP;

		if (noHitbox) {
			castles = new Bitmap[GameMenuActivity.MAX_LEVEL + 1];//+1 because of tutorial being level 0
			for (int i = 0; i < castles.length; i++) {
				castles[i] = create(true, false, getLevelDef(i)[0], getLevelDef(i)[1]);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		this.canvas = canvas;

		canvas.drawBitmap(castle, DD.getWidth() - castle.getWidth(), 0, null);
	}

	@Override
	public void attack(Canvas canvas) {
		super.attack(canvas, offensiveConf, false);
	}

	@Override
	public void isAttacked(int damage, Piece p) {
		hp -= damage;
		if (hp <= 0) Game.won = true;
	}

	public void drawLife() {
		if (canvas == null)
			throw new NullPointerException("Create the castle before it's life bar");

		float len = (enemyCastle.bottom - enemyCastle.top - 20)*(level < 6? 1:0.6f);

		dH.setAngle(DrawHelper.RECT_ANGLE);
		dH.drawHealthBar(DD.getWidth() - 10, DD.getHeight()/2f - len/2f, (int) len, hp*100/maxHP, canvas);
	}

	public RectF getRectF() {
		return enemyCastle;
	}

	public int getHP() {
		return hp;
	}

	public void setLevel(int level) {
		this.level = level;

		enemyCastle = new RectF(DD.getWidth() - ((DD.getWidth()/10f)*2f), DD.getHeight()/5f, DD.getWidth(), (DD.getHeight()/5f)*4f);

		offensiveConf = getLevelDef(level)[0];

		if (castles != null) castle = castles[level];
		else castle = create(false, false, getLevelDef(level)[0], getLevelDef(level)[1]);
	}

	private boolean[][] getLevelDef(int level) {
		boolean[] def = new boolean[CastleUpgrades.DEFENSIVE_UPGRADES.values().length],
				off = new boolean[CastleUpgrades.OFFENSIVE_UPGRADES.values().length];
		switch (level) {
			case 0:
				break;
			case 1:
				off[0] = true;
				break;
			case 2:
				off[0] = def[0] = true;
				break;
			case 3:
			case 4:
				off[0] = off[1] = def[0] = true;
				break;
			case 5:
				off[0] = off[1] = off[2] = def[0] = true;
				break;
			case 6:
				off[0] = off[1] = off[2] = def[0] = def[2] = true;
				break;
			default:
				throw new IllegalArgumentException("Non-existent level!");
		}

		return new boolean[][]{off, def};
	}

	/*TODO
	public Bitmap[][] load(int level) {
		int y = (int) (rects[0][0].height() - DD.getHeight()/6f), height = (int) (DD.getHeight()/6f);


		List<Bitmap> top = new ArrayList<>();
		{
			top.aDD(Bitmap.createBitmap(castle[0], 0, y, castle[0].getWidth(), height));
			if (level > 0) {
				top.aDD(castle[1]);
				if (level > 1) {
					top.aDD(Bitmap.createBitmap(castle[2], 0, y - 25, castle[2].getWidth(), height + 25));
					top.aDD(castle[3]);
				}
			}
		}
		List<Bitmap> miDDle = new ArrayList<>();
		{
			miDDle.aDDAll(Arrays.asList(castle).subList(0, level));
		}
		List<Bitmap> bottom = new ArrayList<>();
		{
			bottom.aDD(Bitmap.createBitmap(castle[0], 0, 0, castle[0].getWidth(), height));
			if (level > 0) {
				bottom.aDD(castle[1]);
				if (level > 1) {
					bottom.aDD(Bitmap.createBitmap(castle[2], 0, 0, castle[2].getWidth(), height + 25));
					bottom.aDD(castle[3]);
				}
			}
		}

		return new Bitmap[][]{top.toArray(new Bitmap[top.size()]), miDDle.toArray(new Bitmap[miDDle.size()]), bottom.toArray(new Bitmap[bottom.size()])};
	}
	*/

}
