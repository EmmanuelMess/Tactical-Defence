package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.types;

import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.Piece;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups.PowerUp;
import com.emmanuelmess.tacticaldefence.game.inanimate.pieces.types.HealerPiece;

/**
 * @author Emmanuel
 *         on 2015-03-06, at 22:39.
 */
public class Life extends PowerUp {

	private int lifeToGive = 100;

	public Life() {
		super();
	}

	public void setTo(Piece p) {
		if (p instanceof HealerPiece) {
			int i = p.getMaxHealth() - p.getHealth();
			p.setHealth(i);
			lifeToGive -= i;
			((HealerPiece) p).setGivableLife(lifeToGive);
		} else {
			p.setHealth(lifeToGive);
		}
		lifeToGive = 0;
	}

}
