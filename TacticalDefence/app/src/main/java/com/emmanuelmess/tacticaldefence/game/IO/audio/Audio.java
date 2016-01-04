package com.emmanuelmess.tacticaldefence.game.IO.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmanuel
 *         on 2015-02-21, at 08:50 PM.
 */
@SuppressWarnings("deprecation")
public abstract class Audio {

	private final int CURRENT_API_VERSION = android.os.Build.VERSION.SDK_INT;

	private final Context context;
	private final Map<Integer, Boolean> loaded = new HashMap<>();

	protected Audio(Context context) {
		this.context = context;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected SoundPool getSoundPool() {
		SoundPool sp;

		if (CURRENT_API_VERSION >= Build.VERSION_CODES.LOLLIPOP) {
			SoundPool.Builder spb = new SoundPool.Builder();

			spb.setAudioAttributes(new AudioAttributes.Builder()
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.setLegacyStreamType(AudioManager.STREAM_MUSIC)
					.setUsage(AudioAttributes.USAGE_GAME)
					.build());
			spb.setMaxStreams(1);

			sp = spb.build();
		} else {
			sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		}

		sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loaded.put(sampleId, true);
			}
		});

		return sp;
	}

	public long getSoundDuration(int rawId) {//TODO use or delete
		MediaPlayer player = MediaPlayer.create(context, rawId);
		return player.getDuration();
	}

	protected boolean isLoaded(int i) {
		return loaded.get(i) != null? loaded.get(i):false;
	}

}
