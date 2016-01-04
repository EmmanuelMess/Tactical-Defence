package com.emmanuelmess.tacticaldefence;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.emmanuelmess.API.Chronometer;
import com.emmanuelmess.API.FadeOut;
import com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity;
import com.emmanuelmess.tacticaldefence.game.IO.saving.FileIOMemory;
import com.emmanuelmess.tacticaldefence.game.inanimate.castle.players.AICastle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.io.IOException;

import static android.graphics.BitmapFactory.decodeResource;
import static com.emmanuelmess.tacticaldefence.TacticalDefence.DD;

/**
 * @author Emmanuel
 *         on 2015-01-04, at 12:14 PM.
 */
public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1;
	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	private static final String STATE_RESOLVING_ERROR = "resolving_error";
	private static Activity activity;
	private boolean doNotConnect, startGame;
	private Chronometer chr = new Chronometer();
	private AICastleCreation c;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}

	protected synchronized static Activity getActivity() {
		return activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		Log.v("MainActivity", "Starting activity");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(new RenderMainActivity(this));

		try {
			FileIOMemory f = new FileIOMemory(getApplicationContext());
			doNotConnect = f.getDeclinedToSignIn();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(!doNotConnect) {
			// Create a GoogleApiClient instance
			((TacticalDefence) this.getApplication()).setGoogleApi(new GoogleApiClient.Builder(this)
					.addApi(Games.API)//.addApiIfAvailable(Games.API, Games.SCOPE_GAMES)
					.addScope(Games.SCOPE_GAMES)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build());

			mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
		}

		c = new AICastleCreation();
		c.run();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Connected to Google Play services!
		// The good stuff goes here.
		startGame = true;
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection has been interrupted.C:\Users\alumno\.android
		// Disable any UI components that depend on Google APIs
		// until onConnected() is called.
		startGame = true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				((TacticalDefence) this.getApplication()).getGoogleApi().connect();
			}
		} else {
			// Show dialog using GoogleApiAvailability.getErrorDialog()
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
	}

	// The rest of this code is all about building the error dialog

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "errordialog");
	}

	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingError = false;
		startGame = true;
		try {
			new FileIOMemory(getApplicationContext()).setDeclinedSignIn(true).saveToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() { }

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(
					this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((MainActivity) getActivity()).onDialogDismissed();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to connect
				if (!((TacticalDefence) this.getApplication()).getGoogleApi().isConnecting() && !((TacticalDefence) this.getApplication()).getGoogleApi().isConnected()) {
					((TacticalDefence) this.getApplication()).getGoogleApi().connect();
				}
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!doNotConnect && !mResolvingError) {// && mGoogleApiClient.hasConnectedApi(Games.API)) {
			((TacticalDefence) this.getApplication()).getGoogleApi().connect();
			chr.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (GameMenuActivity.getAiCastle() == null)
			c.interrupt();
	}

	static class AICastleCreation extends Thread {
		AICastle c;

		@Override
		public void run() {
			c = new AICastle(getActivity().getApplicationContext(), true);

		}

		public synchronized AICastle getAICastle() {
			return c;
		}
	}

	class RenderMainActivity extends View {
		private final Bitmap EmmanuelMessWorkBitmap;
		private final Bitmap TacticalDefenceBitmap;
		private final FadeOut EmmanuelMessWORKS;
		private final FadeOut TacticalDefence;
		private final Paint paint = new Paint();
		private final Rect loadingBounds = new Rect();
		private Intent i;

		public RenderMainActivity(Context context) {
			super(context);
			EmmanuelMessWorkBitmap = decodeResource(context.getResources(), R.drawable.emmanuelmess_works);
			TacticalDefenceBitmap = decodeResource(context.getResources(), R.drawable.tactical_defence);
			EmmanuelMessWORKS = new FadeOut(1000);
			TacticalDefence = new FadeOut(1000);
			i = new Intent(activity.getApplicationContext(), GameMenuActivity.class);
		}

		@Override
		public boolean onTouchEvent(@NonNull MotionEvent event) {
			if (event.getActionMasked() == MotionEvent.ACTION_UP) {
				if (!EmmanuelMessWORKS.isFaded()) EmmanuelMessWORKS.setAlpha(0);
				else if (!TacticalDefence.isFaded()) TacticalDefence.setAlpha(0);
			}
			return true;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			setBackgroundColor(Color.BLACK);

			if (!EmmanuelMessWORKS.isFaded())
				EmmanuelMessWORKS.doFadeOut(canvas, EmmanuelMessWorkBitmap, DD.getWidth()/2 - EmmanuelMessWorkBitmap.getWidth()/2,
						DD.getHeight()/2 - EmmanuelMessWorkBitmap.getHeight()/2);
			else if (!TacticalDefence.isFaded())
				TacticalDefence.doFadeOut(canvas, TacticalDefenceBitmap, DD.getWidth()/2 - TacticalDefenceBitmap.getWidth()/2,
						DD.getHeight()/2 - TacticalDefenceBitmap.getHeight()/2);
			else {
				String l = getResources().getString(R.string.loading);

				paint.setColor(Color.RED);
				paint.setTextSize(56);
				paint.getTextBounds(l, 0, l.length(), loadingBounds);
				canvas.drawText(l, DD.getWidth()/2f - loadingBounds.width()/2f, DD.getHeight()/2f - loadingBounds.height()/2f, paint);
			}

			if (!TacticalDefence.isFaded() || (!doNotConnect && !startGame)) invalidate();
			else {
				try {
					c.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				GameMenuActivity.setAiCastle(c.getAICastle());
				startActivity(i);
				finish();
			}
		}

	}

}