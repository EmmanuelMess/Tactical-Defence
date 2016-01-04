package com.emmanuelmess.tacticaldefence.activities.game;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuelmess.tacticaldefence.MainActivity;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.activities.AboutActivity;
import com.emmanuelmess.tacticaldefence.activities.ExplanationsActivity;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.AICastle;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.PlayerCastle;
import com.emmanuelmess.tacticaldefence.game.render.RenderGameMenuActivity;
import com.google.android.gms.games.Games;

import org.acra.ACRA;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Emmanuel
 *         on 2014-12-14, at 06:21 PM.
 */
@SuppressWarnings("UnusedParameters")
public class GameMenuActivity extends Activity {

	public static final int MAX_LEVEL = 6;
	public static final String RECALL = "recall";

	public static int lvlIndex;
	public static MediaPlayer m;
	private static AICastle aiCastle;

	private static TextView v;

	private FileIOMemory f;

	public synchronized static AICastle getAiCastle() {
		return aiCastle;
	}

	public synchronized static void setAiCastle(AICastle c) {
		aiCastle = c;
	}

	public static TextView getLevelTextView() {
		return v;
	}

	public static int getLevel() {
		return lvlIndex;
	}

	public static int getMaximumLevel() {
		return MAX_LEVEL;
	}

	private static void sendToBack(final View child) {
		final ViewGroup parent = (ViewGroup) child.getParent();
		if (null != parent) {
			parent.removeView(child);
			parent.addView(child, 0);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_menu);
		if(((TacticalDefence) this.getApplication()).isWorkingGoogleApi())
			Games.setViewForPopups(((TacticalDefence) this.getApplication()).getGoogleApi(), getWindow().getDecorView().findViewById(android.R.id.content));

		try {
			f = new FileIOMemory(getApplicationContext());
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}

		v = (TextView) findViewById(R.id.level);

		m = MediaPlayer.create(getApplicationContext(), R.raw.music_menu);
		m.start();
		m.setLooping(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (aiCastle == null || m == null || f == null || !((TacticalDefence) this.getApplication()).testGoogleApi(f)) {
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			finish();
		}

		m.start();

		try {
			f.reload();
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}

		lvlIndex = f.getLevelDone() < MAX_LEVEL? f.getLevelDone() + 1:MAX_LEVEL;
		((RenderGameMenuActivity) findViewById(R.id.view_game_menu)).castle = new PlayerCastle(getApplicationContext(), f, true);
		long l = f.getGamePlayTime();
		((TextView) findViewById(R.id.gamePlayTime)).setText(
				String.format("%01d:%02d:%02d",
						TimeUnit.MILLISECONDS.toHours(l),
						TimeUnit.MILLISECONDS.toMinutes(l)%60,
						TimeUnit.MILLISECONDS.toSeconds(l)%60)
		);

		if(f != null && f.getDefensiveCastleModifications()[0] && f.getDefensiveCastleModifications() [2]
				&& areAllTrue(f.getOffensiveCastleModifications()))
			Games.Achievements.unlock(((TacticalDefence) this.getApplication()).getGoogleApi(), getApplicationContext().getString(R.string.achievement_finished_castle));

		goLoading(false);
	}

	@Override
	public void onPause() {
		super.onPause();

		m.pause();
	}

	public void startGame(View view) {
		startActivity(new Intent(view.getContext(), GameActivity.class));
		goLoading(true);
	}

	public void showUpgrades(View view) {
		startActivity(new Intent(view.getContext(), GameCastleModifyActivity.class));
	}

	public void onExplanationsSelected(View view) {
		if(TacticalDefence.isDebug())
			Games.Achievements.incrementImmediate(((TacticalDefence) this.getApplication()).getGoogleApi(), "CgkI8Z_wlPkbEAIQBw", 1);
		else startActivity(new Intent(view.getContext(), ExplanationsActivity.class));
	}

	public void onClickGames(View view) {
		if(((TacticalDefence) this.getApplication()).isWorkingGoogleApi())
			startActivityForResult(Games.Achievements.getAchievementsIntent(((TacticalDefence) this.getApplication()).getGoogleApi()), TacticalDefence.REQUEST_ACHIEVEMENTS);
		else Toast.makeText(getApplicationContext(), "GoogleApiClient not working", Toast.LENGTH_SHORT).show();
	}

	public void onClickAbout(View view) {
		startActivity(new Intent(this, AboutActivity.class));
	}

	public AICastle getAICastle() {
		return aiCastle;
	}

	private void goLoading(boolean load) {
		((RenderGameMenuActivity) findViewById(R.id.view_game_menu)).goLoading(load);
		View v = findViewById(R.id.view_game_menu);

		if (load) v.bringToFront();
		else sendToBack(v);

	}

	private static boolean areAllTrue(boolean[] array) {
		for(boolean b : array) if(!b) return false;
		return true;
	}

}
