package com.example.maptargetfull;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.maptargetfull.GoogleMapFragment.target_type;
import com.example.maptargetfull.PointsDBAccess.Point;

public class PopupDialog extends DialogFragment {

	public static String TAG = "set_target_on_location_dialog";
	private String mCallerTag;
	private EditText mETName;
	private double mLatitude;
	private double mLongitude;

	public PopupDialog() {
		// Empty constructor required for DialogFragment
	}

	public static PopupDialog newInstance(String title,
			double latitude, double longitude, String callertag) {
		PopupDialog frag = new PopupDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putDouble("latitude", latitude);
		args.putDouble("longitude", longitude);
		args.putString("callertag", callertag);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_set_target_on_location,
				container);

		((Button) view.findViewById(R.id.btn_accept))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});

		((Button) view.findViewById(R.id.btn_cancel))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});

		String title = getArguments().getString("title", "Test");
		
	    final PopupWindow popupWindow = 
	    	new PopupWindow(view, 150, 300);  
	             
	    popupWindow.showAsDropDown(container, 50, -30);
		
//		getDialog().setTitle(title);
//		getDialog().getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}

}
