package com.emmanuelmess.tacticaldefence.activities.game;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emmanuelmess.tacticaldefence.Debug;
import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.TacticalDefence;
import com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades;
import com.emmanuelmess.tacticaldefence.game.render.RenderGameCastleModifyActivityCastle;

import org.acra.ACRA;

import java.io.IOException;

import static com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment.BITMAP;
import static com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment.BOTTOM_DATA;
import static com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment.IS_DATA_CROSSED;
import static com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment.IS_RIGHT;
import static com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment.InterfaceDataCommunicator;
import static com.emmanuelmess.tacticaldefence.activities.fragments.SidebarFragment.WIDTH;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.DEFENSIVE_UPGRADES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.DEF_UPGRADES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.OFFENSIVE_UPGRADES;
import static com.emmanuelmess.tacticaldefence.game.inanimate.castle.CastleUpgrades.getPrice;

public class GameCastleModifyActivity extends FragmentActivity implements InterfaceDataCommunicator {

	public final static int NUMBER_OF_OFFENSIVE_MODIFICATIONS = 3;
	public final static int NUMBER_OF_DEFENSIVE_MODIFICATIONS = 3;
	private final static int SIDEBAR_WIDTH = 70;//TODO 2015-12-16 this shouldn't be hardcoded
	private static TextView moneyTextView, offensivePoints, defensivePoints;
	public boolean[] showOffensive;
	public boolean[] showDefensive;
	public int money;
	public int spent = 0;
	public FileIOMemory save;
	private RenderGameCastleModifyActivityCastle castle;
	private Fragment sidebar2;
	private String[][] bottomData;
	private boolean[][] isBottomDataCrossed;

	private MediaPlayer m;

	public static TextView getMoneyTextView() {
		return moneyTextView;
	}

	public static TextView getOffensivePoints() {
		return offensivePoints;
	}

	public static TextView getDefensivePoints() {
		return defensivePoints;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fragment sidebar1 = new SidebarFragment();
		sidebar2 = new SidebarFragment();
		Bundle argsS1 = new Bundle();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_game_castlemodify);

		castle = ((RenderGameCastleModifyActivityCastle) this.findViewById(R.id.castle));

		file();

		bottomData = new String[][]{new String[OFFENSIVE_UPGRADES.values().length], new String[DEF_UPGRADES.values().length]};
		isBottomDataCrossed = new boolean[][]{new boolean[OFFENSIVE_UPGRADES.values().length], new boolean[DEF_UPGRADES.values().length]};
		for (int j = 0; j < bottomData[0].length; j++) {
			bottomData[0][j] = "$" + CastleUpgrades.getPrice(OFFENSIVE_UPGRADES.values()[j]);
			isBottomDataCrossed[0][j] = showOffensive[j];
		}
		for (int j = 0; j < bottomData[1].length; j++) {
			bottomData[1][j] = "$" + CastleUpgrades.getPrice(DEF_UPGRADES.values()[j]);
			isBottomDataCrossed[1][j] = showDefensive[j>=1?j+1:j];
		}

		changeFragments(true, false);

		argsS1.putBoolean(IS_RIGHT, true);
		argsS1.putInt(WIDTH, SIDEBAR_WIDTH);
		argsS1.putIntArray(BITMAP, new int[]{R.drawable.upgrade_wall, R.drawable.upgrade_rampart});
		argsS1.putStringArray(BOTTOM_DATA, bottomData[1]);
		argsS1.putBooleanArray(IS_DATA_CROSSED, isBottomDataCrossed[1]);
		sidebar1.setArguments(argsS1);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.defensive_sidebar_view, sidebar1).commit();

		moneyTextView = (TextView) findViewById(R.id.money_spent);
		offensivePoints = (TextView) findViewById(R.id.offensive_points);
		defensivePoints = (TextView) findViewById(R.id.defensive_points);

		m = MediaPlayer.create(getApplicationContext(), R.raw.music_castle_upgrade);
		m.start();
		m.setLooping(true);

	}

	@Override
	protected void onPause() {
		super.onPause();
		m.pause();

		if (!TacticalDefence.isDebug()) money -= spent;

		try {
			if (save != null) {
				if (!TacticalDefence.isDebug()) save.setMoney(money);

				for (int i = 0; i < showOffensive.length; i++)
					save.setCastleModifications(showOffensive[i], i, FileIOMemory.OFFENSIVE);
				for (int i = 0; i < showDefensive.length; i++)
					save.setCastleModifications(showDefensive[i], i, FileIOMemory.DEFENSIVE);

				save.saveToFile();
			}
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}

		spent = 0;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (m != null) m.start();
		file();
		changeFragments(false, showDefensive[2]);
	}

	@Override
	public void updateData(int item, boolean isRight) {
		if (isRight) {
			if (item >= 1) item++;
			if (!save.getDefensiveCastleModifications()[item]) {
				if ((money - spent >= getPrice(DEFENSIVE_UPGRADES.values()[item]) || showDefensive[item])) {
					showDefensive[item] = !showDefensive[item];

					if (item == 2) changeFragments(false, showDefensive[2]);

					if (showDefensive[item]) {
						spent += getPrice(DEFENSIVE_UPGRADES.values()[item]);
					} else {
						spent -= getPrice(DEFENSIVE_UPGRADES.values()[item]);
					}
				} else castle.setRedMoney(true);
			}
		} else {
			if (!save.getOffensiveCastleModifications()[item]) {
				if ((money - spent >= getPrice(OFFENSIVE_UPGRADES.values()[item]) || showOffensive[item])) {
					showOffensive[item] = !showOffensive[item];

					if (showOffensive[item]) {
						spent += getPrice(OFFENSIVE_UPGRADES.values()[item]);
					} else {
						spent -= getPrice(OFFENSIVE_UPGRADES.values()[item]);
					}
				} else castle.setRedMoney(true);
			}
		}

		castle.postInvalidate();
	}

	private void changeFragments(boolean isNew, boolean isRampart) {
		Fragment sidebar2 = isNew? this.sidebar2:new SidebarFragment();
		Bundle argsS2 = new Bundle();

		argsS2.putBoolean(IS_RIGHT, false);
		argsS2.putInt(WIDTH, SIDEBAR_WIDTH);
		argsS2.putIntArray(BITMAP, new int[]{!isRampart? R.drawable.upgrade_tower_1:R.drawable.upgrade_tower_2, R.drawable.upgrade_archers,
				!isRampart? R.drawable.upgrade_tower_ii_1:R.drawable.upgrade_tower_ii_2});
		argsS2.putStringArray(BOTTOM_DATA, bottomData[0]);
		argsS2.putBooleanArray(IS_DATA_CROSSED, isBottomDataCrossed[0]);
		sidebar2.setArguments(argsS2);
		FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
		if (!isNew) transaction2.remove(this.sidebar2);
		transaction2.replace(R.id.offensive_sidebar_view, sidebar2).commit();
	}

	private void file() {
		try {
			save = new FileIOMemory(getApplicationContext());
			save.reload();
			money = !TacticalDefence.isDebug()? save.getMoney():Debug.AI_MONEY;

			if (save.getOffensiveCastleModifications() != null)
				showOffensive = save.getOffensiveCastleModifications().clone();

			if (save.getDefensiveCastleModifications() != null)
				showDefensive = save.getDefensiveCastleModifications().clone();
		} catch (IOException e) {
			ACRA.getErrorReporter().handleException(e);
		}
	}

}
