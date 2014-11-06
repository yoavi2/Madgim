package com.example.maptargetfull;

import java.io.File;

import us.ba3.me.ImageDataType;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.markers.DynamicMarker;
import us.ba3.me.markers.DynamicMarkerMapInfo;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import us.ba3.me.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OfflineMapFragment extends Fragment {

	public static String TAG = "offline_map";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		 if (savedInstanceState == null) {
             
	          //Extract the mbtiles map asset if needed
	            String sourceAssetName = "open-streets-dc.mbtiles";
	            String targetFileName = getActivity().getFilesDir().getAbsolutePath() +
	                    File.separator + sourceAssetName;
	            File destFile = new File(targetFileName);
	            if(!destFile.exists()) {
	                    AssetExtractor extractor = new AssetExtractor(getActivity(),
	                            sourceAssetName,targetFileName);
	                    extractor.extractAsset(false);
	            }
	             
	             
	            //Add the mb tiles map.
	           // mymap mapView = (mymap)this.findViewById(R.id.mapView1);
	            OfflineMap mapView = new OfflineMap(getActivity());
	            mapView.set(mapView);
	            mapView.addMBTilesMap("mapquest", targetFileName, "grayGrid",
	                    ImageDataType.kImageDataTypePNG, false, 2,MapLoadingStrategy.kLowestDetailFirst );
	             
	            // Zoom into washington D.C.
	              mapView.setLocationThatFitsCoordinates(
	                    new Location(38.848,-77.1127),
	                    new Location(38.933,-76.9665),
	                    0, 0);
	              
	              //Get the map view and add a street map.
	            //  MapView mapView = (MapView)this.findViewById(R.id.mapView1);
	             /* mapView.addInternetMap("MapQuest",
	      				"http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.jpg",
	      				"", 		//Subdomains
	      				19,			//Max Level
	      				2,			//zOrder
	      				3,			//Number of simultaneous downloads
	      				true,		//Use cache
	      				false		//No alpha
	      				);*/
	              //Add dynamic marker map layer
	              DynamicMarkerMapInfo mapInfo = new DynamicMarkerMapInfo();
	              mapInfo.delegate = new Delegate(mapView);
	              mapInfo.name = "Markers";
	              mapInfo.zOrder = 10;
	              mapView.addMapUsingMapInfo(mapInfo);
	               Bitmap mbit = null;
	               
	               mbit = BitmapFactory.decodeResource(getResources(),
	       				R.drawable.tank);
	              //Add a marker
	              DynamicMarker marker = new DynamicMarker();
	              marker.name = "marker1";
	              marker.setImage(mbit, false);
	              marker.anchorPoint = new PointF(16,16);
	              marker.location.longitude = -77.1127;
	              marker.location.latitude = 38.848;
	              mapView.addDynamicMarkerToMap("mapquest", marker);
	              return mapView;
	        }
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return null;
	}
}
