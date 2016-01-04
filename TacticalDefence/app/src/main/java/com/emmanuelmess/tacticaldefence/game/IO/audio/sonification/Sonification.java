package com.emmanuelmess.tacticaldefence.game.IO.audio.sonification;

import android.content.Context;
import android.media.SoundPool;

import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.game.IO.audio.Audio;

import java.util.Random;

/**
 * @author Emmanuel
 *         on 2015-02-18, at 12:13 AM, at 22:27.
 */
public class Sonification extends Audio {

	private final Random ran;
	private final int[][] i;
	private SoundPool sp;

	public Sonification(Context context) {
		super(context);
		ran = new Random();

		sp = this.getSoundPool();

		i = new int[][]{{sp.load(context, R.raw.piece_tower_arrow_shot_1, 1), sp.load(context, R.raw.piece_tower_arrow_shot_2, 1),
				sp.load(context, R.raw.piece_tower_arrow_shot_3, 1)},
				{sp.load(context, R.raw.piece_tower_arrow_impact, 1)}, {sp.load(context, R.raw.piece_healer_heal, 1)}};

	}

	public int shoot(float percentageInLeft) {
		float left = percentageInLeft/100, right = (100 - percentageInLeft)/100;
		int i = this.i[0][ran.nextInt(3)];

		do sp.play(i, left, right, 0, 0, 1);
		while (!this.isLoaded(i));

		return i;
	}

	public int impacted(float percentageInLeft) {
		float left = percentageInLeft/100, right = (100 - percentageInLeft)/100;
		int i = this.i[1][ran.nextInt(1)];

		do sp.play(i, left, right, 0, 0, 1);
		while (!this.isLoaded(i));


		return i;
	}

	public int heal(float percentageInLeft) {
		float left = percentageInLeft/100, right = (100 - percentageInLeft)/100;
		int i = this.i[2][ran.nextInt(1)];

		do sp.play(i, left, right, 0, 0, 1);
		while (!this.isLoaded(i));

		return i;
	}

	public void unload(int i) {
		sp.unload(i);
	}

	public void unloadAll() {
		for (int i[] : this.i) for (int e : i) sp.unload(e);
	}

	public void release() {
		sp.release();
		sp = null;
	}
}
