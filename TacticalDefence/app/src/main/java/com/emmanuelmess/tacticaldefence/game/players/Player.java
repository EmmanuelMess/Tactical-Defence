package com.emmanuelmess.tacticaldefence.game.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.API.Utils;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.activities.game.GameActivity;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.inanimate.defences.Defence;
import com.emmanuelmess.tacticaldefence.game.inanimate.defences.types.BarricadeDefence;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.PieceGroup;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types.Attack;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types.Life;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types.Shield;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.DistractorPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.HealerPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.FighterPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.ThrowerPiece;
import com.google.android.gms.games.Games;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.Map;
import java.util.TreeMap;

import static com.emmanuelmess.API.twodmensions.Contain.isContainedInRectF;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.Game.SHOW.ATTACK;
import static com.emmanuelmess.tacticaldefence.game.Game.SHOW.HEAL;
import static com.emmanuelmess.tacticaldefence.game.Game.SHOW.NONE;
import static com.emmanuelmess.tacticaldefence.game.Game.actionR;
import static com.emmanuelmess.tacticaldefence.game.Game.drawDeadPiece;
import static com.emmanuelmess.tacticaldefence.game.Game.frame;
import static com.emmanuelmess.tacticaldefence.game.Game.getAllyCastle;
import static com.emmanuelmess.tacticaldefence.game.Game.getEnemyCastle;
import static com.emmanuelmess.tacticaldefence.game.Game.isAction;
import static com.emmanuelmess.tacticaldefence.game.Game.isMove;
import static com.emmanuelmess.tacticaldefence.game.Game.money;
import static com.emmanuelmess.tacticaldefence.game.Game.moveR;
import static com.emmanuelmess.tacticaldefence.game.Game.setWhatToShow;
import static com.emmanuelmess.tacticaldefence.game.Game.showMoveAction;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES.ALLY_DISTRACTOR;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES.ALLY_HEALER;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES.ALLY_THROWER;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.TYPES.ALLY_TOWER;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.Types.getPrice;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.ACTIONING;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.MOVING;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.SELECTED;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.UNSELECTED;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp.Types;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Emmanuel
 *         on 2015-01-30, at 03:16 PM.
 */
public class Player {

	private static final int[] POINTS = new int[]{150, 200, 350, 425, 600, 750, 1000};
	private static PieceGroup pieceGroup = new PieceGroup();
	private final Context context;
	private final Bitmap movementLandmark;
	private final Paint paint = new Paint();
	private final Map<Integer, Boolean> isDeadAdded = new TreeMap<>();
	private final RectF notSpawnArea = new RectF(), notPlaceDefArea = new RectF();
	private final Chronometer notSpawn = new Chronometer(), notPlaceDef = new Chronometer();
	private final float[] moving = new float[2];
	private Piece selected;
	private boolean drawMovementLandmark = false;
	private int deadEnemy = 0;

	public Player(Context context) {
		this.context = context;

		movementLandmark = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_game_movement_landmark);

		notSpawnArea.set(getAllyCastle().getRectF().right + getAllyCastle().getRectF().width()/2f, 0, DD.getWidth(), DD.getHeight());
		notPlaceDefArea.set(DD.getWidth()/2f, 0, DD.getWidth(), DD.getHeight());
	}

	public static boolean getAmountPieces() {
		for (Piece p : pieceGroup.getAll())
			if (p != null) return true;
		return false;
	}

	public static PieceGroup getPieceGroup() {
		return pieceGroup;
	}

	public static int getPointsForLevel(int level) {
		if (level > POINTS.length)
			throw new IllegalArgumentException("No data for level: " + level);
		else return POINTS[level];
	}

	public void onDraw(Canvas canvas) {
		pieceGroup.drawAlive(canvas);

		if (pieceGroup.getSelected() != -1) {
			if (drawMovementLandmark) {
				canvas.drawBitmap(movementLandmark, moving[0], moving[1] - movementLandmark.getHeight(), null);
			}

			if (selected != null && selected.getState() != DEAD) {
				paint.setColor(Color.BLUE);
				canvas.drawCircle(pieceGroup.getPieceByIdentifier(pieceGroup.getSelected()).getCoordinates()[0],
						pieceGroup.getPieceByIdentifier(pieceGroup.getSelected()).getCoordinates()[1], 5.0f, paint);
				paint.setColor(Color.BLACK);
				canvas.drawCircle(pieceGroup.getPieceByIdentifier(pieceGroup.getSelected()).getCoordinates()[0],
						pieceGroup.getPieceByIdentifier(pieceGroup.getSelected()).getCoordinates()[1], 2.0f, paint);
			}
		}

		getAllyCastle().attack(canvas);
	}

	public void onForegroundDraw(Canvas canvas) {
		pieceGroup.drawAction(canvas);

		if (notPlaceDef.hasStarted()) {
			if (notPlaceDef.getElapsedTime() <= 250) {
				paint.setColor(Color.RED);
				canvas.drawRect(notPlaceDefArea, paint);
			} else notPlaceDef.stop();
		}

		if (notSpawn.hasStarted()) {
			if (notSpawn.getElapsedTime() <= 250) {
				paint.setColor(Color.RED);
				canvas.drawRect(notSpawnArea, paint);
			} else notSpawn.stop();
		}
	}

	public void onUpdate(boolean isPaused) {
		if (!isPaused) {
			if (pieceGroup.getAmountDead().size() >= pieceGroup.getAll().length)
				showMoveAction = isAction = isMove = drawMovementLandmark = false;

			if (deadEnemy < AI.getPieceGroup().getAmountDead().size()) {
				for (int i = (deadEnemy == 0? 0:deadEnemy - 1); i < AI.getPieceGroup().getAmountDead().size(); i++) {
					Game.money += getPrice(AI.getPieceGroup().getAmountDead().get(i).getType()) - 5;
				}
				deadEnemy = AI.getPieceGroup().getAmountDead().size();
			}

			for (int i = 0; i < pieceGroup.getAll().length; i++) {
				if (pieceGroup.getAll()[i].getState() == DEAD && (isDeadAdded.get(i) == null || !isDeadAdded.get(i))) {
					drawDeadPiece(pieceGroup.getAll()[i]);
					isDeadAdded.put(i, true);
				}
			}
		}

		if (getSelected() != null) {
			if (getSelected().getState() == DEAD)
				showMoveAction = drawMovementLandmark = false;

			if (isMove && getSelected().getState() == MOVING) {
				getSelected().move(moving[0], moving[1]);

				if (getSelected().stopped() != -1) {
					pieceGroup.getPieceByIdentifier(pieceGroup.getSelected()).setState(SELECTED);
					drawMovementLandmark = false;
				}
			}
		}

		if (isPaused) {
			if (!notSpawn.isPaused()) notSpawn.pause();
			if (!notPlaceDef.isPaused()) notPlaceDef.pause();
		} else {
			if (notSpawn.isPaused()) notSpawn.resume();
			if (notPlaceDef.isPaused()) notPlaceDef.resume();
		}
	}

	@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		Geometry eventPoint = new GeometryFactory().createPoint(new Coordinate(event.getX(), event.getY()));

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (frame != null && !isContainedInRectF(frame, event)) {
				if (Game.selected != -1) {
					int v = Game.selected%10;
					if (Game.selected < 20) {
						if (!notSpawnArea.contains(x, y)) {
							if (getSelected() != null) getSelected().stop();

							Piece piece;

							switch (Game.selected) {
								case 10:
									piece = new FighterPiece(context, ALLY_TOWER);
									break;
								case 11:
									piece = new HealerPiece(context, ALLY_HEALER);
									break;
								case 12:
									piece = new DistractorPiece(context, ALLY_DISTRACTOR);
									break;
								case 13:
									piece = new ThrowerPiece(context, ALLY_THROWER);
									break;

								default:
									throw new IllegalArgumentException("Unlisted piece: " + Game.selected);
							}

							drawMovementLandmark = false;

							money -= getPrice(Piece.Types.TYPES.values()[v]);
							pieceGroup.setAllUnselected();
							putPiece(piece, x, y);
							int i = pieceGroup.add(piece);
							pieceGroup.getPieceByIdentifier(i).setState(SELECTED);
							this.selected = piece;
							Game.selected = -1;
							showMoveAction = true;
							Game.isAction = Game.isMove = false;
						} else notSpawn.start();
					} else if (Game.selected >= 30) {
						if (!notPlaceDefArea.contains(x, y) || !getAllyCastle().getRectF().contains(x, y)) {
							switch (Game.selected) {
								case 30:
									Game.addDefence(new BarricadeDefence(context, x, y));
									break;

								default:
									throw new IllegalArgumentException("Unlisted defence!");
							}

							money -= Defence.Types.getPrice(Defence.Types.TYPES.values()[v]);
							Game.selected = -1;
							Game.isAction = Game.isMove = false;
						} else notPlaceDef.start();
					} else if (Game.selected >= 20) {
						if (pieceGroup.getPieceByCoordinates(x, y) != null) {
							boolean notNecessary = false;
							Piece p = pieceGroup.getPieceByCoordinates(x, y);

							switch (Game.selected) {
								case 20:
									if(p.getHealth() < p.getMaxHealth()) new Life().setTo(p);
									else notNecessary = true;
									break;
								case 21:
									if(p.getDefence() < p.getMaxDefence()) new Shield(context).setTo(p);
									else notNecessary = true;
									break;
								case 22:
									if(p.getPowerUp(Types.TYPES.ATTACK) != null)new Attack().setTo(p);
									else notNecessary = true;
									break;

								default:
									throw new IllegalArgumentException("Unlisted powerup: " + Game.selected);
							}

							if(!notNecessary) {
								money -= Types.getPrice(Types.TYPES.values()[v]);
								Games.Achievements.incrementImmediate(((TacticalDefence) ((GameActivity) context).getApplication()).getGoogleApi(), context.getString(R.string.achievement_20_powerups), 1);
								Game.selected = -1;
							}
						}
					}
				}
				if (pieceGroup.getPieceByCoordinates(x, y) != null && pieceGroup.getPieceByCoordinates(x, y).getState() != DEAD) {
					drawMovementLandmark = false;
					if (Game.isAction && getSelected() instanceof HealerPiece) {
						if (!((HealerPiece) getSelected()).isDry()) {
							getSelected().stop();

							((HealerPiece) getSelected()).setWhomToHeal(pieceGroup.getPieceByCoordinates(x, y));
							getSelected().setState(ACTIONING);
						} //else TODO DRY WARNING
					} else {
						getSelected().stop();

						if (getSelected().getState() == MOVING)
							getSelected().setState(UNSELECTED);

						if (pieceGroup.getPieceByCoordinates(x, y).getState() == UNSELECTED || pieceGroup.getPieceByCoordinates(x, y).getState() == ACTIONING)
							pieceGroup.setSelected(pieceGroup.getID(pieceGroup.getPieceByCoordinates(x, y)));

						if (getSelected() instanceof HealerPiece) setWhatToShow(HEAL);
						else if (getSelected() instanceof DistractorPiece) setWhatToShow(NONE);
						else setWhatToShow(ATTACK);

						showMoveAction = true;
						this.selected = pieceGroup.getPieceByCoordinates(x, y);
					}
				} else if (pieceGroup.getSelected() != -1) {
					if (isMove && !isContainedInRectF(frame, event) && !isContainedInRectF(actionR, event) && !isContainedInRectF(moveR, event)
							&& !getEnemyCastle().getPreparedGeometry().contains(eventPoint)) {
						getSelected().stop();

						moving[0] = x;
						moving[1] = y;
						drawMovementLandmark = true;
						getSelected().setState(MOVING);
					} else if (isAction && getSelected() instanceof FighterPiece) {
						drawMovementLandmark = false;
						getSelected().stop();

						if (AI.getPieceGroup().getPieceByCoordinates(x, y) != null)
							((FighterPiece) getSelected()).setWhomToAttack(x, y, AI.getPieceGroup().getPieceByCoordinates(x, y));
						else if (getEnemyCastle().getPreparedGeometry().contains(eventPoint))
							((FighterPiece) getSelected()).setWhomToAttack(x, y, getEnemyCastle());

						getSelected().setState(ACTIONING);
					}
				}
			}
		}
		return true;
	}

	private void putPiece(Piece p, float x, float y) {
		if(x < p.height()/2f) x = p.height()/2f;
		y = (float) Utils.clamp(p.height()/2f, DD.getHeight() - p.height()/2f, y);

		if (p.testCoordinates(x, y)) {
			Piece piece = getPieceGroup().getPieceByCoordinates(x, y);
			float dAB = (float) sqrt(pow((piece.getCoordinates()[0] - x), 2) + pow((piece.getCoordinates()[1] - y), 2));
			float xC = piece.getCoordinates()[0] + (x-piece.getCoordinates()[0]) * p.height()/dAB;
			float yC = piece.getCoordinates()[1] + (y-piece.getCoordinates()[1]) * p.height()/dAB;
			p.setCoordinates(xC, yC);
		} else p.setCoordinates(x, y);
	}

	public void destroy() {
		pieceGroup = new PieceGroup();
	}

	private Piece getSelected() {
		return pieceGroup.getPieceByIdentifier(pieceGroup.getSelected());
	}

}
