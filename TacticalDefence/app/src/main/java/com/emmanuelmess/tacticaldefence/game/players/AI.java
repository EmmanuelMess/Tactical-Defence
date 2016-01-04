package com.emmanuelmess.tacticaldefence.game.players;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.tacticaldefence.Debug;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.inanimate.Helper;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.AICastle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.PlayerCastle;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.PieceGroup;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types.Attack;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types.Life;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types.Shield;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.DistractorPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.HealerPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.FighterPiece;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.Game.getEnemyCastle;
import static com.emmanuelmess.tacticaldefence.game.Game.getLevel;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES.ENEMY_TOWER;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.getPrice;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.ACTIONING;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.MOVING;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.SELECTED;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.UNSELECTED;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp.Types;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp.Types.getPrice;

/**
 * @author Emmanuel
 *         on 2015-01-29, at 08:29 PM.
 */
public class AI {

	private static PieceGroup pieceGroup = new PieceGroup();

	private final int[] POINTS = new int[]{50, 250, 350, 700, 1100, 1550, 2000};
	private final int[] MAX_PIECES_PER_WAVE = new int[]{1, 2, 2, 3, 3, 4, 4};

	private final Context context;
	private final Map<Integer, Float[]> movCoordinates = new TreeMap<>();
	private final Map<Integer, Boolean> isDeadAdded = new TreeMap<>(), avoidCollision = new TreeMap<>();//TODO use Piece instead of Integer
	private final Chronometer waveTimer = new Chronometer();
	private final Rect notSpawnArea = new Rect();
	private AICastle eCastle;
	private int points;
	private int deadEnemy = 0;
	private RectF r;
	private Random ran;
	private PlayerCastle castle;
	private float[] nearestRight = new float[2];

	public AI(Context context, PlayerCastle castle, AICastle eCastle) {
		this.context = context;
		this.castle = castle;
		this.eCastle = eCastle;

		points = getPointsForLevel(getLevel());

		r = new RectF();
		ran = new Random();

		notSpawnArea.set(0, 0, (int) (eCastle.getRectF().left - eCastle.getRectF().width()/2), DD.getHeight());
	}

	public static PieceGroup getPieceGroup() {
		return pieceGroup;
	}

	public void onDraw(Canvas canvas) {
		pieceGroup.drawAlive(canvas);
		eCastle.attack(canvas);
	}

	public void onForegroundDraw(Canvas canvas) {
		pieceGroup.drawAction(canvas);
	}

	public void onUpdate(boolean isPaused) {
		if (!isPaused) {
			if (deadEnemy < Player.getPieceGroup().getAmountDead().size()) {
				for (int i = (deadEnemy == 0? 0:deadEnemy - 1); i < Player.getPieceGroup().getAmountDead().size(); i++) {
					points += getPrice(Player.getPieceGroup().getAmountDead().get(i).getType()) - 5;
				}
				deadEnemy = Player.getPieceGroup().getAmountDead().size();
			}

			for (int i = 0; i < pieceGroup.getAll().length; i++) {
				if (pieceGroup.getAll()[i].getState() == DEAD && (isDeadAdded.get(i) == null || !isDeadAdded.get(i))) {
					Game.drawDeadPiece(pieceGroup.getAll()[i]);
					isDeadAdded.put(i, true);
				}
			}
		}

		if (Player.getAmountPieces() && !(TacticalDefence.isDebug() && Debug.NO_AI)) {
			for (int i = 0; i <= (pieceGroup.getAll().length == 0? 1:pieceGroup.getAll().length - 1); i++) {
				Piece p;
				int price = getPrice(TYPES.ENEMY_TOWER);

				if (points >= price && (pieceGroup.getAmountDead().size() == pieceGroup.getAll().length
						|| !waveTimer.hasStarted() || waveTimer.getElapsedTime() >= 60000)) {
					waveTimer.start();
					for (int g = 0; g < MAX_PIECES_PER_WAVE[getLevel()]; g++) {
						int e;
						boolean damaged = false, pieceAttacked = false;

						for (Piece piece : pieceGroup.getAll()) {
							damaged = piece.getHealth() < piece.getMaxHealth() && piece.getState() != DEAD;
							pieceAttacked = pieceAttacked || piece.isBeingAttacked() != null;
						}

						if (g == 0 && damaged && pieceGroup.getAmountHealers() <= 1) {
							e = addPiece(TYPES.ENEMY_HEALER);
						} else if (g == 0 && pieceAttacked)
							e = addPiece(TYPES.ENEMY_DISTRACTOR);
						else e = addPiece(ENEMY_TOWER);

						movCoordinates.put(e, new Float[]{0f, 0f});
						avoidCollision.put(e, ran.nextBoolean());
					}
				} else if ((p = pieceGroup.getAll()[i]).getState() != DEAD) {
					if (p.getState() == UNSELECTED || p.getState() == MOVING)
						p.setState(SELECTED);

					if (getLevel() >= 3) {
						if (p.getHealth() <= 30 && points >= getPrice(Types.TYPES.SHIELD) + getPrice(p.getType())) {
							new Shield(context).setTo(p);
							points -= getPrice(Types.TYPES.SHIELD);
						} else if (p.getHealth() <= 30 && points >= getPrice(Types.TYPES.HEAL) + getPrice(p.getType())) {
							new Life().setTo(p);
							points -= getPrice(Types.TYPES.HEAL);
						}
					}

					if (p instanceof FighterPiece) {// TODO: 2015-11-02 add support for BallistaPieces
						if (p.isBeingAttacked() != null) {
							setWhomToAttack(p.isBeingAttacked(), ((FighterPiece) p));
						} else {
							if (getEnemies(0, p).length == 0) {
								if (!((FighterPiece) p).canAttack()) {
									toECastle(p);
								} else if (castle.getHP() > 0) {
									((FighterPiece) p).setWhomToAttack(getNearestRight(p)[0], getNearestRight(p)[1], castle);
								}
							} else {
								if (getEnemies(0, p).length <= pieceGroup.getAllUnselected().length + 1) {
									setWhomToAttack(getNearestEnemy(1, p), ((FighterPiece) p));
								} else {
									if (eCastle.getHP() < 20) {
										if (!((FighterPiece) p).canAttack()) {
											toECastle(p);
										} else {
											setWhomToAttack(getNearestEnemy(1, p), ((FighterPiece) p));
										}
									} else {
										if (getEnemies(1, p).length <= pieceGroup.getAllUnselected().length + 1) {
											toECastle(p);
										} else {
											setWhomToAttack(getNearestEnemy(1, p), ((FighterPiece) p));
										}
									}
								}
							}
						}

						if (getLevel() >= 3 && ((FighterPiece) p).isAttacking() && points >= getPrice(Types.TYPES.ATTACK) + getPrice(TYPES.ENEMY_TOWER)) {
							new Attack().setTo(p);
							points -= getPrice(Types.TYPES.ATTACK);
						}
					} else if (p instanceof HealerPiece) {
						if (p.isBeingAttacked() == null && !((HealerPiece) p).isDry()) {
							if (((HealerPiece) p).getWhomToHeal() == null) {
								Piece heal = pieceGroup.getAll()[0];
								for (Piece piece : pieceGroup.getAll()) {
									if ((float) piece.getHealth()/piece.getMaxHealth() < (float) heal.getHealth()/heal.getMaxHealth()) {
										heal = piece;
										break;
									}
								}
								((HealerPiece) p).setWhomToHeal(heal);
							} else if (!((HealerPiece) p).canHeal()) {
								moveTo(p, ((HealerPiece) p).getWhomToHeal().getCoordinates()[0], ((HealerPiece) p).getWhomToHeal().getCoordinates()[1]);
							} else p.setState(ACTIONING);
						} else {
							moveTo(p, getEnemyCastle().getRectF().centerX(), getEnemyCastle().getRectF().centerY());
						}
					} else if (p instanceof DistractorPiece) {
						if (((DistractorPiece) p).getMark() == null) {
							for (Piece piece : pieceGroup.getAll()) {
								if (piece.isBeingAttacked() != null) {
									((DistractorPiece) p).setMark(piece.isBeingAttacked());
									break;
								}
							}
						} else {
							moveTo(p, ((DistractorPiece) p).getMark().getCoordinates()[0], ((DistractorPiece) p).getMark().getCoordinates()[1]);
						}
					}
				}
			}
		}

		if (isPaused && !waveTimer.isPaused()) waveTimer.pause();
		else if (!isPaused && waveTimer.isPaused()) waveTimer.resume();
	}

	private float[] getNearestRight(Piece p) {
		Coordinate c[] = DistanceOp.nearestPoints(Game.getAllyCastle().getGeometry(), p.getCenter());
		nearestRight[0] = (float) c[0].x;
		nearestRight[1] = (float) c[0].y;

		return nearestRight;
	}

	private void setWhomToAttack(Piece enemy, FighterPiece p) {
		if (enemy != null) {// TODO: 2015-10-07 enemy shouldn't be null in any case, but is!
			if (p.getAttacked() != enemy) {
				p.setState(ACTIONING);
				p.setWhomToAttack(enemy.getCoordinates()[0] + (float) ran.nextGaussian()*10, enemy.getCoordinates()[1] + (float) ran.nextGaussian()*10,
						enemy);
			}

			moveTo(p, enemy.getCoordinates()[0], enemy.getCoordinates()[1]);
		}
	}

	private float[][] getEnemies(int t, Piece p) {
		List<float[]> f = new ArrayList<>();

		float left = p.getCoordinates()[0] - p.getBitmap().getWidth()*1.5f;
		float top = p.getCoordinates()[1] - p.getBitmap().getHeight()*1.5f;
		float right = p.getCoordinates()[0] + p.getBitmap().getWidth()*1.5f;
		float bottom = p.getCoordinates()[1] + p.getBitmap().getHeight()*1.5f;
		switch (t) {
			case 0:
				r.set(left, top, right, bottom);
				break;
			case 1:
				r.set(left, top, p.getCoordinates()[0], bottom);
				break;
			case 2:
				r.set(p.getCoordinates()[0], top, right, bottom);
				break;
			default:
				throw new IllegalArgumentException(t + "IS NOT a valid argument!");
		}

		for (Piece piece : Player.getPieceGroup().getAll()) {
			if (piece != null && piece.getState() != DEAD
					&& (r.contains(piece.getCoordinates()[0] - piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] - piece.getBitmap().getHeight()/2)
					|| r.contains(piece.getCoordinates()[0] + piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] - piece.getBitmap().getHeight()/2)
					|| r.contains(piece.getCoordinates()[0] + piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] + piece.getBitmap().getHeight()/2)
					|| r.contains(piece.getCoordinates()[0] - piece.getBitmap().getWidth()/2, piece.getCoordinates()[1] - piece.getBitmap().getHeight()/2)))
				f.add(piece.getCoordinates());
		}

		return f.toArray(new float[f.size()][2]);
	}

	private int addPiece(TYPES type) {
		Piece piece;
		float x, y;

		switch (type) {
			case ENEMY_TOWER:
				piece = new FighterPiece(context, type);
				break;
			case ENEMY_HEALER:
				piece = new HealerPiece(context, type);
				break;
			case ENEMY_DISTRACTOR:
				piece = new DistractorPiece(context, type);
				break;
			default:
				throw new IllegalArgumentException();
		}
		points -= getPrice(type);

		do {
			x = eCastle.getRectF().left - eCastle.getRectF().width()/2f
					+ ran.nextInt((int) (eCastle.getRectF().width()*1.5f - piece.width())) + piece.width()/2f;
			y = ran.nextInt(DD.getHeight() - piece.height()) + piece.height()/2f;

			r.set(x - piece.width()/2, y - piece.height()/2, x + piece.width()/2, y + piece.height()/2);
		}
		while (pieceGroup.getPieceByCoordinates(x, y) != null || notSpawnArea.contains((int) x, (int) y) || piece.testCoordinates(x, y));

		r.setEmpty();
		piece.setCoordinates(x, y);
		return pieceGroup.add(piece);
	}

	private Piece getNearestEnemy(int placeToSearch, Piece p) {
		float[][] e = getEnemies(placeToSearch, p);
		return Helper.getNearestEnemy(e, Player.getPieceGroup(), p.getCoordinates()[0], p.getCoordinates()[1]);
	}

	private void moveTo(Piece p, float x, float y) {
		Float[] c = movCoordinates.get(pieceGroup.getID(p));

		p.setState(MOVING);

		Piece e = Player.getPieceGroup().getPieceByCoordinates(x, y);

		if (!p.testCoordinates(p.getCoordinates()[0] - p.height()/2f, p.getCoordinates()[1])
				|| (p instanceof DistractorPiece && e != null
				&& e.getCenter().distance(p.getCenter()) < p.height()/2 + e.height())) {
			c[0] = p.getCoordinates()[0];
			c[1] = p.getCoordinates()[1];
		} else {
			float[] c2 = tryNewRoute(p, c);
			x = c2[0];
			y = c2[1];
		}

		movCoordinates.put(pieceGroup.getID(p), c);

		p.move(x, y);
	}

	private void toECastle(Piece p) {
		moveTo(p, castle.getRectF().right + p.getBitmap().getWidth()/2f, p.getCoordinates()[1]);
	}

	private float[] tryNewRoute(Piece p, Float[] c) {
		float x2, y2;

		if (!p.testCoordinates(p.getCoordinates()[0], p.getCoordinates()[1] - p.height()/2f) && avoidCollision.get(pieceGroup.getID(p))) {
			x2 = p.getCoordinates()[0];
			y2 = 0;
		} else if (!p.testCoordinates(p.getCoordinates()[0], p.getCoordinates()[1] + p.height()/2f)) {
			if (avoidCollision.get(pieceGroup.getID(p)))
				avoidCollision.put(pieceGroup.getID(p), false);

			x2 = p.getCoordinates()[0];
			y2 = DD.getHeight();
		} else {
			x2 = DD.getWidth();
			y2 = p.getCoordinates()[1];
		}

		movCoordinates.put(pieceGroup.getID(p), c);

		return new float[]{x2, y2};
	}

	private int getPointsForLevel(int level) {
		if (level > POINTS.length)
			throw new IllegalArgumentException("No data for level: " + level);
		else return POINTS[level];
	}

	public void destroy() {
		pieceGroup = new PieceGroup();
	}

	public String mau() {
		return new Mauricio().toString();
	}

	class Mauricio {
		Mauricio() {

		}

		public String toString() {
			return "Mauricio";
		}
	}

}
