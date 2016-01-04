package com.emmanuelmess.tacticaldefence.game.inanimate.castle;

import android.content.Context;

/**
 * @author Emmanuel
 *         on 2015-03-01, at 11:30 AM.
 */
public class CastleUpgrades {

	public static int getPrice(OFFENSIVE_UPGRADES o) {
		switch (o) {
			case TOWERS:
				return 100;
			case ARCHERS:
				return 300;
			case TOWERS_II:
				return 150;

			default:
				throw new IllegalArgumentException();
		}
	}

	@Deprecated
	public static int getPrice(DEFENSIVE_UPGRADES d) {
		switch (d) {
			case WALL:
				return 100;

			case RAMPART:
				return 500;

			default:
				throw new IllegalArgumentException();
		}
	}

	public static int getPrice(DEF_UPGRADES d) {
		switch (d) {
			case WALL:
				return 100;

			case RAMPART:
				return 500;

			default:
				throw new IllegalArgumentException();
		}
	}

	public static int getOffensivePoints(OFFENSIVE_UPGRADES o) {
		switch (o) {
			case TOWERS:
				return 2;
			case ARCHERS:
				return 5;
			case TOWERS_II:
				return 1;

			default:
				throw new IllegalArgumentException();
		}
	}

	@Deprecated
	public static int getDefensivePoints(DEFENSIVE_UPGRADES d) {
		switch (d) {
			case WALL:
				return 100;

			case RAMPART:
				return 500;

			default:
				throw new IllegalArgumentException();
		}
	}

	public static int getDefensivePoints(DEF_UPGRADES d) {
		switch (d) {
			case WALL:
				return 100;

			case RAMPART:
				return 500;

			default:
				throw new IllegalArgumentException();
		}
	}

	public enum OFFENSIVE_UPGRADES {TOWERS, ARCHERS, TOWERS_II}

	@Deprecated
	public enum DEFENSIVE_UPGRADES {
		WALL, MOAT_WATER, RAMPART
	}

	public enum DEF_UPGRADES {WALL, RAMPART}

	public class CastleUpgradesDrawer {

		final Context context;

		public CastleUpgradesDrawer(Context context) {
			this.context = context;
		}
	}

}
