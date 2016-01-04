package com.emmanuelmess.tacticaldefence.game.IO.saving;

import android.content.Context;

import com.emmanuelmess.tacticaldefence.BackupAgent;

import java.io.IOException;
import java.util.Arrays;

import static com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler.DECLINED_SIGN_IN;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler.DEFENSIVE_MOD;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler.GAMEPLAY_TIME;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler.LEVEL_DONE;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler.MONEY;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler.OFFENSIVE_MOD;
import static org.acra.ACRA.getErrorReporter;

/**
 * @author Emmanuel
 *         on 2015-01-15, at 06:26 AM, at 22:27.
 */
public class FileIOMemory {

	public static final boolean OFFENSIVE = true, DEFENSIVE = false;

	private Context context;
	private int level;
	private int money;
	private boolean[] offensiveCastleModificationsMem,
			defensiveCastleModificationsMem;
	private long gamePlayTime;
	private boolean declinedSignIn;

	public FileIOMemory(Context context) throws IOException {
		this.context = context;
		reload();
	}

	public void reload() throws IOException {
		FileIO.readGame(context);

		level = FileIO.level;
		getErrorReporter().putCustomData(LEVEL_DONE, String.valueOf(level));

		money = FileIO.money;
		getErrorReporter().putCustomData(MONEY, String.valueOf(money));

		offensiveCastleModificationsMem = FileIO.offensiveCastleModifications;
		getErrorReporter().putCustomData(OFFENSIVE_MOD, Arrays.toString(offensiveCastleModificationsMem));

		defensiveCastleModificationsMem = FileIO.defensiveCastleModifications;
		getErrorReporter().putCustomData(DEFENSIVE_MOD, Arrays.toString(defensiveCastleModificationsMem));

		gamePlayTime = FileIO.gamePlayTime;
		getErrorReporter().putCustomData(GAMEPLAY_TIME, String.valueOf(gamePlayTime));

		declinedSignIn = FileIO.declinedSignIn;
		getErrorReporter().putCustomData(DECLINED_SIGN_IN, String.valueOf(declinedSignIn));
	}

	public void setCastleModifications(boolean castleModification, int index, boolean type) {
		if (type) {
			offensiveCastleModificationsMem[index] = castleModification;
			getErrorReporter().putCustomData(OFFENSIVE_MOD, Arrays.toString(offensiveCastleModificationsMem));
		} else {
			defensiveCastleModificationsMem[index] = castleModification;
			getErrorReporter().putCustomData(DEFENSIVE_MOD, Arrays.toString(defensiveCastleModificationsMem));
		}
	}

	public boolean[] getOffensiveCastleModifications() {
		return offensiveCastleModificationsMem;
	}

	public boolean[] getDefensiveCastleModifications() {
		return defensiveCastleModificationsMem;
	}

	public int getLevelDone() {
		return level;
	}

	public void setLevelDone(int levelDone) {
		level = levelDone;
		getErrorReporter().putCustomData(LEVEL_DONE, String.valueOf(level));
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
		getErrorReporter().putCustomData(MONEY, String.valueOf(money));
	}

	public long getGamePlayTime() {
		return gamePlayTime;
	}

	public void setGamePlayTime(long gamePlayTime) {
		this.gamePlayTime = gamePlayTime;
		getErrorReporter().putCustomData(GAMEPLAY_TIME, String.valueOf(gamePlayTime));
	}

	public FileIOMemory setDeclinedSignIn(boolean declinedSignIn) {
		this.declinedSignIn = declinedSignIn;
		getErrorReporter().putCustomData(DECLINED_SIGN_IN, String.valueOf(declinedSignIn));
		return this;
	}

	public boolean getDeclinedToSignIn() {
		return declinedSignIn;
	}

	public void saveToFile() throws IOException {
		FileIO.saveGame(level, offensiveCastleModificationsMem, defensiveCastleModificationsMem, money, gamePlayTime, declinedSignIn);
		BackupAgent.requestBackup(context);
	}

}
