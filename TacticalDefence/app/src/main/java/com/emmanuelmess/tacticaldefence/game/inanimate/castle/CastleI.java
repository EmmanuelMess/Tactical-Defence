package com.emmanuelmess.tacticaldefence.game.inanimate.castle;

import android.graphics.Canvas;

import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;

/**
 * @author Emmanuel
 *         on 2015-03-01, at 01:15 PM, at 22:27.
 */
public interface CastleI {

	void draw(Canvas canvas);

	void attack(Canvas canvas);

	void isAttacked(int damage, Piece p);

}
