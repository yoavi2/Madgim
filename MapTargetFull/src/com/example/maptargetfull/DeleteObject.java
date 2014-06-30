package com.example.maptargetfull;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class DeleteObject  extends AsyncTask<Void, Void, ArrayList<String>>{
	
	private Friend friend;
	
	public DeleteObject(Friend f){
		this.friend = f;
	}
	
	@Override
	protected ArrayList<String> doInBackground(Void... params) {

		// create HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        
        // make GET request to the given URL
        HttpResponse httpResponse;
        
        HttpDelete httpdelete = new HttpDelete("http://172.20.19.192:3000/friends/" + friend.getnick());

		try {
			httpResponse = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
