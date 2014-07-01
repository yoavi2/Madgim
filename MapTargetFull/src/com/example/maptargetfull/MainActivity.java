package com.example.maptargetfull;

// Lior The Magnificent

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AbstractNavDrawerActivity {
	// Constants
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.example.maptargetfull.provider";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "example.com";
	// The account name
	public static final String ACCOUNT = "dummyaccount";

	private DialogDetails dialog = new DialogDetails();

	public static String currFragment;

	// Instance fields
	Account mAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the dummy account
		mAccount = CreateSyncAccount(this);

		GlobalParams.getInstance().PointsDBaccess = new PointsDBAccess(this);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new FirstFragment()).commit();
		}
	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

		NavDrawerItem[] menu = new NavDrawerItem[] {
				NavMenuSection.createMenuSection(100, R.string.text_mapsection),
				NavMenuItem.createMenuItem(101, R.string.text_googlemap,
						R.drawable.google_icon, true, false),
				NavMenuItem.createMenuItem(102, R.string.text_map,
						R.drawable.ic_action_locate, true, false),
				NavMenuItem.createMenuItem(103, R.string.text_list,
						R.drawable.ic_action_paste, true, false) };

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
			break;
		case 102:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new FirstFragment()).commit();
			currFragment = FirstFragment.TAG;
			this.invalidateOptionsMenu();
			break;
		case 103:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new SecondFragment()).commit();
			currFragment = SecondFragment.TAG;
			this.invalidateOptionsMenu();
			break;
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
			dialog.show(getSupportFragmentManager(), "test");

			return true;
		} else if(id == R.id.action_list) {
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new SecondFragment()).commit();
			currFragment = SecondFragment.TAG;
			this.invalidateOptionsMenu();
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if (currFragment == FirstFragment.TAG || currFragment == null) {
			menu.findItem(R.id.action_settings).setVisible(true);
			menu.findItem(R.id.action_list).setVisible(true);
		} else {
			menu.findItem(R.id.action_settings).setVisible(false);
			menu.findItem(R.id.action_list).setVisible(false);
		}
		return true;
	}

	static public class MainFragment extends Fragment {

		public static String TAG = "main_fragment";

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View mainView = inflater.inflate(R.layout.fragment_main, container,
					false);

			return mainView;
		}
	}

}