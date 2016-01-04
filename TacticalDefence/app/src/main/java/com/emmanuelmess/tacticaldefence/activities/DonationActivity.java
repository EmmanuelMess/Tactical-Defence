package com.emmanuelmess.tacticaldefence.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.emmanuelmess.tacticaldefence.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import de.schildbach.wallet.integration.android.BitcoinIntegration;

import static com.emmanuelmess.tacticaldefence.activities.game.GameMenuActivity.m;

/**
 * @author Emmanuel & https://github.com/dschuermann
 *         2015-02-13, at 04:28 AM.
 */
public class DonationActivity extends ListActivity {

	private final int REQUEST_CODE = 0;
	private InterstitialAd mInterstitialAd;
	private boolean autoLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donation);

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getApplicationContext().getString(R.string.ad_unit_id));
		requestNewInterstitial();

		AboutActivity.testMusic(m, getApplicationContext());
		m.start();

		String[] values = new String[]{getResources().getString(R.string.advertising),getResources().getString(R.string.Bitcoin), getResources().getString(R.string.Paypal)};

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		getListView().setAdapter(adapter);

		mInterstitialAd.setAdListener(new AdListener() {

			@Override
			public void onAdLoaded() {
				if (autoLoad) {
					mInterstitialAd.show();
					autoLoad = false;
				}
			}

			@Override
			public void onAdClosed() {
				requestNewInterstitial();
			}
		});
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			String txHash = BitcoinIntegration.transactionHashFromResult(result);
			if (txHash != null) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.thanks), Toast.LENGTH_LONG).show();
			}
		} else if (resultCode != Activity.RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.horribly_wrong), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				if (mInterstitialAd.isLoaded()) {
					mInterstitialAd.show();
				} else if(autoLoad) {
					Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.already_loading), Toast.LENGTH_SHORT).show();
				} else if(mInterstitialAd.isLoading()) {
					autoLoad = true;
					Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.loading), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
					requestNewInterstitial();
				}
				break;
			case 1:
				BitcoinIntegration.requestForResult(this, REQUEST_CODE, "1Pb9aZygyzeK3B3ucMpr2crbyhpQZVWAWV");
				break;
			case 2:
				PayPal();
				break;
		}
	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
					.addTestDevice("0AEDA9876DA8499B8911FAE8C4501229")
					.build();

		mInterstitialAd.loadAd(adRequest);
	}

	/**
	 * @author https://github.com/dschuermann
	 */
	private void PayPal() {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
		uriBuilder.appendQueryParameter("cmd", "_donations");
		uriBuilder.appendQueryParameter("business", "emmanuelbendavid@gmail.com");
		//uriBuilder.appendQueryParameter("lc", "US");//TODO country
		uriBuilder.appendQueryParameter("item_name", "Donation for Tactical Defence");
		uriBuilder.appendQueryParameter("no_note", "1");
		// uriBuilder.appendQueryParameter("no_note", "0");
		// uriBuilder.appendQueryParameter("cn", "Note to the developer");
		uriBuilder.appendQueryParameter("no_shipping", "1");
		uriBuilder.appendQueryParameter("currency_code", "USD");
		Uri payPalUri = uriBuilder.build();

		try {
			Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
			startActivity(viewIntent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.need_browser), Toast.LENGTH_SHORT).show();
		}
	}

}
