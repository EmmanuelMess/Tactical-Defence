package com.emmanuelmess.tacticaldefence.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emmanuelmess.tacticaldefence.R;

import java.util.Scanner;

import static com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity.m;

/**
 * @author Emmanuel
 *         on 2015-02-09, at 01:41 AM, at 22:27.
 */
public class CreditsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_credits);

		Scanner sc = new Scanner(getResources().getString(R.string.credits_text));
		TextView tv = (TextView) findViewById(R.id.credits_textview);
		StringBuilder sb = new StringBuilder();

		sc.useDelimiter(",");
		while (sc.hasNext()) {
			String s = sc.next();
			//tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Integer.getInteger(s.substring(0,2)));
			sb.append(s.substring(2, s.length()));
		}

		sb.append("\n\nLicensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. " +
				"\nVisit http://tacticaldefence.blogspot.com.ar/p/lisence.html for more information about the licence");// TODO: 2015-10-31 languages

		tv.setText(sb.toString());

		AboutActivity.testMusic(m, getApplicationContext());
		m.start();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (m != null) m.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		m.pause();
	}

}
