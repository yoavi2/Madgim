package com.example.maptargetfull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import us.ba3.me.ImageDataType;
import us.ba3.me.Location;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.markers.DynamicMarker;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maptargetfull.GlobalParams.markerType;
import com.example.maptargetfull.PointsDBAccess.Point;

public class OfflineMapFragment extends Fragment {

	public static String TAG = "offline_map";
	private PointsDBAccess mDbHandler;
	public static HashMap<Long, Point> mOfflineMapPoints;
	private HashMap<Long, DynamicMarker> mMarkers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (savedInstanceState == null) {

			// Extract the mbtiles map asset if needed
			String sourceAssetName = "open-streets-dc.mbtiles";
			String targetFileName = getActivity().getFilesDir()
					.getAbsolutePath() + File.separator + sourceAssetName;
			File destFile = new File(targetFileName);
			if (!destFile.exists()) {
				AssetExtractor extractor = new AssetExtractor(getActivity(),
						sourceAssetName, targetFileName);
				extractor.extractAsset(false);
			}

			// Add the mb tiles map.
			// mymap mapView = (mymap)this.findViewById(R.id.mapView1);
			OfflineMap mapView = new OfflineMap(getActivity());
			mapView.set(mapView);
			mapView.addMBTilesMap("mapquest", targetFileName, "grayGrid",
					ImageDataType.kImageDataTypePNG, false, 2,
					MapLoadingStrategy.kLowestDetailFirst);

			// Zoom into washington D.C.
			mapView.setLocationThatFitsCoordinates(new Location(38.848,
					-77.1127), new Location(38.933, -76.9665), 0, 0);

			this.mMarkers = new HashMap<Long, DynamicMarker>();
			this.mDbHandler = new PointsDBAccess(getActivity());
			mOfflineMapPoints = new HashMap<Long, PointsDBAccess.Point>();
			ArrayList<Point> points = this.mDbHandler.getPoints(true);

			if (points.size() != 0) {
				for (Point point : points) {
					mapView.addMarkerOnLocationOffline(point.first_name,
							point.pointType == 1 ? markerType.Tank
									: markerType.Truck, point.langitude,
							point.longitude);
				}
			}

			return mapView;
		}

		return null;
	}
}
