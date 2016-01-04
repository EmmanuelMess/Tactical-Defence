package com.emmanuelmess.tacticaldefence;

import android.app.Application;
import android.util.Log;

import com.emmanuelmess.API.DeviceDimensions;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades;
import com.google.android.gms.common.api.GoogleApiClient;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.IOException;

/**
 * @author Emmanuel
 *         on 2015-03-08, at 02:44 PM, at 18:30.
 */
@ReportsCrashes(mailTo = "emmanuelbendavid@gmail.com",
		mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.ups0)
public class TacticalDefence extends Application {

	public static DeviceDimensions DD;
	private static boolean DEBUG;
	public static int REQUEST_ACHIEVEMENTS = 0;
	private final String TAG = this.getClass().getSimpleName();
	private GoogleApiClient mGoogleApiClient;

	public static boolean isDebug() {
		return DEBUG;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.v(TAG, "Made by EmmanuelMess, enjoy!");
		Log.i(TAG, "Loading...");

		ACRA.init(this);
		Log.i(TAG, "ACRA initialized!");

		DEBUG = android.os.Debug.isDebuggerConnected();
		Log.i(TAG, "Debug data initialized!");

		try {
			FileIOMemory a = new FileIOMemory(getApplicationContext());
			if (a.getDefensiveCastleModifications()[1]) {
				a.setCastleModifications(false, 1, false);
				a.setMoney(a.getMoney() + CastleUpgrades.getPrice(CastleUpgrades.DEFENSIVE_UPGRADES.MOAT_WATER));
			}
			Log.i(TAG, "Content updated!");
		} catch (IOException e) {
			Log.i(TAG, "Error updating content!");
			ACRA.getErrorReporter().handleException(e);
		}

		DD = new DeviceDimensions(getApplicationContext());
		Log.i(TAG, "Initialized variables!");
		Log.i(TAG, "Done!");
	}

	/*
	@Override
	public void onTerminate() {// TODO: 2015-12-16 doesn't work!
		if(getGoogleApi() != null)
			getGoogleApi().disconnect();
		super.onTerminate();
	}
*/

	public void setGoogleApi(GoogleApiClient API) {
		mGoogleApiClient = API;
	}

	/**
	 * @return the object if user accepted to connect, null if not
	 */
	public GoogleApiClient getGoogleApi() {
		return mGoogleApiClient;
	}

	public boolean testGoogleApi(FileIOMemory f) {
		return f.getDeclinedToSignIn() || getGoogleApi() != null;
	}

	public boolean isWorkingGoogleApi() {
		return getGoogleApi() != null && getGoogleApi().isConnected();
	}

	public static class ACRAHandler {
		public static final String IS_IN_DEBUG = "\nDebug mode";
		public static final String LEVEL_DONE = "Last level done";
		public static final String MONEY = "Money";
		public static final String OFFENSIVE_MOD = "Offensive modifications";
		public static final String DEFENSIVE_MOD = "Defensive modifications";
		public static final String GAMEPLAY_TIME = "Gameplay time";
		public static final String DECLINED_SIGN_IN = "Declined sign in";
		public static final String LEVEL = "Level";
	}

}
