package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.API.Move;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.IO.audio.sonification.Sonification;
import com.emmanuelmess.tacticaldefence.game.inanimate.Arrow;
import com.emmanuelmess.tacticaldefence.game.inanimate.Helper;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleI;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.players.AI;
import com.emmanuelmess.tacticaldefence.game.players.Player;
import com.vividsolutions.jts.geom.Coordinate;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.ACTIONING;
import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @author Emmanuel
 *         2015-03-03, at 11:14 AM.
 */
public class FighterPiece extends Piece {

	protected final Sonification a;
	protected final Arrow arrow;
	private final Chronometer cr = new Chronometer();
	protected Coordinate relativeP;//if point is in castle, it's absolute, if it's in a piece, relative to the piece.
	protected float absX, absY;//used to save the absolute position of a piece
	protected Piece pieceToAttack;
	protected CastleI castleToAttack;
	protected boolean alreadyAttacking;
	protected boolean newArrow = true;
	protected double FIRE_RANGE;
	protected float corex1, corey1;

	public FighterPiece(Context context, Types.TYPES type) {
		super(context, type);

		a = new Sonification(context);
		FIRE_RANGE = getBitmap().getWidth()*1.75f;

		arrow = new Arrow(context);
		arrow.setArrowType(Arrow.TYPES.SIMPLE);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		//a.unloadAll();//TODO put in onUpdate()
	}

	public void setWhomToAttack(float x, float y, @Nullable Piece p) {
		if (p != null) castleToAttack = null;
		pieceToAttack = p;

		if (p != null) {
			setState(ACTIONING);
			relativeP = new Coordinate(x - (pieceToAttack.getCoordinates()[0] - pieceToAttack.width()/2f),
					y - (pieceToAttack.getCoordinates()[1] - pieceToAttack.height()/2f));
		}
	}

	public void setWhomToAttack(float x, float y, CastleI c) {
		if (c != null) pieceToAttack = null;
		castleToAttack = c;

		setState(ACTIONING);

		relativeP = new Coordinate(x, y);
	}

	public void attack(Canvas canvas) {
		if (getState() != DEAD) {
			if (pieceToAttack != null && pieceToAttack.getState() != DEAD
					&& ((alreadyAttacking? 0:getCenter().getCoordinate().distance(pieceToAttack.getCenter().getCoordinate())) <= FIRE_RANGE || alreadyAttacking)) {
				alreadyAttacking = true;
				if (newArrow) {
					absX = (float) (relativeP.x + (pieceToAttack.getCoordinates()[0] - pieceToAttack.getBitmap().getWidth()/2f));
					absY = (float) (relativeP.y + (pieceToAttack.getCoordinates()[1] - pieceToAttack.getBitmap().getHeight()/2f));
					newArrow = false;
				}

				if (attackCore(canvas, absX, absY)) {
					Piece p = (toString().contains("ally")? AI.getPieceGroup():Player.getPieceGroup()).getPieceByCoordinates(absX, absY);

					if (p != null) {
						p.isAttacked(this);
						a.impacted(absX*100/DD.getWidth());
					}
					alreadyAttacking = false;
				}
			} else if (castleToAttack != null && (getCenter().getCoordinate().distance(relativeP) <= FIRE_RANGE || alreadyAttacking)) {
				alreadyAttacking = true;

				if (attackCore(canvas, (float) relativeP.x, (float) relativeP.y)) {
					castleToAttack.isAttacked(Arrow.getArrowDamage(arrow.getArrowType()), this);
					a.impacted((float) (relativeP.x*100/DD.getWidth()));
					alreadyAttacking = false;
				}
			}
		}
	}

	public boolean canAttack() {
		return getState() != DEAD
				&& (toString().contains("ally")? Game.getEnemyCastle():Game.getAllyCastle()).getGeometry().distance(getCenter()) < FIRE_RANGE;
	}

	public Piece getAttacked() {
		return pieceToAttack;
	}

	public boolean isAttacking() {
		return pieceToAttack != null || castleToAttack != null;
	}

	public int getDamageDone() {
		return Arrow.getArrowDamage(arrow.getArrowType());
	}

	public void setArrowType(Arrow.TYPES t) {
		arrow.setArrowType(t);
	}

	protected boolean attackCore(Canvas canvas, float x2, float y2) {// TODO: 2015-10-12 make general function in API
		if (!cr.hasStarted()) {
			cr.start();

			corex1 = this.getCoordinates()[0];
			corey1 = this.getCoordinates()[1];

			a.shoot(corex1*Arrow.getArrowVelocity(arrow.getArrowType())/DD.getWidth());
		}

		if (Game.isPaused) cr.pause();
		else if (cr.isPaused()) cr.resume();

		double d = cr.getElapsedTime()*50f/1000f;

		float[] c = Move.move(corex1, corey1, x2, y2, d);

		if (d >= sqrt(pow(corex1 - x2, 2) + pow(corey1 - y2, 2))) {
			cr.stop();
			newArrow = true;

			return true;
		}
		arrow.drawArrow((float) Helper.angle(corex1, corey1, x2, y2), c[0], c[1], canvas);

		return false;
	}
}
