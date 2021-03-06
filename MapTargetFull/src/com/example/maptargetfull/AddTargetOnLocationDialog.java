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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.maptargetfull.GoogleMapFragment.target_type;
import com.example.maptargetfull.PointsDBAccess.Point;

public class AddTargetOnLocationDialog extends DialogFragment {

	public static String TAG = "set_target_on_location_dialog";
	private String mCallerTag;
	private EditText mETName;
	private double mLatitude;
	private double mLongitude;

	public interface AddTargetOnLocationListener {
		void createPointOnLocation(String name,
				GoogleMapFragment.target_type type, double latitude,
				double longitude);
	}

	public AddTargetOnLocationDialog() {
		// Empty constructor required for DialogFragment
	}

	public static AddTargetOnLocationDialog newInstance(String title,
			double latitude, double longitude, String callertag) {
		AddTargetOnLocationDialog frag = new AddTargetOnLocationDialog();
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

		this.mCallerTag = getArguments().getString("callertag");
		this.mLatitude = getArguments().getDouble("latitude");
		this.mLongitude = getArguments().getDouble("longitude");

		this.mETName = (EditText) view.findViewById(R.id.et_name);
		this.mETName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mETName.setCompoundDrawables(null, null, null, null);
			}
		});

		((Button) view.findViewById(R.id.btn_accept))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// Validation
						if (mETName.getText().toString().trim().isEmpty()) {

							Toast.makeText(getActivity(),
									getString(R.string.empty_msg),
									Toast.LENGTH_LONG).show();

							// Draw pencil
							Drawable pencil = getResources().getDrawable(
									R.drawable.pencil2);
							pencil.setBounds(0, 0, pencil.getIntrinsicWidth(),
									pencil.getIntrinsicHeight());
							mETName.setCompoundDrawables(null, null, pencil,
									null);
						} else {
							GoogleMapFragment.target_type tt_type = GoogleMapFragment.target_type.FRIEND;
							if (((RadioGroup) getView().findViewById(
									R.id.rg_target_type))
									.getCheckedRadioButtonId() == R.id.rb_enemy) {
								tt_type = GoogleMapFragment.target_type.ENEMY;
							}

//							AddTargetOnLocationListener fragment = (AddTargetOnLocationListener) getActivity()
//									.getFragmentManager().findFragmentByTag(
//											mCallerTag);
//							fragment.createPointOnLocation(mETName.getText()
//									.toString(), tt_type, mLatitude, mLongitude);
							
							GlobalParams.markerType mMarkerType = tt_type == target_type.FRIEND ? GlobalParams.markerType.Tank : GlobalParams.markerType.Truck;
							String strName = mETName.getText().toString();
							
							long rowid;
							
							// Check if exist marker with this name
							if (GlobalParams.isExist(strName)) {
//								Toast.makeText(GlobalParams.getInstance().inflaterContext, 
//										"Marker with this name already exists!", 
//										Toast.LENGTH_LONG).show();
								
								GlobalParams.getInstance().PointsDBaccess.deletePointOffline(strName);
								GlobalParams.getInstance().removeFrommPoints(GlobalParams.getInstance().getRowidByName(strName));
								GlobalParams.getCurrMap().removeMarkerOnLocationOffline(strName);
								
								rowid = GlobalParams.getInstance().PointsDBaccess.createPoint(strName, 
										   "", 
										   mLongitude, 
										   mLatitude, 
										   false, 
										   tt_type == target_type.FRIEND ? 1 : 2);
							}
							else {
								rowid = GlobalParams.getInstance().PointsDBaccess.createPoint(strName, 
										   "", 
										   mLongitude, 
										   mLatitude, 
										   false, 
										   tt_type == target_type.FRIEND ? 1 : 2);
							}
							
							if (rowid != -1)
					    	{
					    		Point p = GlobalParams.getInstance().PointsDBaccess.new Point();
					    		p.first_name = strName;
					    		p.longitude = mLongitude;
					    		p.langitude = mLatitude;
					    		p.pointType = tt_type == target_type.FRIEND ? 1 : 2;
					    		p.rowID = rowid;
					    		GlobalParams.getInstance().addPoint(p);
								
								GlobalParams.getCurrMap().addMarkerOnLocationOffline(strName, 
									     mMarkerType, 
										 mLatitude, 
										 mLongitude);
					    	}
							
							// Reload current fragment
							Fragment frg = null;
							frg = getFragmentManager().findFragmentByTag("list_frame");
							final FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.detach(frg);
							ft.attach(frg);
							ft.commit();

							dismiss();
						}
					}
				});

		((Button) view.findViewById(R.id.btn_cancel))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});

		String title = getArguments().getString("title", getString(R.string.title_add));
		getDialog().setTitle(title);
		// Show soft keyboard automatically
		// this.mETName.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}

}
