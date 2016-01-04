package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types;

import com.emmanuelmess.tacticaldefence.game.inanimate.Arrow;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.FighterPiece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.fighters.ThrowerPiece;

/**
 * @author Emmanuel
 *         on 2015-05-30, at 22:36.
 */
public class Attack extends PowerUp {

	public Attack() {

	}

	public void setTo(Piece p) {
		if (p instanceof FighterPiece) {
			((FighterPiece) p).setArrowType(!(p instanceof ThrowerPiece)? Arrow.TYPES.ENHANCED:Arrow.TYPES.TROWER_ENHANCED);
		}
	}

}
