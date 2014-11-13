package com.example.maptargetfull;

import java.util.ArrayList;
import java.util.UUID;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maptargetfull.PointsDBAccess.Point;

public class MainActivity extends AbstractNavDrawerActivity implements
		MqttCallback, IMqttActionListener {
	// Constants
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.example.maptargetfull.provider";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "example.com";
	// The account name
	public static final String ACCOUNT = "dummyaccount";

	private DialogDetails dialog = new DialogDetails();

	public Dialog pdialog;

	public String currFragment;
	public static String originFragment;
	private boolean didSyncFailed;
	private MqttAndroidClient c;
	private OfflineMap mOfflineMapView;

	// Instance fields
	Account mAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GlobalParams.getInstance().currActivity = this;
		
		// Create the dummy account
		mAccount = CreateSyncAccount(this);

		GlobalParams.getInstance().PointsDBaccess = new PointsDBAccess(this);

		if (savedInstanceState == null) {
//			FirstFragment first = new FirstFragment();
//			getFragmentManager().beginTransaction()
//					.replace(R.id.content_frame, first, FirstFragment.TAG)
//					.commit();
//			GlobalParams.getInstance().setProgress(first);
//			this.currFragment = FirstFragment.TAG;
//			originFragment = FirstFragment.TAG;
			OfflineMapFragment offline = new OfflineMapFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, offline, OfflineMapFragment.TAG)
					.commit();
			
			SecondFragment list = new SecondFragment();
			getFragmentManager().beginTransaction()
			.replace(R.id.list_frame, list, "list_frame")
			.commit();
			
			GlobalParams.getInstance().setProgress(offline);
			this.currFragment = OfflineMapFragment.TAG;
			originFragment = OfflineMapFragment.TAG;
		}

		c = new MqttAndroidClient(this, "tcp://192.168.43.37:1883", UUID.randomUUID().toString());

		try {
			c.setCallback(new mqtthandler(this, c));
			c.connect(this, this);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.refresh(true);
	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

		NavDrawerItem[] menu = new NavDrawerItem[] {
				NavMenuItem.createMenuItem(101, R.string.text_googlemap,
						R.drawable.google_map_icon, true, false),
				NavMenuItem.createMenuItem(102, R.string.text_map,
						R.drawable.image_map_icon, true, false),
				NavMenuItem.createMenuItem(103, R.string.text_mapa,
						R.drawable.mapa_icon, true, false),
				NavMenuItem.createMenuItem(104, R.string.text_ua3,
						R.drawable.google_map_icon, true, false), };

		NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
		navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
		navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
		navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
		navDrawerActivityConfiguration.setNavItems(menu);
		navDrawerActivityConfiguration
				.setDrawerShadow(R.drawable.x);
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
		getFragmentManager().popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
					.replace(R.id.content_frame, first, FirstFragment.TAG)
					.commit();
			this.currFragment = FirstFragment.TAG;
			originFragment = FirstFragment.TAG;
			GlobalParams.getInstance().setProgress(first);
			break;
		case 103:
			WebViewFragment web = new WebViewFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, web, WebViewFragment.TAG)
					.commit();
			this.currFragment = WebViewFragment.TAG;
			originFragment = WebViewFragment.TAG;
			break;
		case 104:
			OfflineMapFragment offline = new OfflineMapFragment();
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, offline,
							OfflineMapFragment.TAG).commit();
			this.currFragment = OfflineMapFragment.TAG;
			originFragment = OfflineMapFragment.TAG;
			break;
		}

		this.invalidateOptionsMenu();
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

	// private ActionMode.Callback mActionModeCallback = new
	// ActionMode.Callback() {
	//
	// // Called when the action mode is created; startActionMode() was called
	// @Override
	// public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	// // Inflate a menu resource providing context menu items
	// MenuInflater inflater = mode.getMenuInflater();
	// inflater.inflate(R.menu.currmarker, menu);
	// return true;
	// }
	//
	// // Called each time the action mode is shown. Always called after
	// onCreateActionMode, but
	// // may be called multiple times if the mode is invalidated.
	// @Override
	// public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	// return false; // Return false if nothing is done
	// }
	//
	// // Called when the user selects a contextual menu item
	// @Override
	// public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.tv_name:
	// // shareCurrentItem();
	// mode.finish(); // Action picked, so close the CAB
	// return true;
	// default:
	// return false;
	// }
	// }
	//
	// // Called when the user exits the action mode
	// @Override
	// public void onDestroyActionMode(ActionMode mode) {
	// GlobalParams.getInstance().mActionMode = null;
	// }
	// };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		GlobalParams.setMenu(menu);
		GlobalParams.getInstance().inflater = getMenuInflater();
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// SecondFragment fragment = (SecondFragment)
		// getFragmentManager().findFragmentBy
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			dialog.show(getFragmentManager(), "test");
			return true;
		case R.id.action_list:
			// getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag(currFragment));
			getFragmentManager()
					.beginTransaction()
					.addToBackStack(null)
					.replace(R.id.content_frame, new SecondFragment(),
							SecondFragment.TAG).commit();
			this.currFragment = SecondFragment.TAG;
			this.invalidateOptionsMenu();
			break;
		case R.id.action_refresh:
			this.refresh(true);
			
			// Reload current fragment
			Fragment frg = null;
			frg = GlobalParams.getFragment().findFragmentByTag("list_frame");
			final FragmentTransaction ft = GlobalParams.getFragment().beginTransaction();
			ft.detach(frg);
			ft.attach(frg);
			ft.commit();
			
			break;
		case R.id.action_online_map_toggle:
			 if (GlobalParams.getInstance().isOffline) {
				 item.setIcon(R.drawable.cloud_on);
				 GlobalParams.getInstance().isOffline = false;
				 
				 GlobalParams.goToOnlineMap();
				 GlobalParams.addMarkersFromDB();
				 
				 Toast.makeText(this, "Online map", Toast.LENGTH_SHORT).show();
			 }
			 else {
				 item.setIcon(R.drawable.cloud_off);
				 GlobalParams.getInstance().isOffline = true;
				 
				 GlobalParams.goToOfflineMap();
				 GlobalParams.addMarkersFromDB();
				 
				 Toast.makeText(this, "Offline map", Toast.LENGTH_SHORT).show();
			 }
			break;
		case R.id.action_connect:
			Toast.makeText(
					this,
					"Data may not be synced. Internet may be down or server isn't responding",
					Toast.LENGTH_LONG).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void refreshThis(Boolean withDialog) {
		// Pass the settings flags by inserting them in a bundle
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		if (withDialog) {
			pdialog = new ProgressDialog(this);
			pdialog.setTitle("Refreshing data...");
			pdialog.show();
		}

		if (!this.isConnected()) {
			
		} else {
//			ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
		}
	}
	
	public void refresh(Boolean withDialog) {
		// Pass the settings flags by inserting them in a bundle
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		if (withDialog) {
			pdialog = new ProgressDialog(this);
			pdialog.setTitle("Refreshing data...");
			pdialog.show();
		}

		if (!this.isConnected()) {
			refresh_view();
		} else {
			ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (!this.isConnected() || this.didSyncFailed) {
			menu.findItem(R.id.action_connect).setVisible(true);
		} else {
			menu.findItem(R.id.action_connect).setVisible(false);
		}

		if (currFragment.equals(FirstFragment.TAG)) {
			menu.findItem(R.id.action_settings).setVisible(true);
			menu.findItem(R.id.action_list).setVisible(true);
			menu.findItem(R.id.action_online_map_toggle).setVisible(false);
		} else if (currFragment.equals(GoogleMapFragment.TAG)
				|| currFragment.equals(OfflineMapFragment.TAG)) {
			menu.findItem(R.id.action_settings).setVisible(false);
			menu.findItem(R.id.action_list).setVisible(false);
			menu.findItem(R.id.action_online_map_toggle).setVisible(true);
		} else {
			menu.findItem(R.id.action_settings).setVisible(false);
			menu.findItem(R.id.action_list).setVisible(false);
			menu.findItem(R.id.action_online_map_toggle).setVisible(false);
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
			didSyncFailed = !intent.getExtras().getBoolean(
					GlobalParams.getInstance().syncSucceeded);

			refresh_view();
		}
	};

	public void refresh_view() {
		invalidateOptionsMenu();

		Fragment currFrag = getFragmentManager()
				.findFragmentByTag(currFragment);

		if (currFragment.equals(FirstFragment.TAG)) {
			new refreshAsync().execute(pdialog);
		} else if (currFragment.equals(SecondFragment.TAG)) {
			((SecondFragment) currFrag).new callservice(
					SecondFragment.ACTION_GET, pdialog).execute();
		} else if (currFragment.equals(GoogleMapFragment.TAG)) {
			this.onNavItemSelected(101);
			pdialog.hide();
		} else if (currFragment.equals(WebViewFragment.TAG)) {
			this.onNavItemSelected(103);
			pdialog.hide();
		} else if (currFragment.equals(OfflineMapFragment.TAG)) {
			this.onNavItemSelected(104);
			pdialog.hide();
		}
	}

	static public class refreshAsync extends AsyncTask<Dialog, Void, Void> {
		Dialog pdialog;

		@Override
		protected Void doInBackground(Dialog... params) {
			GlobalParams.getInstance().clearList();

			ArrayList<Point> points = GlobalParams.getInstance().PointsDBaccess
					.getPoints(false);
			for (Point point : points) {
				GlobalParams.getInstance().addPoint(point);
			}

			pdialog = params[0];

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			// if (GlobalParams.getInstance().getf() != null)
			// {
			// GlobalParams.getInstance().getf().getView().invalidate();
			// }

			GlobalParams.getInstance().getView().invalidate();

			pdialog.hide();
		}
	}

	@Override
	public void onBackPressed() {

		if (this.currFragment.equals(SecondFragment.TAG)) {
			// Reload google map + Popbackstack
			if (originFragment.equals(GoogleMapFragment.TAG)) {
				getFragmentManager().popBackStackImmediate();
				this.onNavItemSelected(101);
				// Super
			} else {
				this.currFragment = originFragment;
				super.onBackPressed();
			}

			// getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentByTag(currFragment)).commit();
			invalidateOptionsMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void lockDrawer(boolean lock) {
		super.lockDrawer(lock);
	}

	public boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

		return (activeNetwork != null && activeNetwork.isConnected());
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		Toast t = Toast.makeText(this, "LOST", Toast.LENGTH_LONG);
		t.show();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {

		if (topic.equals("test")) {
			// if (topic == "test") {
			Toast t = Toast.makeText(this, message.toString(),
					Toast.LENGTH_LONG);
			t.show();
		}
		// }
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		Toast t = Toast.makeText(this, "complete", Toast.LENGTH_LONG);
		t.show();
	}

	@Override
	public void onSuccess(IMqttToken asyncActionToken) {
		try {
			c.subscribe("update", 0);
			c.subscribe("insert", 0);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//		while (!c.isConnected())
//		{
//			try {
//				c.connect();
//			} catch (MqttException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		Toast.makeText(GlobalParams.getInstance().currActivity, "FAIL", Toast.LENGTH_LONG).show();
	}	
}
