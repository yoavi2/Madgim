package com.example.maptargetfull;

//import com.technotalkative.contextualactionbarsingle.MainActivity.ActionBarCallBack;

import java.io.ObjectInputStream.GetField;

import us.ba3.me.HaloPulse;
import us.ba3.me.Location;
import us.ba3.me.markers.DynamicMarkerMapDelegate;
import ActionMode.ActionModeCallback;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.MenuItem;

public class Delegate implements DynamicMarkerMapDelegate {
private OfflineMap currMap;
	public  Delegate (OfflineMap m){
		this.currMap = m;
	}
	
	@Override
	public void tapOnMarker(String arg0, String arg1, PointF arg2, PointF arg3) {
	    
		GlobalParams.getInstance().Exist = true;
		Location currLoc= GlobalParams.getInstance().myList.get(arg1);
		HaloPulse beacon = new HaloPulse();
	    beacon.name = "beacon";
	    beacon.location = currLoc;
	    beacon.minRadius = 40;
	    beacon.maxRadius = 65;
	    beacon.animationDuration = 1.5f;
	    beacon.repeatDelay = 0;
	    beacon.fade = true;
	    beacon.fadeDelay = 1;
	    
	    beacon.zOrder = 3;
	    beacon.lineStyle.strokeColor = Color.WHITE;
	    beacon.lineStyle.outlineColor = Color.rgb(30,151,235);
	    beacon.lineStyle.outlineWidth = 4;
	    currMap.addHaloPulse(beacon);
	    
	    currMap.setLocation(currLoc, 0.3);
//	    GlobalParams.getInstance().inflater.inflate(R.menu.currmarker, GlobalParams.getMenu());
	    ((OfflineMapFragment)GlobalParams.getFragment().findFragmentByTag(OfflineMapFragment.TAG)).AddContextualMenu();
	}

}
