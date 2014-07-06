package com.example.maptargetfull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.example.maptargetfull.PointsDBAccess.Point;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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
	private ArrayList<Friend> MyFriends = new ArrayList<Friend>();

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
		this.MyFriends.clear();
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

	public void addFriend(Friend newFriend) {
		MyFriends.add(newFriend);
	}

	public Friend getSpecificFriend(int position) {
		return this.MyFriends.get(position);
	}

	public ArrayList<Friend> getFriends() {
		return this.MyFriends;
	}

	public void DeleteFriend(String name) {
		for (Friend iterable : MyFriends) {
			if (iterable.getName().equals(name)) {
				MyFriends.remove(iterable);
				break;
			}
		}
	}

	public void deleteFriend(int position) {
		this.MyFriends.remove(position);
	}

	public void deleteFriends(Collection<Friend> collection) {
		this.MyFriends.removeAll(collection);
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
				+ point.first_name + "_" + point.last_name + "_" + point.rowID
				+ ".jpg");
		return mediaFile;
	}
}
