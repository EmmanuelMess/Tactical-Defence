package com.emmanuelmess.tacticaldefence.game.IO.saving;

import android.content.Context;
import android.util.Log;

import com.emmanuelmess.tacticaldefence.Debug;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.activities.game.GameCastleModifyActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import static com.emmanuelmess.tacticaldefence.BackupAgent.NAME;

/**
 * @author Emmanuel
 *         on 2015-01-08, at 12:45 AM, at 22:27.
 */
class FileIO {

	static int level;
	static int money;
	static boolean[] offensiveCastleModifications;
	static boolean[] defensiveCastleModifications;
	static long gamePlayTime;
	static boolean declinedSignIn;
	private static Context context;

	static void saveGame(int level, boolean[] offensiveCastleModifications, boolean[] defensiveCastleModifications, int money, long gamePlayTime, boolean declinedSignIn) throws IOException {
		Log.v("tacticaldefence.game", "Attempting to save game...");
		FileOutputStream out = context.openFileOutput(NAME, Context.MODE_PRIVATE);
		out.write(parseData(level, offensiveCastleModifications, defensiveCastleModifications, money, gamePlayTime, declinedSignIn).getBytes());
		out.close();
	}

	static void readGame(Context context) throws IOException {
		FileIO.context = context;

		Integer level = -1;
		Integer money = 0;
		boolean[] offensiveCastleModifications = new boolean[GameCastleModifyActivity.NUMBER_OF_OFFENSIVE_MODIFICATIONS];
		boolean[] defensiveCastleModifications = new boolean[GameCastleModifyActivity.NUMBER_OF_DEFENSIVE_MODIFICATIONS];
		Long gamePlayTime = 0l;
		Boolean declinedSingIn = false;

		String d;
		try {

			FileInputStream in = context.openFileInput(NAME);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			for (int i = 0; (d = br.readLine()) != null; i++) {
				switch (i) {
					case 0:
						level = Integer.parseInt(d);
						break;
					case 1:
						money = Integer.parseInt(d);
						break;
					case 2:
					case 3:
						Scanner sc = new Scanner(d);
						sc.useDelimiter(",");
						for (int f = 0; sc.hasNextBoolean(); f++) {
							if (i == 2) {
								offensiveCastleModifications[f] = sc.nextBoolean();
							} else defensiveCastleModifications[f] = sc.nextBoolean();
						}
						break;
					case 4:
						gamePlayTime = Long.parseLong(d);
						break;
					case 5:
						declinedSingIn = Boolean.parseBoolean(d);
				}

			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		FileIO.level = level;
		FileIO.money = money;
		FileIO.offensiveCastleModifications = offensiveCastleModifications;
		FileIO.defensiveCastleModifications = defensiveCastleModifications;
		FileIO.gamePlayTime = gamePlayTime;
		FileIO.declinedSignIn = declinedSingIn;
	}

	private static String parseData(int level, boolean[] offensiveCastleModifications, boolean[] defensiveCastleModifications, int money, long gamePlayTime, boolean declinedSignIn) {
		StringBuilder b = new StringBuilder();

		b.append(level);
		b.append(System.getProperty("line.separator"));

		b.append(money);
		b.append(System.getProperty("line.separator"));

		for (boolean offensiveCastleModification : offensiveCastleModifications) {
			b.append(",");
			b.append(offensiveCastleModification);
		}
		b.append(",");
		b.append(System.getProperty("line.separator"));

		for (boolean defensiveCastleModification : defensiveCastleModifications) {
			b.append(",");
			b.append(defensiveCastleModification);
		}
		b.append(",");
		b.append(System.getProperty("line.separator"));

		b.append(TacticalDefence.isDebug()? Debug.GAMEPLAY_TIME:gamePlayTime);
		b.append(System.getProperty("line.separator"));

		b.append(declinedSignIn);
		return b.toString();
	}

}
