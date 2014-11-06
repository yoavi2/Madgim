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
	              
	              return mapView;
	        }

		return null;
	}
}
