package com.example.maptargetfull;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FirstFragment extends Fragment implements OnTouchListener,OnLongClickListener,android.content.DialogInterface.OnClickListener, OnClickListener{
    
	public static String TAG = "Map";
	
    private DrawSample surface = null;
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private Friend currFriend;
    public static final int MEDIA_TYPE_IMAGE = 1;
   
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.firstfragment, container, false);

	//	Bitmap frameBuffer = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Config.ARGB_8888);
     //   surface = new DrawSample(getActivity(), null);
        surface = (DrawSample) rootView.findViewById(R.id.rect);
        surface.setLongClickable(true);
        surface.setOnLongClickListener(this);
		dialog =  new Dialog(getActivity(), R.style.cust_dialog);
		
	    builder = new AlertDialog.Builder(getActivity());
		builder.setPositiveButton("Add new friend" , this);
		builder.setNegativeButton("Cancel", this);
		builder.setTitle("Map management");
    //    view = surface;
        GlobalParams.getInstance().setview(surface);
        
     //   surface = (DrawSample) getView().findViewById(R.id.rect);
        surface.setOnTouchListener(this);
        return rootView;
    }

    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	
    	 boolean isMatch = false;

    	if (event.getAction() == MotionEvent.ACTION_DOWN) 
		{
    		Bitmap mBitmap = null;
			mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
    		for (Friend iter : GlobalParams.getInstance().getFriends()) {
				
			
    		Rect guess = new Rect(iter.getWidth(),iter.getHeight(),iter.getWidth() +
    				mBitmap.getWidth(),iter.getHeight() + mBitmap.getHeight());
			if (guess.contains((int)event.getX(),(int)event.getY()))
			{
			//  Set the selected friend in the global parameter	
				this.currFriend = iter;
				
				isMatch = true;
				
//		    	Toast.makeText(v.getContext(),"clicked!" ,Toast.LENGTH_SHORT).show();
				//dialog.show(getSupportFragmentManager(),"test");
		
		    	dialog.setContentView(R.layout.customdialog);
		    	
		    	TextView FirstName = (TextView) dialog.findViewById(R.id.FirstName);
		    	ImageView image    = (ImageView)dialog.findViewById(R.id.image);
        		Button btnSave     = (Button)   dialog.findViewById(R.id.saveButton);
        		Button btnEdit     = (Button)   dialog.findViewById(R.id.EditButton);
        		Button btnDel      = (Button)   dialog.findViewById(R.id.DeleteButton);
        		EditText edit      = (EditText) dialog.findViewById(R.id.FirstName);
        		
        		edit.setEnabled(false);
        		
        	//  Set the listener for the objects we want to handle the click event	
        		btnSave.setOnClickListener(this);
        		btnDel.setOnClickListener(this);
        		btnEdit.setOnClickListener(this);
		    	image.setOnClickListener(this);

		     	String imageInSD = Environment.getExternalStorageDirectory().getPath() + "/Pictures/MyCameraApp/" +
		     						iter.getName()+".jpg";
		     	Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
		     	if (bitmap == null){
		     		bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.nophoto2);
		     	}
		     			     	
		         Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
      	         image.setImageBitmap(resizedBitmap);
		    	FirstName.setText(iter.getName());
		    	btnSave.setVisibility(View.GONE);
		    	dialog.setTitle("Details");
		    	
		    	dialog.show();
		    	
		    	
		    	Window window = dialog.getWindow();
		    	
//		   	window.setLayout(600,500);
//		    	final NotificationManager mgr=
//		                (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//		       //     Notification note=new Notification(R.drawable.red_little,
//		         //                                                   "Android Example Status message!",
//		           //                                                 System.currentTimeMillis());
//		            Notification n = new Notification();
//		            n.icon = R.drawable.red_little;
//		            n.tickerText = "Friend selected";
//		            PendingIntent p= null;
//		            n.setLatestEventInfo(getActivity(), "Ttile",
//		            		"notification message",p);
//
//		            mgr.notify(12, n);
//		            
					};
			
    		}
    		if (!isMatch){
    			
    			surface.invalidate();
    			AlertDialog alert = builder.create();
				alert.show();
    		}
    		return true;
		}
    	else{
    		return false;
    	}
    	
    }
	/** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type,Friend currFriend){
          return Uri.fromFile(getOutputMediaFile(type, currFriend));
          
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && surface != null) {
//         
//        surface.invalidate();
//        }
        
        
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, Friend currFriend){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
        
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                                   + currFriend.getName() + ".jpg");
        return mediaFile;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
    	case R.id.image:
			v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));;
			
			// create Intent to take a picture and return control to the calling application
	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	        Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE,currFriend ); // create a file to save the image
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
	        
	        // start the image capture Intent
	        getActivity().startActivityForResult(intent, 100);
	        break;
	        
    	case R.id.EditButton:
    		Button btnSave = (Button) dialog.findViewById(R.id.saveButton);
    		btnSave.setVisibility(View.VISIBLE);
    		EditText edit  = (EditText) dialog.findViewById(R.id.FirstName);
    		edit.setEnabled(true);
    		break;
    		
    	case R.id.DeleteButton:
    		
    		EditText firstName = (EditText) dialog.findViewById(R.id.FirstName);    		
    		GlobalParams.getInstance().DeleteFriend(firstName.getText().toString());
    		
    	//  Close the dialog	
    		dialog.hide();
    		
    		new DeleteObject(currFriend).execute();
    		
       //   ReDraw the map, to refresh the deleted object
    		surface.invalidate();
    		
    		break;
    		
    	case R.id.saveButton:
    		dialog.hide();
    		break;
    		
	} 
		
	}

	@Override
	public boolean onLongClick(View v) {
		Toast.makeText(getActivity(), v.getId(), Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

}
