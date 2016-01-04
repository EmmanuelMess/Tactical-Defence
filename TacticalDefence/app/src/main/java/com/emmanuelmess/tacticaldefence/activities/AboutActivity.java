package com.emmanuelmess.tacticaldefence.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity;

import static com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity.m;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_about);

		testMusic(m, getApplicationContext());
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

	public void onClickContact(View view) {
		Context context = getApplicationContext();
		String extraText;

		try {
			Intent email = new Intent(Intent.ACTION_SEND);
			extraText = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "\n"
					+ "------------------------" + "\n";

			email.putExtra(Intent.EXTRA_SUBJECT,
					"[" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "] " + "CONTACT");
			email.putExtra(Intent.EXTRA_TEXT, extraText);
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, context.getString(R.string.email_chooser)));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onClickCredits(View view) {
		startActivity(new Intent(this, CreditsActivity.class));
	}

	@SuppressWarnings("UnusedParameters")
	public void onClickHelp(View view) {
		startActivity(new Intent(this, DonationActivity.class));
	}

	public static void testMusic(MediaPlayer m, Context context) {
		if(m == null) context.startActivity(new Intent(context, GameMenuActivity.class));
	}

}
