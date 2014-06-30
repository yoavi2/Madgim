package com.example.maptargetfull;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maptargetfull.AddTargetOnLocationDialog.AddTargetOnLocationListener;
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
		AddTargetOnLocationListener {

	public static String TAG = "google_map_fragment";

	private Dialog mGpsDialog;
	private boolean mLocationSet = false;
	private MapView mMapView;
	private Bundle mBundle;
	private GoogleMap mMap;
	public View mViewInfoWindow;
	private LocationManager mLocationService;
	private ArrayList<Marker> mMarkers;

	static public enum target_type {
		FRIEND, ENEMY
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBundle = savedInstanceState;

		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_googlemap, container,
				false);

		this.mLocationService = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);

		mMapView = (MapView) view.findViewById(R.id.map);
		mMapView.onCreate(mBundle);
		mMapView.onResume();// needed to get the map to display immediately

		MapsInitializer.initialize(getActivity());

		setUpMapIfNeeded(view);

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

		// Build gps alert
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
		
		// Check if enabled and if not send user to the GPS settings
		if (!this.mLocationService
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			this.mGpsDialog.show();
		}

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

			this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			this.mMap.setMyLocationEnabled(true);
			this.mMarkers = new ArrayList<Marker>();

			this.mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
				@Override
				public void onMapLongClick(LatLng loc) {
					android.app.FragmentManager fm = getFragmentManager();
					AddTargetOnLocationDialog addTargetDialog = AddTargetOnLocationDialog
							.newInstance("Add Target", loc);
					addTargetDialog.show(fm, AddTargetOnLocationDialog.TAG);
				}
			});

			this.mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

			this.mMap
					.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
						@Override
						public void onInfoWindowClick(Marker marker) {

						}
					});

			this.mLocationService.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0,
					new LocationListener() {
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
						public void onStatusChanged(String provider,
								int status, Bundle extras) {
						}
					});

			this.adjustMap();
		}

	}

	private void adjustMap() {

		Location location = this.mLocationService
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {
			
			location = this.mLocationService
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		// Last resort 
		if (location == null) {
			location = this.mMap.getMyLocation();
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

			for (Marker marker : this.mMarkers) {
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

	public void addMarkerOnLocation(String name, target_type type, LatLng loc) {

		Marker destMark = this.mMap.addMarker(new MarkerOptions().position(loc)
				.title(name).snippet(type.toString()));

		if (type == target_type.FRIEND) {
			destMark.setIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.friend));
		} else {
			destMark.setIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.enemy));
		}

		destMark.showInfoWindow();
		this.mMarkers.add(destMark);

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
			image.setImageResource(R.drawable.ic_launcher);
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

}
