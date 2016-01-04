package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp;
/**
 * @author Emmanuel
 *         on 2015-05-30, at 22:36.
 */
public class Shield extends PowerUp {

	private Bitmap defence;

	public Shield(Context context) {
		super();

		defence = BitmapFactory.decodeResource(context.getResources(), R.drawable.piece_defencehalo);
	}

	public void setTo(Piece p) {
		p.setPowerUp(Types.TYPES.SHIELD, this);
		p.setDefence(100);
	}

	@Override
	public Bitmap getBitmap() {
		return defence;
	}

}
