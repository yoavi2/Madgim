package com.example.maptargetfull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

import com.example.maptargetfull.PointsDBAccess.Point;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class GlobalParams {
	private boolean ShowEenemy;
	private boolean ShowFriend;
	private DrawSample mydraw;
	public PointsDBAccess PointsDBaccess;
	public int selectedItem;
	public ProgressDialog pdialog;
	public String syncFinished = "com.example.maptargetfull.sync_finished";
	public String syncSucceeded = "SUCCEEDED";
	public SecondFragment listFriends;
	private Fragment frag;
	private ArrayList<Point> mPoints = new ArrayList<Point>();

	public void setProgress(Fragment p) {
		this.frag = p;
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
	
	public Point getPointByRowid(long rowid)
	{
		for (int i = 0; i < mPoints.size(); i++) {
			if (mPoints.get(i).rowID == rowid) {
				return mPoints.get(i);
			}
		}
		return null;
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
