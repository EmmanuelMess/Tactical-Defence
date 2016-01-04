package com.emmanuelmess.tacticaldefence.game.inanimate.pieces.powerups;

import android.graphics.Bitmap;
/**
 * @author Emmanuel
 *         on 2015-03-05, at 04:56 PM.
 */
public abstract class PowerUp {

	public PowerUp() {

	}

	public Bitmap getBitmap() {
		return null;
	}

	public static class Types {
		public static int getPrice(TYPES type) {
			switch (type) {
				case HEAL:
					return 25;

				case SHIELD:
				case ATTACK:
					return 50;

				default:
					throw new IllegalArgumentException("");
			}
		}

		public enum TYPES {HEAL, SHIELD, ATTACK}
	}

}
