package com.example.maptargetfull;

//import com.technotalkative.contextualactionbarsingle.MainActivity.ActionBarCallBack;

import us.ba3.me.HaloPulse;
import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarkerMapDelegate;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

public class Delegate implements DynamicMarkerMapDelegate {
private OfflineMap currMap;
	public  Delegate (OfflineMap m){
		this.currMap = m;
	}
	
	@Override
	public void tapOnMarker(String arg0, String arg1, PointF arg2, PointF arg3) {
	    
		if (!arg0.equals("GPS")) {
			GlobalParams.getInstance().Exist = true;
			Location currLoc= GlobalParams.getInstance().myList.get(arg1);
			HaloPulse beacon = new HaloPulse();
		    beacon.name = "beacon";
		    beacon.location = currLoc;

		    if (GlobalParams.getInstance().height > 1080) {
			    beacon.minRadius = 120;
			    beacon.maxRadius = 150;
			    beacon.lineStyle.outlineWidth = 6;
		    }
		    else if (GlobalParams.getInstance().height >= 800) {
			    beacon.minRadius = 32;
			    beacon.maxRadius = 47;
			    beacon.lineStyle.outlineWidth = 2;
		    }
		    else {
			    beacon.minRadius = 40;
			    beacon.maxRadius = 65;
			    beacon.lineStyle.outlineWidth = 3;
		    }
		    
		    beacon.animationDuration = 1.5f;
		    beacon.repeatDelay = 0;
		    beacon.fade = true;
		    beacon.fadeDelay = 1;
		    
		    beacon.zOrder = 3;
		    beacon.lineStyle.strokeColor = Color.WHITE;
		    beacon.lineStyle.outlineColor = Color.rgb(30,151,235);
		    currMap.addHaloPulse(beacon);
		    
		    currMap.setLocation(currLoc, 0.3);
		    GlobalParams.getInstance().currMarkerName = arg1;
		    
		    if (arg0.equals("Tanks")) {
		    	GlobalParams.getInstance().currMarkerType = GlobalParams.markerType.Tank;
		    }
		    else {
		    	GlobalParams.getInstance().currMarkerType = GlobalParams.markerType.Truck;
		    }
		    
		    
		}
	}

}
