package com.example.maptargetfull;

import java.util.ArrayList;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import com.example.maptargetfull.GlobalParams.markerType;

public class OfflineMap extends us.ba3.me.MapView {

	public static String TAG = "offline_map";
	private OfflineMap currMap;
	private ArrayList<Location> markersLoc = new ArrayList<Location>();
	private Location loc = new Location();
	
	public OfflineMap(Context context) {
		super(context);
	    GlobalParams.getInstance().frgOfflineMap = ((OfflineMapFragment)GlobalParams.getFragment().findFragmentByTag(OfflineMapFragment.TAG));
	}

    public void deletePoint(String name) {
    	if (GlobalParams.getInstance().PointsDBaccess.deletePointOffline(name))
    	{
    		GlobalParams.getInstance().deletePointByName(name);
    	}
    	
//    	((MainActivity)GlobalParams.getInstance().currActivity).refresh(false);
    	
//    	GlobalParams.getInstance().getView().invalidate();
    }
	
	public void addMarkersLayer(String strLayerName, int zOrder) {

		// Add dynamic marker map layer
		DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
		mapInfo.name = strLayerName;
		mapInfo.zOrder = zOrder;
		mapInfo.delegate = new Delegate(currMap);
		currMap.addMapUsingMapInfo(mapInfo);
	}

	public void addMarkerOnLocationOffline(String strName, markerType mrkType,
			double latitude, double longitude) {

		// Image
		Bitmap bmImage = null;

		// Add a marker
		DynamicMarker marker = new DynamicMarker();
		marker.name = strName;
		
		if (GlobalParams.getInstance().height > 1080) {
			marker.anchorPoint = new PointF(100, 50);
	    }
	    else if (GlobalParams.getInstance().height >= 800) {
			marker.anchorPoint = new PointF(26, 14);
	    }
	    else {
			marker.anchorPoint = new PointF(32, 18);
	    }
//		Add Target
		marker.location.longitude = longitude;
		marker.location.latitude = latitude;
		
		long rowId = GlobalParams.getInstance().getRowidByName(strName);
		boolean isSynched = GlobalParams.getInstance().PointsDBaccess.getSynched(rowId);
		
		if (mrkType == markerType.Tank) {

			if (isSynched) {
				bmImage = BitmapFactory.decodeResource(getResources(),
													   R.drawable.tank);
			}
			else {
				bmImage = BitmapFactory.decodeResource(getResources(),
						   							   R.drawable.tank_no_conn);
			}
			
			marker.setImage(bmImage, false);
			currMap.addDynamicMarkerToMap("Tanks", marker);
		} 
		else if (mrkType == markerType.Truck){
			
			if (isSynched) {
				bmImage = BitmapFactory.decodeResource(getResources(),
													   R.drawable.truck);
			}
			else {
				bmImage = BitmapFactory.decodeResource(getResources(),
						   							   R.drawable.truck_no_conn);
			}

			marker.setImage(bmImage, false);
			currMap.addDynamicMarkerToMap("Trucks", marker);
		}
		else {
			bmImage = BitmapFactory.decodeResource(getResources(),
					   R.drawable.me);
			
			marker.setImage(bmImage, false);
			currMap.addDynamicMarkerToMap("GPS", marker);
		}

		// Add the marker to the markers list
		GlobalParams.getInstance().AddMarker(strName, marker.location);
	}
	
	public void removeMarkerOnLocationOffline(String strName) {
		try {
			currMap.removeDynamicMarkerFromMap("Tanks", strName);
		} catch (Exception e) {
			
		}
		
		try {
			currMap.removeDynamicMarkerFromMap("Trucks", strName);
		} catch (Exception e) {
			
		}
	}

	public void set(OfflineMap mapView) {
		currMap = mapView;

		this.addMarkersLayer("Tanks", 2);
		this.addMarkersLayer("Trucks", 2);
		this.addMarkersLayer("GPS", 2);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
	
		GlobalParams.getInstance().isScaling = true;
		
		return super.onScaleBegin(detector);
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		
		GlobalParams.getInstance().isScaling = false;
		
		super.onScaleEnd(detector);
	}
	
	@Override
	public void onLongPress(MotionEvent arg0) {
		
		int maskedAction = arg0.getActionMasked();
		
		super.onLongPress(arg0);

	    if (!GlobalParams.getInstance().isScaling) {
		
		super.getLocationForPoint(new PointF(arg0.getX(), arg0.getY()),
				new ConvertPointCallback() {

					@Override
					public void convertComplete(Location loc1) {

						try {
							Thread.sleep(50);
						} 
							catch (InterruptedException e) {
						}

//						Bitmap mbit = null;
//
//						if (!GlobalParams.getInstance().Exist) {
//							currMap.removeHaloPulse("beacon");
//
//							// Add the marker to the markers list
//							GlobalParams.getInstance().AddMarker(
//									"Marker" + loc1.latitude, loc1);
//
//							// Add the marker to the map
//							currMap.addMarkerOnLocationOffline("Marker"
//									+ loc1.latitude, markerType.Truck,
//									loc1.latitude, loc1.longitude);
//						}
//
//						GlobalParams.getInstance().Exist = false;
						
						FragmentManager fm = GlobalParams.getFragment();
						AddTargetOnLocationDialog addTargetDialog = AddTargetOnLocationDialog
								.newInstance(GlobalParams.getInstance().currActivity.getString(R.string.title_add), loc1.latitude, loc1.longitude, TAG);
						addTargetDialog.setStyle(R.style.cust_dialog,addTargetDialog.getTheme());
//						Window window = addTargetDialog.getActivity().getWindow();
//						window.setLayout(200, 50);
						addTargetDialog.show(fm, AddTargetOnLocationDialog.TAG);
//						
//						addTargetDialog.getDialog().getWindow().setLayout(20, 20);

					}
				});  
	    }
	}
	
	public void ShowMeTheContextualMenu() {
		if (GlobalParams.getInstance().Exist) {
			GlobalParams.getInstance().frgOfflineMap.AddContextualMenu();
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {

		Bitmap mbit = null;

		// mbit = FontUtil.createLabel("test", labelStyle);
		mbit = BitmapFactory.decodeResource(getResources(), R.drawable.tank);
		super.getLocationForPoint(new PointF(arg0.getX(), arg0.getY()),
				new ConvertPointCallback() {

					@Override
					public void convertComplete(Location loc1) {

						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Bitmap mbit = null;

						ShowMeTheContextualMenu();
						
						if (!GlobalParams.getInstance().Exist) {
							currMap.removeHaloPulse("beacon");
							GlobalParams.getInstance().currMarkerName = null;
							GlobalParams.getInstance().frgOfflineMap.RemoveContextualMenu();
						}

						GlobalParams.getInstance().Exist = false;

					}
				});

		return super.onSingleTapUp(arg0);

	}

}
