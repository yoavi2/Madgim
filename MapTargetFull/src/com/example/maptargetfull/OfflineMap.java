package com.example.maptargetfull;

import java.util.ArrayList;

import us.ba3.me.ConvertPointCallback;
import us.ba3.me.HaloPulse;
import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarker;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class OfflineMap extends us.ba3.me.MapView {
 OfflineMap currMap;
 public int x;
	public int y;
 ArrayList<Location> markersLoc = new ArrayList<Location>();
private Location loc = new Location();
	public OfflineMap(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void set(OfflineMap mapView) {
		// TODO Auto-generated method stub
		currMap = mapView;
		
	}
	@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub

			
			Bitmap mbit = null;
	        
	       // mbit = FontUtil.createLabel("test", labelStyle);
			mbit = BitmapFactory.decodeResource(getResources(),
       				R.drawable.tank);
	        super.getLocationForPoint(new PointF(arg0.getX(), arg0.getY()), new ConvertPointCallback(){
				
				
	        	@Override
				public void convertComplete(Location loc1) {
	        		
	        		try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Bitmap mbit = null;
					currMap.hideDynamicMarker("Circle","marker2" );

					
					if(!GlobalParams.getInstance().Exist)
					{
					    currMap.removeHaloPulse("beacon");

						//currMap.removeDynamicMarkerFromMap("Cirlce", "marker2");
				    GlobalParams.getInstance().AddMarker("marker" + loc1.longitude, loc1);
			        markersLoc.add(new Location(loc1.latitude, loc1.longitude));
				       // mbit = FontUtil.createLabel("test", labelStyle);
						mbit = BitmapFactory.decodeResource(getResources(),
			       				R.drawable.tank);
					/*
						DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
			              mapInfo.delegate = new Delegate(currMap);
			              mapInfo.name = "Markers" + loc1.latitude;
			              mapInfo.zOrder = 2;
			              currMap.addMapUsingMapInfo(mapInfo);*/
						
					loc.longitude = loc1.longitude;
					loc.latitude = loc1.latitude;
					Log.w("onTouch", "lon:" + loc.longitude + " lat:" + loc.latitude);
					
					  DynamicMarker marker = new DynamicMarker();
				        marker.name = "marker" + loc1.longitude;
				        marker.setImage(mbit, false);
				        marker.anchorPoint = new PointF(57,32);
				        marker.location.longitude = loc1.longitude;
				        marker.location.latitude = loc1.latitude;
				        currMap.addDynamicMarkerToMap("Markers", marker);
				        
					}
					else{
						
						
						/*mbit = BitmapFactory.decodeResource(getResources(),
		         				R.drawable.rad);
		            //Add a marker
		              DynamicMarker marker2 = new DynamicMarker();
		              marker2.name = "marker2";
		              marker2.setImage(mbit, false);
		              marker2.anchorPoint = new PointF(50,50);
		              marker2.location.longitude = loc1.longitude;
		              marker2.location.latitude = loc1.latitude;
		              
		              currMap.addDynamicMarkerToMap("Circle", marker2);*/
					}
					GlobalParams.getInstance().Exist = false;

				}});
	        
	        
	        
	      
	        return super.onSingleTapUp(arg0);
			
		}

}
