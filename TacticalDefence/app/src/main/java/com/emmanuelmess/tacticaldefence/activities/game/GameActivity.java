package com.emmanuelmess.tacticaldefence.activities.game;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.tacticaldefence.MainActivity;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.TacticalDefence.ACRAHandler;
import com.emmanuelmess.tacticaldefence.game.Game;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.render.RenderGameActivity;
import com.google.android.gms.games.Games;

import org.acra.ACRA;

import java.io.IOException;

public class GameActivity extends Activity {
	private static final int CURRENT_API_VERSION = android.os.Build.VERSION.SDK_INT;
	public static Chronometer gamePlayTime;
	private static Activity gameActivity;
	private static RenderGameActivity game;

	private static Activity getActivity() {
		return gameActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(game = new RenderGameActivity(this));

		if (((TacticalDefence) this.getApplication()).isWorkingGoogleApi())
			Games.setViewForPopups(((TacticalDefence) this.getApplication()).getGoogleApi(), getWindow().getDecorView().findViewById(android.R.id.content));

		ACRA.getErrorReporter().putCustomData(ACRAHandler.LEVEL, String.valueOf(GameMenuActivity.getLevel()));
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			FileIOMemory f = new FileIOMemory(getApplicationContext());

			if(!((TacticalDefence) this.getApplication()).testGoogleApi(f))
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}

		gamePlayTime = new Chronometer();
		gameActivity = this;
	}

	@Override
	public void onPause() {
		super.onPause();

		gamePlayTime.pause();
		try {
			FileIOMemory m = new FileIOMemory(getApplicationContext());
			m.setGamePlayTime(m.getGamePlayTime() + gamePlayTime.getElapsedTime());
			if (Game.won && m.getLevelDone() < GameMenuActivity.getLevel())
				m.setLevelDone(GameMenuActivity.getLevel());
			m.saveToFile();
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}
		gamePlayTime = null;

		if (game != null) {
			game.surfacePaused();
			game = null;
		}

		ACRA.getErrorReporter().removeCustomData(ACRAHandler.LEVEL);

		finish();
	}

	@Override
	public void onBackPressed() {
		if (game != null && game.onBackPressed()) {
			startActivity(new Intent(getApplicationContext(), GameMenuActivity.class));
			super.onBackPressed();
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		View decorView = getActivity().getWindow().getDecorView();
		if (hasFocus && CURRENT_API_VERSION >= Build.VERSION_CODES.KITKAT) {
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}


}