package com.example.maptargetfull;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maptargetfull.AddTargetOnLocationDialog.AddTargetOnLocationListener;
import com.example.maptargetfull.EditTargetDialog.EditTargetListener;
import com.example.maptargetfull.PointsDBAccess.Point;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends Fragment implements
		AddTargetOnLocationListener, EditTargetListener {

	public static String TAG = "google_map_fragment";

	private PointsDBAccess mDbHandler;
	public static HashMap<Long, Point> mGooglePoints;
	private Dialog mGpsDialog;
	private boolean mLocationSet = false;
	private MapView mMapView;
	private GoogleMap mMap;
	public View mViewInfoWindow;
	private LocationManager mLocationService;
	private HashMap<Long, Marker> mMarkers;

	static public enum target_type {
		FRIEND, ENEMY
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_googlemap, container,
				false);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity().getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services
													// are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
					getActivity(), requestCode);
			dialog.show();

		} else { // Google Play Services are available
			
			this.mLocationService = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);

			mMapView = (MapView) view.findViewById(R.id.map);
			mMapView.onCreate(savedInstanceState);
			mMapView.onResume();// needed to get the map to display immediately

			MapsInitializer.initialize(getActivity());

			setUpMapIfNeeded(view);
			
			this.mMarkers = new HashMap<Long, Marker>();
			this.mDbHandler = new PointsDBAccess(getActivity());
			mGooglePoints = new HashMap<Long, PointsDBAccess.Point>();
			ArrayList<Point> points = this.mDbHandler.getPoints(true);
			
			if (points.size() != 0) {
				for (Point point : points) {		
					this.addMarkerOnLocation(point.rowID, point.first_name, (point.pointType == 1?target_type.FRIEND:target_type.ENEMY), point.langitude, point.longitude, false);
				}
			}
			
			// Build gps alert
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.cust_dialog);
			builder.setTitle("Location Services Not Active");
			builder.setMessage("It is recommended enable Location Services and GPS");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					// Show location settings when the user acknowledges
					// the alert dialog
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							// Exit Application
							dialogInterface.dismiss();
						}
					});
			this.mGpsDialog = builder.create();

		}
		return view;
	}

	private void setUpMapIfNeeded(View inflatedView) {
		if (mMap == null) {
			mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
		}
	}

	@Override
	public void onResume() {
		
		mMapView.onResume();
		
		this.mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		this.mMap.setMyLocationEnabled(true);
		
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Check if enabled and if not send user to the GPS settings
		if (!this.mLocationService
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			this.mGpsDialog.show();
		}

		this.mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng loc) {
				FragmentManager fm = getFragmentManager();
				AddTargetOnLocationDialog addTargetDialog = AddTargetOnLocationDialog
						.newInstance("Add Target", loc.latitude, loc.longitude, TAG);
				addTargetDialog.setStyle(R.style.cust_dialog,addTargetDialog.getTheme());
				addTargetDialog.show(fm, AddTargetOnLocationDialog.TAG);
			}
		});

		this.mMap
				.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {
						
						FragmentManager fm = getFragmentManager();

						long rowid = getRowIdByMarker(marker);
						
						if (rowid != -1) {
							EditTargetDialog editTargetDialog = EditTargetDialog
									.newInstance("Edit Target", rowid, TAG);
							editTargetDialog.setStyle(R.style.cust_dialog, editTargetDialog.getTheme());
							editTargetDialog.show(fm, EditTargetDialog.TAG);
						} else {
							Toast.makeText(getActivity(), "Error occured. point not found?", Toast.LENGTH_LONG).show();
						}
					}
				});

		this.mLocationService.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
					@Override
					public void onProviderEnabled(String provider) {
						if (mGpsDialog.isShowing()) {
							mGpsDialog.dismiss();
						}
					}

					@Override
					public void onProviderDisabled(String provider) {

					}

					@Override
					public void onLocationChanged(Location location) {
						if (!mLocationSet) {

							adjustMap();
							mLocationSet = true;
							mLocationService.removeUpdates(this);
							getActivity().invalidateOptionsMenu();
						}
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});

		this.adjustMap();
	}

	private void adjustMap() {

		Location location = this.mLocationService
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {

			location = this.mLocationService
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		LatLng loc = null;

		if (location != null) {
			loc = new LatLng(location.getLatitude(), location.getLongitude());
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));

		}

		if (this.mMarkers.size() > 0) {

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			if (location != null) {
				builder.include(loc);
			}

			for (Marker marker : this.mMarkers.values()) {
				builder.include(marker.getPosition());
			}

			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
					builder.build(),
					getResources().getDisplayMetrics().widthPixels,
					getResources().getDisplayMetrics().heightPixels,
					getResources().getDisplayMetrics().heightPixels / 6);
			this.mMap.animateCamera(cu);
		}
	}

	@Override
	public void createPointOnLocation(String name, target_type type, double latitude, double longitude) {
		int pointType = 0;
		
		switch (type) {
		case FRIEND:
			pointType = 1;
			break;
		case ENEMY:
			pointType = 2;
			break;
		default:
			break;
		}
		long rowid = this.mDbHandler.createPoint(name, "", longitude, latitude, true, pointType);
		
		if (rowid == -1) {
			Toast.makeText(getActivity(), "Error creating marker",
					Toast.LENGTH_LONG).show();
		} else {
		
			addMarkerOnLocation(rowid, name, type, latitude, longitude, true);
		
		}
	}
	
	public void addMarkerOnLocation(long rowid, String name, target_type type, double latitude, double longitude, boolean showInfoWindow) {

			Marker destMark = this.mMap.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude)).title(name)
					.snippet(type.toString()));
			
			Point point = this.mDbHandler.new Point();
			
			if (type == target_type.FRIEND) {
				destMark.setIcon(BitmapDescriptorFactory
						.fromResource(R.drawable.friend));
				point.pointType = 1;
			} else {
				destMark.setIcon(BitmapDescriptorFactory
						.fromResource(R.drawable.enemy));
				point.pointType = 2;
			}

			// Put in marker hashtable before showinfowwindow (for image loading purposes)
			this.mMarkers.put(rowid, destMark);
			
			if (showInfoWindow) {
				destMark.showInfoWindow();
			}
			
			point.rowID = rowid;
			point.first_name = name;
			point.last_name = "";
			point.langitude = latitude;
			point.longitude = longitude;
			mGooglePoints.put(rowid, point);
		}

	private class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private View view;

		public CustomInfoWindowAdapter() {
			view = getActivity().getLayoutInflater().inflate(
					R.layout.view_infowindow, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {

			final ImageView image = ((ImageView) view.findViewById(R.id.badge));
			long rowid = getRowIdByMarker(marker);
			
			String imageInSD = Environment.getExternalStorageDirectory().getPath()
					+ "/Pictures/MyCameraApp/" + rowid + ".jpg";
			Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.nophoto2);
			}
			Bitmap resizedBitmap = Bitmap
					.createScaledBitmap(bitmap, 200, 200, true);
			image.setImageBitmap(resizedBitmap);
			
			final String title = marker.getTitle();
			final TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				titleUi.setText(title);
			} else {
				titleUi.setText("");
			}

			final String snippet = marker.getSnippet();
			final TextView snippetUi = ((TextView) view
					.findViewById(R.id.snippet));
			if (snippet != null) {
				snippetUi.setText(snippet);
			} else {
				snippetUi.setText("");
			}

			return view;
		}
	}

	@Override
	public void onDestroyView() {

		MapFragment f = (MapFragment) getFragmentManager().findFragmentById(
				R.id.map);

		if (f != null) {

			getFragmentManager().beginTransaction().remove(f).commit();
			mMap = null;
		}
		super.onDestroyView();
	}

	@Override
	public void deletePoint(long rowid) {
		if (this.mDbHandler.deletePoint(rowid)) {
			mGooglePoints.remove(rowid);
			this.mMarkers.get(rowid).remove();
			this.mMarkers.remove(rowid);
		}
		
	}
	
	@Override
	public void savePoint(Point point) {
		if (this.mDbHandler.updatePoint(point.rowID, point.first_name, point.last_name, point.longitude, point.langitude, true, point.pointType)) {
			mGooglePoints.remove(point.rowID);
			this.mMarkers.get(point.rowID).remove();
			this.mMarkers.remove(point.rowID);
			this.addMarkerOnLocation(point.rowID, point.first_name, (point.pointType == 1?target_type.FRIEND:target_type.ENEMY) , point.langitude, point.longitude, true);
			
		}
		
	}
	
	private long getRowIdByMarker(Marker marker){
		long rowid = -1;
		
		if (this.mMarkers != null) {
			for (int i = 0; i <= this.mMarkers.size(); i++) {
				if (this.mMarkers.values().toArray()[i].equals(marker)) {
					rowid = (Long) this.mMarkers.keySet().toArray()[i];
					break;
				}
			}
		}
		return rowid;
	}

	@Override
	public void imageUpdated(long rowid) {
		this.mMarkers.get(rowid).hideInfoWindow();
		this.mMarkers.get(rowid).showInfoWindow();
	}


}
