package com.example.maptargetfull;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.example.maptargetfull.PointsDBAccess.Point;

public class DeleteObject  extends AsyncTask<Void, Void, ArrayList<String>>{
	
	private Point friend;
	
	public DeleteObject(Point f){
		this.friend = f;
	}
	
	@Override
	protected ArrayList<String> doInBackground(Void... params) {

		GlobalParams.getInstance().PointsDBaccess.deletePoint(friend.rowID);
		
//		// create HttpClient
//        HttpClient httpclient = new DefaultHttpClient();
//        
//        // make GET request to the given URL
//        HttpResponse httpResponse;
//        
//        HttpDelete httpdelete = new HttpDelete("http://172.20.19.192:3000/friends/" + friend.getnick());
//
//		try {
//			httpResponse = httpclient.execute(httpdelete);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
	}

}
