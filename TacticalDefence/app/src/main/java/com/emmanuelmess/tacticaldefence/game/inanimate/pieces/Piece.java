package com.emmanuelmess.tacticaldefence.game.inanimate.pieces;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.API.Move;
import com.emmanuelmess.API.Utils;
import com.emmanuelmess.API.twodmensions.DrawHelper;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.Castle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades;
import com.emmanuelmess.tacticaldefence.game.inanimate.defences.Defence;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.FighterPiece;
import com.emmanuelmess.tacticaldefence.game.players.AI;
import com.emmanuelmess.tacticaldefence.game.players.Player;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;

import static android.graphics.BitmapFactory.decodeResource;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.Game.GF;
import static com.emmanuelmess.tacticaldefence.game.Game.defencesA;
import static com.emmanuelmess.tacticaldefence.game.Game.getAllyCastle;
import static com.emmanuelmess.tacticaldefence.game.Game.getEnemyCastle;
import static com.emmanuelmess.tacticaldefence.game.Game.isPaused;
import static com.emmanuelmess.tacticaldefence.game.Game.piecesBitmap;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Emmanuel
 *         on 19/11/14, at 17:55.
 */
public abstract class Piece {

	public static final int STOPPED_NORMALLY = 0, STOPPED_ANORMALLY = 1;
	private final Types.TYPES type;
	private final Bitmap bm;
	private final DrawHelper drawHelper;
	private final float[] coords = new float[]{0, 0, 0, 0};
	private final Chronometer chr = new Chronometer();
	public Map<PowerUp.Types.TYPES, PowerUp> powers = new HashMap<>();
	protected int HP, maxHP, defence = 0, maxDefence = 100;
	protected float[] c;
	protected boolean dead = false;
	private Geometry center;
	private boolean unselected = true;
	private boolean selected = false;
	private boolean moving = false;
	private boolean attack = false;
	private Paint bPaint = new Paint();//only for piece
	private Piece attacking;
	private int stopped = -1;

	protected Piece(Context context, Types.TYPES type) {
		HP = maxHP = 100;
		this.type = type;
		drawHelper = new DrawHelper();
		bm = Types.createImage(context.getResources(), type);
		c = new float[]{bm.getHeight()/2f, DD.getHeight()/2f};
		center = GF.createPoint(new Coordinate(c[0], c[1]));
	}

	public Types.TYPES getType() {
		return type;
	}

	public boolean isBitmapSelectedByCoordinates(float x, float y) {
		return center.distance(GF.createPoint(new Coordinate(x, y))) <= height()/2f;
	}

	public Bitmap getBitmap() {
		return bm;
	}

	public state getState() {
		if (attack) return state.ACTIONING;
		else if (moving) return state.MOVING;
		else if (selected) return state.SELECTED;
		else if (dead) return state.DEAD;
		else if (unselected) return state.UNSELECTED;
		else throw new IllegalStateException();
	}

	public void setState(state i) {
		if (!dead) {
			switch (i) {
				case UNSELECTED:
					unselected = true;
					selected = moving = attack = false;
					break;

				case SELECTED:
					selected = true;
					unselected = moving = attack = false;
					break;

				case MOVING:
					selected = unselected = attack = false;
					moving = true;
					break;

				case ACTIONING:
					selected = moving = unselected = false;
					attack = true;
					break;

				default:
					throw new IllegalArgumentException(i + " is not a valid state for: " + toString());
			}
		} else throw new IllegalArgumentException("Piece is dead");
	}

	public float[] getCoordinates() {
		return c;
	}

	public boolean setCoordinates(float x, float y) {
		if (!testCoordinates(x, y)) {
			c[0] = x;
			c[1] = y;

			center = GF.createPoint(new Coordinate(c[0], c[1]));
			return false;
		} else return true;
	}

	/**
	 * @param x to test
	 * @param y to test
	 * @return boolean true if location is impossible to reach
	 */
	public boolean testCoordinates(float x, float y) {
		if (getState() == state.DEAD) return true;

		if (x - width()/2f < 0 || x + width()/2f > DD.getWidth() || y - height()/2f < 0 || y + height()/2f > DD.getHeight())
			return true;

		Geometry center = (x == c[0] && y == c[1])? this.center:GF.createPoint(new Coordinate(x, y));

		for (Piece p : Utils.merge(Player.getPieceGroup().getAll(), AI.getPieceGroup().getAll()))
			if (p != this && p.getState() != state.DEAD && center.distance(p.getCenter()) < this.height()/2f + p.height()/2f)
				return true;

		for (Defence d : defencesA)
			if (center.distance(d.getGeometry()) < height()/2f)
				return true;

		Geometry r = (toString().contains("ally")? getEnemyCastle():getAllyCastle()).getGeometry();

		return r.distance(center) < height()/2f;
	}

	@Override
	public String toString() {
		switch (type) {
			case ALLY_TOWER:
				return "ally.tower";
			case ENEMY_TOWER:
				return "enemy.tower";
			case ALLY_HEALER:
				return "ally.healer";
			case ENEMY_HEALER:
				return "enemy.healer";
			case ALLY_DISTRACTOR:
				return "ally.distractor";
			case ENEMY_DISTRACTOR:
				return "enemy.distractor";
			case ALLY_THROWER:
				return "ally.thrower";
			case ENEMY_THROWER:
				return "enemy.thrower";
			default:
				throw new IllegalArgumentException("Type is impossible to define");
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(getBitmap(), c[0] - width()/2f, c[1] - height()/2f, bPaint);

		if (!dead) {
			float y, x = getCoordinates()[0] - width()/2f + 10;
			if (getCoordinates()[1] + height()/2f + 10 <= DD.getHeight())
				y = getCoordinates()[1] + height()/2f + 5;
			else y = getCoordinates()[1] - 5 - height()/2f - 5;

			drawHelper.drawHealthBar(x, y, width() - 20, HP*100/maxHP, canvas);

			PowerUp p;
			if ((p = powers.get(PowerUp.Types.TYPES.SHIELD)) != null && defence > 0) {
				canvas.drawBitmap(p.getBitmap(), c[0] - width()/2f, c[1] - height()/2f, null);
				drawHelper.drawHealthBar(x, y, width() - 20, 5, defence*100/maxDefence, Color.TRANSPARENT, Color.YELLOW, canvas);
			}
		}
	}

	public Piece isBeingAttacked() {
		if (attacking != null && attacking.getState() != state.ACTIONING) attacking = null;
		return attacking;
	}

	public void isAttacked(FighterPiece p) {
		attacking = p;
		if (!dead) {
			int f = minIsZero(p.getDamageDone() - defence);
			defence = minIsZero(defence - p.getDamageDone());
			HP = HP - f;
		}
		if (HP <= 0) setDead();
	}

	public void isAttacked(Castle c, CastleUpgrades.OFFENSIVE_UPGRADES t) {
		if (!dead) {
			int f = minIsZero(c.getDamage(t) - defence);
			defence = minIsZero(defence - c.getDamage(t));
			HP = HP - f;
		}
		if (HP <= 0) setDead();
	}

	public Geometry getCenter() {
		return center;
	}

	private void setDead() {
		dead = unselected = true;
		selected = moving = attack = false;
		bPaint.setAlpha(225);
	}

	private int getMaxVelocity() {
		switch (type) {
			case ALLY_TOWER:
			case ENEMY_TOWER:
			case ALLY_HEALER:
			case ENEMY_HEALER:
			case ALLY_DISTRACTOR:
			case ENEMY_DISTRACTOR:
				return 15;
			case ALLY_THROWER:
			case ENEMY_THROWER:
				return 10;

			default:
				throw new IllegalStateException("Illegal piece type: " + type + " !");
		}
	}

	public int height() {
		return getBitmap().getHeight();
	}

	public int width() {
		return getBitmap().getWidth();
	}

	public int getHealth() {
		return HP;
	}

	public boolean setHealth(int health) {
		HP += health;
		HP = HP > maxHP? maxHP:HP;
		return HP == maxHP;
	}

	public int getMaxHealth() {
		return maxHP;
	}

	public boolean setDefence(int d) {
		defence += d;
		defence = defence > maxDefence? maxDefence:defence;
		return defence == maxDefence;
	}

	public int getDefence() {
		return defence;
	}

	public int getMaxDefence() {
		return maxDefence;
	}

	public void move(float x, float y) {
		if (!chr.hasStarted() || x != coords[2] || y != coords[3]) {
			stopped = -1;
			chr.start();
			coords[0] = getCoordinates()[0];
			coords[1] = getCoordinates()[1];
			coords[2] = x;
			coords[3] = y;
		}

		if (isPaused) chr.pause();
		else if (chr.isPaused()) chr.resume();

		double d = chr.getElapsedTime()*getMaxVelocity()/1000f;

		float[] m = Move.move(coords[0], coords[1], coords[2], coords[3], d);

		if (d >= sqrt(pow(coords[2] - coords[0], 2) + pow(coords[3] - coords[1], 2))) {
			stopped = STOPPED_NORMALLY;
			setCoordinates(coords[2], coords[3]);
			chr.stop();
		} else if (testCoordinates(m[0], m[1])) {
			stopped = STOPPED_ANORMALLY;
			chr.stop();
		} else setCoordinates(m[0], m[1]);
	}

	public void stop() {
		chr.stop();
		stopped = 0;
	}

	public int stopped() {
		return stopped;
	}

	public void setPowerUp(PowerUp.Types.TYPES t, PowerUp pu) {
		powers.put(t, pu);
	}

	public PowerUp getPowerUp(PowerUp.Types.TYPES t) {
		return powers.get(t);
	}

	private int minIsZero(int r) {
		return r > 0? r:0;
	}

	public enum state {DEAD, UNSELECTED, SELECTED, MOVING, ACTIONING}

	public static class Types {

		private static final int[] imgI = {R.drawable.ally_tower, R.drawable.enemy_tower, R.drawable.ally_healer, R.drawable.enemy_healer,
				R.drawable.ally_distractor, R.drawable.enemy_distractor, R.drawable.ally_thrower, R.drawable.enemy_thrower};

		public static Bitmap createImage(Resources res, TYPES type) throws IllegalArgumentException {
			if (piecesBitmap == null) piecesBitmap = new Bitmap[TYPES.values().length];
			if (piecesBitmap[type.ordinal()] == null)
				piecesBitmap[type.ordinal()] = decodeResource(res, imgI[type.ordinal()]);

			return piecesBitmap[type.ordinal()];
		}

		public static int getPrice(Types.TYPES type) {
			switch (type) {
				case ALLY_TOWER:
				case ENEMY_TOWER:
				case ALLY_HEALER:
				case ENEMY_HEALER:
					return 50;
				case ALLY_DISTRACTOR:
				case ENEMY_DISTRACTOR:
					return 75;
				case ALLY_THROWER:
				case ENEMY_THROWER:
					return 125;

				default:
					throw new IllegalArgumentException("non-existent price for:  " + type.toString());
			}
		}

		public enum TYPES {ALLY_TOWER, ENEMY_TOWER, ALLY_HEALER, ENEMY_HEALER, ALLY_DISTRACTOR, ENEMY_DISTRACTOR, ALLY_THROWER, ENEMY_THROWER}

	}

}
