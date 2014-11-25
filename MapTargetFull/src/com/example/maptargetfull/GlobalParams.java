package com.example.maptargetfull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;

import us.ba3.me.ImageDataType;
import us.ba3.me.Location;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.markers.DynamicMarker;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maptargetfull.PointsDBAccess.Point;
import com.google.android.gms.maps.model.LatLng;

public class GlobalParams {
	public Boolean isOffline = true;
	public Boolean isScaling = false;
	public enum markerType {Tank, Truck, GPS};
	public markerType currMarkerType;
	public static Context inflaterContext;
	public static ViewGroup viewGroup;
	public static FragmentManager mFragmentManager;
	public static OfflineMap mCurrMap; 
	public static android.view.Menu myMenu;
	private boolean ShowEenemy;
	public LocationManager mLocationService;
	private boolean ShowFriend;
	private DrawSample mydraw;
	public PointsDBAccess PointsDBaccess;
	public int selectedItem;
	public ProgressDialog pdialog;
	public String syncFinished = "com.example.maptargetfull.sync_finished";
	public String syncSucceeded = "SUCCEEDED";
	public SecondFragment listFriends;
	private Fragment frag;
	public MenuInflater inflater;
	public ActionMode mActionMode;
	public ActionMode.Callback mActionModeCallback;
	public OfflineMapFragment frgOfflineMap;
	public Activity currActivity;
	public String currMarkerName;
	private ArrayList<Point> mPoints = new ArrayList<Point>();
	public boolean Exist;
	public Hashtable<String, Location> myList = new Hashtable<String, Location>();
	public Hashtable<String, Point> myPoints = new Hashtable<String, Point>();
	public static PointsDBAccess mDbHandler;
	public static HashMap<Long, Point> mOfflineMapPoints;
	public static HashMap<Long, DynamicMarker> mMarkers;
	public static String targetFileName;
	public int height;
	public int width;
	
	public static boolean isExist(String markerName) {
		for (Point point : GlobalParams.getInstance().mPoints) {
			if (point.first_name.equals(markerName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void removeFrommPoints(long rowid) {
		for (Point point : GlobalParams.getInstance().mPoints) {
			if (point.rowID == rowid) {
				mPoints.remove(point);
			}
		}
	}
	
	public void adjustMap() {

		android.location.Location location = this.mLocationService
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {

			location = this.mLocationService
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		LatLng loc = null;

		if (location != null) {
			loc = new LatLng(location.getLatitude(), location.getLongitude());
			GlobalParams.getInstance().mCurrMap.addMarkerOnLocationOffline("MyLoc", markerType.GPS, loc.latitude, loc.longitude);
			//.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));

		}
	}
	
	public static void refreshMarkers() {
		
		if (GlobalParams.getInstance().mPoints.size() != 0) {
			for (Point point : GlobalParams.getInstance().mPoints) {
				if (GlobalParams.getInstance().isOffline) {
					mCurrMap.removeDynamicMarkerFromMap("offline", point.first_name);
				}
				else {
					mCurrMap.removeDynamicMarkerFromMap("online", point.first_name);
				}
			}
		}
		
		addMarkersFromDB();
	}
	
	public static void addMarkersFromDB() {
		mMarkers = new HashMap<Long, DynamicMarker>();
		mDbHandler = new PointsDBAccess(GlobalParams.getInstance().currActivity);
		mOfflineMapPoints = new HashMap<Long, PointsDBAccess.Point>();
		GlobalParams.getInstance().mPoints = mDbHandler.getPoints(false);

		getInstance().adjustMap();
		
		if (GlobalParams.getInstance().mPoints.size() != 0) {
			for (Point point : GlobalParams.getInstance().mPoints) {
				mCurrMap.addMarkerOnLocationOffline(point.first_name,
						point.pointType == 1 ? markerType.Tank
								: markerType.Truck, point.langitude,
						point.longitude);
			}
		}
	}
	
	public static void goToOnlineMap() {
		mCurrMap.removeMap("offline", false);
		mCurrMap.addInternetMap("online",
				"http://server.arcgisonline.com/arcgis/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}.jpg",
				"", 		//Subdomains
				19,			//Max Level
				2,			//zOrder
				3,			//Number of simultaneous downloads
				true,		//Use cache
				false		//No alpha
				);
		
		mCurrMap.set(mCurrMap);
	}
	
	public static void goToOfflineMap() {
		mCurrMap.removeMap("online", false);
		
		View.inflate(inflaterContext, R.layout.fragment_offlinemap, viewGroup);
		
		mCurrMap.set(mCurrMap);
		
		try {
			mCurrMap.addMBTilesMap("offline", "/storage/emulated/0/Download/IsraelMap.mbtiles", "grayGrid",
					ImageDataType.kImageDataTypePNG, false, 2,
					MapLoadingStrategy.kLowestDetailFirst);
		} catch (Exception e) {
			Toast.makeText(GlobalParams.getInstance().inflaterContext, "קובץ המפות לא נמצא", Toast.LENGTH_LONG).show();
		}
	}
	
	public static void setMenu(android.view.Menu menu) {
		myMenu = menu;
	}
	
	public static android.view.Menu getMenu() {
		return myMenu;
	}
	
	public static void setCurrMap(OfflineMap mMap) {
		mCurrMap = mMap;
	}
	
	public static OfflineMap getCurrMap() {
		return mCurrMap;
	}
	
	public static void setFragment(FragmentManager frg) {
		mFragmentManager = frg;
	}
	
	public static FragmentManager getFragment() {
		return mFragmentManager;
	}
	
	public void setProgress(Fragment p) {
		this.frag = p;
	}
	
	public void AddMarker(String strName, Location loc){
		myList.put(strName, loc);
	}
	
	public void UpdateMarker(String strName, Location loc){
		myList.put(strName, loc);
	}
	
	public void RemoveMarker(String strName){
		myList.remove(strName);
	}

	public Fragment getf() {
		return this.frag;
	}

	public void setEnemy(boolean show) {
		this.ShowEenemy = show;
	}

	public void setFriend(boolean show) {
		this.ShowFriend = show;
	}

	public void clearList() {
		this.mPoints.clear();
	}

	public boolean getEenemy() {
		return this.ShowEenemy;
	}

	public boolean getFriend() {
		return this.ShowFriend;
	}

	GlobalParams(boolean a, boolean b) {
		this.ShowEenemy = a;
		this.ShowFriend = b;
	}

	public void setview(DrawSample view) {
		this.mydraw = view;
	}

	public DrawSample getView() {
		return this.mydraw;
	}

	public void addPoint(Point newPoint) {
		mPoints.add(newPoint);
	}
	
	public void updatePoint(Point updPoint){
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).rowID == updPoint.rowID) {
				mPoints.set(i, updPoint);
			}
		}
	}
	
	public void deletePointByRowid(long rowid)
	{
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).rowID == rowid) {
				mPoints.remove(i);
			}
		}
	}
	
	public void deletePointByName(String name)
	{
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).first_name.equals(name)) {
				mPoints.remove(i);
			}
		}
	}
	
	public Point getPointByName(String name)
	{
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).first_name == name) {
				return mPoints.get(i);
			}
		}
		return null;
	}
	
	public Point getPointByRowid(long rowid)
	{
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).rowID == rowid) {
				return mPoints.get(i);
			}
		}
		return null;
	}
	
	public long getRowidByName(String name)
	{
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).first_name.equals(name)) {
				return mPoints.get(i).rowID;
			}
		}
		return -1;
	}
	
	public Point getSpecificPoint(int position) {
		return this.mPoints.get(position);
	}

	public ArrayList<Point> getPoints() {
		return this.mPoints;
	}

	public void DeletePoint(String name) {
		for (Point iterable : mPoints) {
			if (iterable.first_name.equals(name)) {
				mPoints.remove(iterable);
				break;
			}
		}
	}

	public void deletePoints(int position) {
		this.mPoints.remove(position);
	}

	public void deletePoints(Collection<Point> collection) {
		this.mPoints.removeAll(collection);
	}

	static final private GlobalParams INSTANCE = new GlobalParams(true, true);

	static public GlobalParams getInstance() {
		return INSTANCE;
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(Point point){
          return Uri.fromFile(getOutputMediaFile(point));
    }

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(Point point) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ point.rowID
				+ ".jpg");
		return mediaFile;
	}
	
	public static void storeImage(Bitmap image, Point point) {
		File pictureFile = getOutputMediaFile(point);
		if (pictureFile != null) {
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				image.compress(Bitmap.CompressFormat.PNG, 30, fos);
				fos.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}
	

	public static class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int rowid = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	    	rowid = params[0];
			String imageInSD = Environment.getExternalStorageDirectory().getPath()
					+ "/Pictures/MyCameraApp/" + rowid + ".jpg";


			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageInSD, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, 200,
					200);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(imageInSD, options);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	    	if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            final BitmapWorkerTask bitmapWorkerTask =
	                    getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }	    
	}
	
	public static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap,
	            BitmapWorkerTask bitmapWorkerTask) {
	        super(res, bitmap);
	        bitmapWorkerTaskReference =
	            new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
	    }

	    public BitmapWorkerTask getBitmapWorkerTask() {
	        return bitmapWorkerTaskReference.get();
	    }
	}
	
	public static boolean cancelPotentialWork(int data, ImageView imageView) {
	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final int bitmapData = bitmapWorkerTask.rowid;
	        // If bitmapData is not yet set or it differs from the new data
	        if (bitmapData == 0 || bitmapData != data) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	            // The same work is already in progress
	            return false;
	        }
	    }
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	
	public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
		}
	
	public static void loadBitmap(long rowid, ImageView imageView, Context context) {
	    if (GlobalParams.cancelPotentialWork((int)rowid, imageView)) {
	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(),R.drawable.nophoto2), task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (int)rowid);
		}
	}
	
	public static void loadSagiBitmap(long rowid, ImageView imageView, Context context) {
		
		Point p =GlobalParams.getInstance().getPointByRowid(rowid);
		Bitmap bmImage;
		
		if (p.pointType == 1) {
			bmImage = BitmapFactory.decodeResource(context.getResources(),
					   R.drawable.tank_white);
		}
		else {
			bmImage = BitmapFactory.decodeResource(context.getResources(),
					   R.drawable.truck_white);
		}
		
//		final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		final BitmapWorkerTask taskEmpty = new BitmapWorkerTask(new ImageView(context));
        final AsyncDrawable asyncDrawable =
                new AsyncDrawable(context.getResources(), bmImage, taskEmpty);
        imageView.setImageDrawable(asyncDrawable);
        taskEmpty.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (int)rowid);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}
