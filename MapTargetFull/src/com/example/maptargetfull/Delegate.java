package com.example.maptargetfull;

import us.ba3.me.HaloPulse;
import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarkerMapDelegate;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

public class Delegate implements DynamicMarkerMapDelegate {
private OfflineMap currMap;
	public  Delegate (OfflineMap m){
		this.currMap = m;
	}
	
	@Override
	public void tapOnMarker(String arg0, String arg1, PointF arg2, PointF arg3) {
		// TODO Auto-generated method stub
		Log.w("yes", "works!!!!!");
		GlobalParams.getInstance().Exist = true;
		Location currLoc= GlobalParams.getInstance().myList.get(arg1);
        //setContentView(this.currMap);
		HaloPulse beacon = new HaloPulse();
	    beacon.name = "beacon";
	    beacon.location = currLoc;
	    beacon.minRadius = 65;
	    beacon.maxRadius = 80;
	    beacon.animationDuration = 1.5f;
	    beacon.repeatDelay = 0;
	    beacon.fade = true;
	    beacon.fadeDelay = 1;
	    
	    beacon.zOrder = 10;
	    beacon.lineStyle.strokeColor = Color.WHITE;
	    beacon.lineStyle.outlineColor = Color.rgb(30,151,235);
	    beacon.lineStyle.outlineWidth = 4;
	    currMap.addHaloPulse(beacon);
	}

}
