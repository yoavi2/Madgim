package com.example.maptargetfull;

// Lior The Magnificent

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.example.maptargetfull.PointsDBAccess.Point;

public class MainActivity extends AbstractNavDrawerActivity {
	// Constants
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.example.maptargetfull.provider";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "example.com";
	// The account name
	public static final String ACCOUNT = "dummyaccount";

	private DialogDetails dialog = new DialogDetails();

	private Dialog pdialog;

	public String currFragment;
	public static String originFragment;

	// Instance fields
	Account mAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the dummy account
		mAccount = CreateSyncAccount(this);
		android.os.Debug.waitForDebugger();

		GlobalParams.getInstance().PointsDBaccess = new PointsDBAccess(this);
        
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new FirstFragment(), FirstFragment.TAG).commit();
			this.currFragment = FirstFragment.TAG; 
			originFragment = FirstFragment.TAG;
		}
	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

		NavDrawerItem[] menu = new NavDrawerItem[] {
				NavMenuItem.createMenuItem(101, R.string.text_googlemap,
						R.drawable.google_map_icon, true, false),
				NavMenuItem.createMenuItem(102, R.string.text_map,
						R.drawable.image_map_icon, true, false) };

		NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
		navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
		navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
		navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
		navDrawerActivityConfiguration.setNavItems(menu);
		navDrawerActivityConfiguration
				.setDrawerShadow(R.drawable.drawer_shadow);
		navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
		navDrawerActivityConfiguration
				.setDrawerCloseDesc(R.string.drawer_close);
		navDrawerActivityConfiguration.setDrawerIcon(R.drawable.ic_drawer);
		navDrawerActivityConfiguration.setAdapter(new NavDrawerAdapter(this,
				R.layout.navdrawer_item, menu));
		return navDrawerActivityConfiguration;
	}

	@Override
	protected void onNavItemSelected(int id) {
		switch (id) {
		case 101:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, new GoogleMapFragment(),
							GoogleMapFragment.TAG).commit();
			this.currFragment = GoogleMapFragment.TAG;
			originFragment = GoogleMapFragment.TAG;
			break;
		case 102:
			FirstFragment first = new FirstFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, first).commit();
			this.currFragment = FirstFragment.TAG;
			originFragment = FirstFragment.TAG;
			GlobalParams.getInstance().setProgress(first);
			this.invalidateOptionsMenu();
			break;
		// case 103:
		// SecondFragment second = new SecondFragment();
		// getFragmentManager().beginTransaction()
		// .replace(R.id.content_frame, second).commit();
		// currFragment = SecondFragment.TAG;
		// GlobalParams.getInstance().setProgress(second);
		// this.invalidateOptionsMenu();
		// break;
		}
	}

	public static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
		}

		return newAccount;
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// SecondFragment fragment = (SecondFragment)
		// getFragmentManager().findFragmentBy
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			dialog.show(getFragmentManager(), "test");

			return true;
		} else if (id == R.id.action_list) {
			int i = getFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.content_frame, new SecondFragment(),SecondFragment.TAG).commit();
			this.currFragment = SecondFragment.TAG;
			this.invalidateOptionsMenu();
		} else if (id == R.id.action_refresh) {

			// Pass the settings flags by inserting them in a bundle
			Bundle settingsBundle = new Bundle();
			settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
					true);
			/*
			 * Request the sync for the default account, authority, and manual
			 * sync settings
			 */
			android.os.Debug.waitForDebugger();

			pdialog = new ProgressDialog(this);
			pdialog.setTitle("Refreshing data...");
			pdialog.show();

			ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if (currFragment.equals(FirstFragment.TAG)) {
			menu.findItem(R.id.action_settings).setVisible(true);
			menu.findItem(R.id.action_list).setVisible(true);
		}else if(currFragment.equals(GoogleMapFragment.TAG)){
			menu.findItem(R.id.action_settings).setVisible(false);
			menu.findItem(R.id.action_list).setVisible(true);
		}
		else {
			menu.findItem(R.id.action_settings).setVisible(false);
			menu.findItem(R.id.action_list).setVisible(false);
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(syncFinishedReceiver,
				new IntentFilter(GlobalParams.getInstance().syncFinished));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(syncFinishedReceiver);
	}

	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("finish", "Sync finished, should refresh nao!!");

			GlobalParams.getInstance().clearList();

			ArrayList<Point> points = GlobalParams.getInstance().PointsDBaccess
					.getPoints(false);
			for (Point point : points) {

				Double langitude = point.langitude;
				Double longitude = point.longitude;

				GlobalParams.getInstance().addFriend(
						new Friend(point.first_name, point.rowID,
								point.last_name, langitude.intValue(),
								longitude.intValue()));

				pdialog.hide();
				GlobalParams.getInstance().getf().getView().postInvalidate();
			}

		}
	};

	@Override
	public void onBackPressed() {
		this.currFragment = originFragment;
		invalidateOptionsMenu();
		super.onBackPressed();
	}



	
	
}