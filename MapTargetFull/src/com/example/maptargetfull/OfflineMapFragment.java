package com.example.maptargetfull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import us.ba3.me.ImageDataType;
import us.ba3.me.Location;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.markers.DynamicMarker;
import ActionMode.ActionModeCallback;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.example.maptargetfull.EditTargetDialog.EditTargetListener;
import com.example.maptargetfull.GlobalParams.markerType;
import com.example.maptargetfull.PointsDBAccess.Point;

public class OfflineMapFragment extends Fragment {

	public static String TAG = "offline_map";
	private PointsDBAccess mDbHandler;
	public static HashMap<Long, Point> mOfflineMapPoints;
	private HashMap<Long, DynamicMarker> mMarkers;
	private ActionMode mActionMode;
	private OfflineMap mapView;
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//	                                ContextMenuInfo menuInfo) {
//	    super.onCreateContextMenu(menu, v, menuInfo);
//	    MenuInflater inflater = this.getActivity().getMenuInflater();
//	    inflater.inflate(R.menu.main, menu);
//	    
//	    GlobalParams.setMenu(menu);
//	    GlobalParams.getInstance().inflater = inflater;
//	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//	    switch (item.getItemId()) {
//	        case R.id.item_delete:
//	        	deletePoint(GlobalParams.getInstance().currMarkerName);
//				
//	            return true;
//	        case R.id.item_info:
//
//				
//	            return true;
//	        default:
//	            return super.onContextItemSelected(item);
//	    }
//	}
		
	public void AddContextualMenu() {

        // Start the CAB using the ActionMode.Callback defined above
		if (mActionMode == null) {
	    	GlobalParams.getInstance().mActionModeCallback = new ActionModeCallback();
		}
		
		mActionMode = getActivity().startActionMode(GlobalParams.getInstance().mActionModeCallback);
	}
	
	public void RemoveContextualMenu() {
		
		if (mActionMode != null) {
			mActionMode.finish();
		}
	}
	
//	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//	    // Inflate the menu for the CAB
//		Toast t = new Toast(getActivity());
//		t.setText("test");
//		t.show();
//	    return true;
//	}
//	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
	}
	
//    class ActionBarCallBackTest implements ActionMode.Callback {
//    	 
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//    	    switch (item.getItemId()) {
//		        case R.id.item_delete:
//		        	deletePoint(GlobalParams.getInstance().currMarkerName);
//					
//		            return true;
//		        case R.id.item_info:
//	
//		            return true;
//		        default:
//		            return false;
//		    }
//        }
// 
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            // TODO Auto-generated method stub
//            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
//            return true;
//        }
// 
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            // TODO Auto-generated method stub
// 
//        }
// 
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            // TODO Auto-generated method stub
// 
//            mode.setTitle("שגיא הוא אלוף!");
//            return false;
//        }
// 
//    }
	
//	public void addMarkersFromDB() {
//		this.mMarkers = new HashMap<Long, DynamicMarker>();
//		this.mDbHandler = new PointsDBAccess(getActivity());
//		mOfflineMapPoints = new HashMap<Long, PointsDBAccess.Point>();
//		ArrayList<Point> points = this.mDbHandler.getPoints(false);
//
//		if (points.size() != 0) {
//			for (Point point : points) {
//				mapView.addMarkerOnLocationOffline(point.first_name,
//						point.pointType == 1 ? markerType.Tank
//								: markerType.Truck, point.langitude,
//						point.longitude);
//			}
//		}
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (savedInstanceState == null) {

			GlobalParams.setFragment(getFragmentManager());
			
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
			
			GlobalParams.inflaterContext = inflater.getContext();
			GlobalParams.viewGroup = container;
			
			mapView.inflate(inflater.getContext(), R.layout.fragment_offlinemap, container);
			
			mapView.set(mapView);
			mapView.addMBTilesMap("offline", targetFileName, "grayGrid",
					ImageDataType.kImageDataTypePNG, false, 2,
					MapLoadingStrategy.kLowestDetailFirst);

			GlobalParams.setCurrMap(mapView);
			
			// Zoom into washington D.C.
			mapView.setLocationThatFitsCoordinates(new Location(38.848,
					-77.1127), new Location(38.933, -76.9665), 0, 0);

//			this.addMarkersFromDB();
			GlobalParams.addMarkersFromDB();
			
		    this.getActivity().registerForContextMenu(mapView);
		    
			return mapView;
		}

		return null;
	}
}
