package com.example.maptargetfull;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends DialogFragment {

	public static String TAG = "Settings";

//	private ProgressDialog pdialog;
//	public static final int ACTION_GET = 1;
//	private final int ACTION_ADD_SOLDIER = 2;
//	private boolean isTimeOut = false;
	public View rootView;
//	SwipeRefreshLayout swipeRefreshLayout;
//	ListView list;
//	ArrayList<String> categories = new ArrayList<String>();
//	FriendListAdapter adapter;

	public interface onclickListener {
		public void onRefreshSelected();
	}

//	public void update() {
//		new callservice(ACTION_GET).execute();
//	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		SharedPreferences shar = GlobalParams.getInstance().inflaterContext.getSharedPreferences("set", Context.MODE_PRIVATE);
		
		String ipmString = shar.getString("IPM", "0.0.0.0");
		EditText ipm = (EditText) rootView.findViewById(R.id.ipm);
		ipm.setText(ipmString);
		
		String ipbString = shar.getString("IPB", "0.0.0.0");
		EditText ipb = (EditText) rootView.findViewById(R.id.ipb);
		ipb.setText(ipbString);
		
		String timeoutString = shar.getString("TIMEOUT", "10");
		EditText timeout = (EditText) rootView.findViewById(R.id.timeout);
		timeout.setText(timeoutString);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);

		((Button) rootView.findViewById(R.id.btn_accept))
		.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences shar = GlobalParams.getInstance().inflaterContext.getSharedPreferences("set", Context.MODE_PRIVATE);
				SharedPreferences.Editor ed = shar.edit();
				
				EditText ipm = (EditText) rootView.findViewById(R.id.ipm);
				ed.putString("IPM", ipm.getText().toString());
				
				EditText ipb = (EditText) rootView.findViewById(R.id.ipb);
				ed.putString("IPB", ipb.getText().toString());
				
				EditText timeout = (EditText) rootView.findViewById(R.id.timeout);
				ed.putString("TIMEOUT", timeout.getText().toString());
				
				ed.commit();
				dismiss();
			}
		});
		
		((Button) rootView.findViewById(R.id.btn_cancel))
		.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		getDialog().setTitle("הגדרות");
		return rootView;
	}

	public void showError() {
		Toast.makeText(getActivity(), "connection failed", Toast.LENGTH_SHORT)
				.show();

	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		((MainActivity) getActivity()).lockDrawer(false);
	}
	
	@Override
	public void onResume() {
		((MainActivity) getActivity()).lockDrawer(true);
		
		super.onResume();
	}
}