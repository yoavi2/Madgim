package com.example.maptargetfull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maptargetfull.PointsDBAccess.Point;
import com.example.maptargetfull.PointsDBAccess.PointForSync;
import com.example.maptargetfull.SQLiteDB.Points;

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
	private SettingsFragment settings = new SettingsFragment();
	
	public Dialog pdialog;

	public String currFragment;
	public static String originFragment;
	public static boolean didSyncFailed;
	private MqttAndroidClient c;
	private OfflineMap mOfflineMapView;

	// Instance fields
	Account mAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GlobalParams.getInstance().currActivity = this;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		GlobalParams.getInstance().height = dm.heightPixels;
		GlobalParams.getInstance().width = dm.widthPixels;

		// Create the dummy account
		mAccount = CreateSyncAccount(this);

		GlobalParams.getInstance().PointsDBaccess = new PointsDBAccess(this);

		if (savedInstanceState == null) {
			// FirstFragment first = new FirstFragment();
			// getFragmentManager().beginTransaction()
			// .replace(R.id.content_frame, first, FirstFragment.TAG)
			// .commit();
			// GlobalParams.getInstance().setProgress(first);
			// this.currFragment = FirstFragment.TAG;
			// originFragment = FirstFragment.TAG;
			OfflineMapFragment offline = new OfflineMapFragment();
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, offline,
							OfflineMapFragment.TAG).commit();

			SecondFragment list = new SecondFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.list_frame, list, "list_frame").commit();

			GlobalParams.getInstance().setProgress(offline);
			this.currFragment = OfflineMapFragment.TAG;
			originFragment = OfflineMapFragment.TAG;
		}

		SharedPreferences shar = MainActivity.this.getSharedPreferences("set", Context.MODE_PRIVATE);
		
		String ipb = shar.getString("IPB", "0.0.0.0");
		
		c = new MqttAndroidClient(this, "tcp://" + ipb + ":1883", UUID
				.randomUUID().toString());

		try {
			Log.d("_sagi_", "tcp://" + ipb + ":1883");
			c.setCallback(new mqtthandler(this, c));
			
			MqttConnectOptions opt = new MqttConnectOptions();
			int timeout = Integer.parseInt(shar.getString("TIMEOUT", "30"));
			opt.setConnectionTimeout(timeout);
			
			c.connect(opt, this, this);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("_sagi_", e.getMessage());
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("_sagi_", e.getMessage());
		}
		
		if (!isMyServiceRunning(mqtt_notification.class)) {
			Intent serviceStartIntent = new Intent();
			serviceStartIntent.setClassName(this, "com.example.maptargetfull.mqtt_notification");
			this.startService(serviceStartIntent);
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
		navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);
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

			settings.show(getFragmentManager(), "settings");
			
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
			this.refresh(false);

			// Reload current fragment
//			Fragment frg = null;
//			frg = GlobalParams.getFragment().findFragmentByTag("list_frame");
//			final FragmentTransaction ft = GlobalParams.getFragment()
//					.beginTransaction();
//			ft.detach(frg);
//			ft.attach(frg);
//			ft.commit();

			break;
		case R.id.action_online_map_toggle:
			if (GlobalParams.getInstance().isOffline) {
				item.setIcon(R.drawable.ic_action_cloud_on);
				GlobalParams.getInstance().isOffline = false;

				GlobalParams.goToOnlineMap();
				GlobalParams.addMarkersFromDB();

				Toast.makeText(this, "Online map", Toast.LENGTH_SHORT).show();
			} else {
				item.setIcon(R.drawable.ic_action_cloud_off);
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

	public void refresh(Boolean withDialog) {
		// Pass the settings flags by inserting them in a bundle
		// Bundle settingsBundle = new Bundle();
		// settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		// settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
		// true);
		// /*
		// * Request the sync for the default account, authority, and manual
		// sync
		// * settings
		// */
		if (withDialog) {
			pdialog = new ProgressDialog(this);
			pdialog.setTitle("Refreshing data...");
			pdialog.show();
		}
		
		new refreshAsync().execute(pdialog);

		// if (!this.isConnected()) {
		// refresh_view();
		// } else {
		// ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
		// }

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (this.didSyncFailed) {
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
			menu.findItem(R.id.action_settings).setVisible(true);
			menu.findItem(R.id.action_list).setVisible(false);
			menu.findItem(R.id.action_online_map_toggle).setVisible(true);
		} else {
			menu.findItem(R.id.action_settings).setVisible(true);
			menu.findItem(R.id.action_list).setVisible(false);
			menu.findItem(R.id.action_online_map_toggle).setVisible(false);
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// registerReceiver(syncFinishedReceiver,
		// new IntentFilter(GlobalParams.getInstance().syncFinished));
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(syncFinishedReceiver);
	}

//	@Override
//	protected void onStop() {
//		super.onStop();
//
//		if(pdialog!= null)
//			pdialog.dismiss();
//	}
	
	// private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver()
	// {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// didSyncFailed = !intent.getExtras().getBoolean(
	// GlobalParams.getInstance().syncSucceeded);
	//
	// refresh_view();
	// }
	// };

	public void refresh_view() {
		invalidateOptionsMenu();

		Fragment currFrag = getFragmentManager()
				.findFragmentByTag(currFragment);

		if (currFragment.equals(FirstFragment.TAG)) {
			new refreshAsyncFirstFragment().execute(pdialog);
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

	public class refreshAsync extends AsyncTask<Dialog, Void, Void> {
		private Dialog pdialog;
		private String url = "http://192.168.43.69:3000/friends";
		private Boolean succeeded = true;

		@Override
		protected Void doInBackground(Dialog... params) {
			android.os.Debug.waitForDebugger();
			pdialog = params[0];

			PointsDBAccess pointsDB = new PointsDBAccess(MainActivity.this);
			ArrayList<PointForSync> arrayPoint = pointsDB.getPointsForSync();
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			
			//////////
			SharedPreferences shar = MainActivity.this.getSharedPreferences("set", Context.MODE_PRIVATE);
			
			String timeoutString = shar.getString("TIMEOUT", "10");
			String ipm = shar.getString("IPM", "0.0.0.0");
			
			
			url = "http://" + ipm + ":3000/friends";
			
			//////////
			int timeout = 10;
			
			try {
				timeout = Integer.parseInt(timeoutString); // seconds
			} catch (Exception e) {

			}
			
			HttpParams httpParams = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					timeout * 1000); // http.connection.timeout
			HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000); // http.socket.timeout
			// Sync device to server
			for (int i = 0; i < arrayPoint.size(); i++) {
				JSONObject json = new JSONObject();
				try {
					json.put(Points.Columns.first_name,
							arrayPoint.get(i).first_name);
					json.put(Points.Columns.last_name,
							arrayPoint.get(i).last_name);
					json.put(Points.Columns.longitude,
							arrayPoint.get(i).longitude);
					json.put(Points.Columns.langitude,
							arrayPoint.get(i).langitude);

					json.put(Points.Columns.is_deleted,
							arrayPoint.get(i).is_deleted);
					json.put(Points.Columns.is_google,
							arrayPoint.get(i).is_google);
					json.put(Points.Columns.point_type,
							arrayPoint.get(i).pointType);

					if (arrayPoint.get(i).server_id == null) {
						HttpPost httpPost = new HttpPost(url);

						httpPost.setHeader("Accept", "application/json");
						httpPost.setHeader("Content-Type",
								"application/json;charset=UTF-8");

						StringEntity se = new StringEntity(json.toString(),
								"UTF-8");

						httpPost.setEntity(se);
						ResponseHandler responseHandler = new BasicResponseHandler();
						httpclient.execute(httpPost, responseHandler);
					} else {

						HttpPut httpPut = new HttpPut(url + "/"
								+ arrayPoint.get(i).server_id);

						httpPut.setHeader("Accept", "application/json");
						httpPut.setHeader("Content-Type",
								"application/json;charset=UTF-8");

						httpPut.setEntity(new StringEntity(json.toString(),
								"UTF-8"));

						ResponseHandler responseHandler = new BasicResponseHandler();
						httpclient.execute(httpPut, responseHandler);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("___JSONException0___", e.getMessage());
					succeeded = false;
					break;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("___UnsupportedEncodingException0___", e.getMessage());
					succeeded = false;
					break;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("___ClientProtocolException0___", e.getMessage());
					succeeded = false;
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("___ClientProtocolException0___", e.getMessage());
					succeeded = false;
					break;
				}
			}
			if (succeeded == true) {
				// Sync server to device
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse;

				try {
					httpResponse = httpclient.execute(httpGet);

					// receive response as inputStream
					InputStream inputStream = httpResponse.getEntity()
							.getContent();

					String result = convertInputStreamToString(inputStream);

					JSONObject json = new JSONObject(result);

					JSONArray friends = json.getJSONArray("friends");

					arrayPoint.clear();

					// Run over all the friends, create an instance and add them
					// to
					// the array in global class
					for (int i = 0; i < friends.length(); i++) {
						if (friends.getJSONObject(i).getInt(
								Points.Columns.is_deleted) == SQLiteDB
								.convertBoolean(false)) {
							PointForSync ps = pointsDB.new PointForSync();
							ps.first_name = friends.getJSONObject(i).getString(
									Points.Columns.first_name);
							ps.last_name = friends.getJSONObject(i).getString(
									Points.Columns.last_name);
							ps.longitude = friends.getJSONObject(i).getDouble(
									Points.Columns.longitude);
							ps.langitude = friends.getJSONObject(i).getDouble(
									Points.Columns.langitude);
							ps.is_google = friends.getJSONObject(i).getInt(
									Points.Columns.is_google);
							ps.server_id = friends.getJSONObject(i).getString(
									"_id");
							ps.pointType = friends.getJSONObject(i).getInt(
									Points.Columns.point_type);
							arrayPoint.add(ps);
						}
					}

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("__ClientProtocolException___", e.getMessage());
					succeeded = false;

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("___IOException___", e.getMessage());
					succeeded = false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("___JSONException___", e.getMessage());
					succeeded = false;
				}
			}
			if (succeeded == true) {
				pointsDB.deletePointsForSync();

				for (int i = 0; i < arrayPoint.size(); i++) {
					long rowID = pointsDB.createPoint(
							arrayPoint.get(i).first_name,
							arrayPoint.get(i).last_name,
							arrayPoint.get(i).longitude,
							arrayPoint.get(i).langitude,
							arrayPoint.get(i).is_google == 1 ? true : false,
							arrayPoint.get(i).pointType);
					pointsDB.SetServerID(rowID, arrayPoint.get(i).server_id);
					pointsDB.SetSynched(rowID);
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pdialog.hide();
			MainActivity.didSyncFailed = !succeeded;
			refresh_view();
		}

		private String convertInputStreamToString(InputStream inputStream)
				throws IOException {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = "";
			String result = "";
			while ((line = bufferedReader.readLine()) != null)
				result += line;

			inputStream.close();
			return result;

		}
	}

	static public class refreshAsyncFirstFragment extends
			AsyncTask<Dialog, Void, Void> {
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
		// while (!c.isConnected())
		// {
		// try {
		// c.connect();
		// } catch (MqttException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		Toast.makeText(GlobalParams.getInstance().currActivity, "FAIL",
				Toast.LENGTH_LONG).show();
	}
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
