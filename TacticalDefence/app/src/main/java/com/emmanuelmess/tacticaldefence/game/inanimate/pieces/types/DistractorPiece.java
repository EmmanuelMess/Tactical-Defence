package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;

import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;

import static com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece.state.DEAD;

/**
 * @author Emmanuel
 *         on 2015-04-17, at 18:08.
 */
public class DistractorPiece extends Piece {

	private Piece marked = null;

	public DistractorPiece(Context context, Types.TYPES type) {
		super(context, type);

		this.maxHP = this.HP = 300;
	}


	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		//a.unloadAll();//TODO put in onUpdate()
	}

	public Piece getMark() {
		return marked;
	}

	public void setMark(@Nullable Piece p) {
		if (p.getState() != DEAD) marked = p;
	}

}
