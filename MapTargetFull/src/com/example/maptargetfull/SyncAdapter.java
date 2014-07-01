package com.example.maptargetfull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import com.example.maptargetfull.PointsDBAccess.PointForSync;
import com.example.maptargetfull.SQLiteDB.Points;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private final String url = "http://172.20.19.192:3000/friends";
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		// TODO Auto-generated constructor stub
	}
	
	public SyncAdapter(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		PointsDBAccess pointsDB = new PointsDBAccess(getContext());
		ArrayList<PointForSync> arrayPoint = pointsDB.getPointsForSync();
		
		// create HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        int timeout = 5; // seconds
        HttpParams httpParams = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000); // http.connection.timeout
        HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000); // http.socket.timeout
        
        //Sync device to server
        for (int i = 0; i < arrayPoint.size(); i++) {
        	JSONObject json = new JSONObject();
        	try {
				json.put(Points.Columns.first_name, arrayPoint.get(i).first_name);
				json.put(Points.Columns.last_name, arrayPoint.get(i).last_name);
				json.put(Points.Columns.longitude, arrayPoint.get(i).longitude);
				json.put(Points.Columns.langitude, arrayPoint.get(i).langitude);
				json.put(Points.Columns.is_deleted, arrayPoint.get(i).is_deleted);
				json.put(Points.Columns.is_google, arrayPoint.get(i).is_google);
				
				if (arrayPoint.get(i).server_id != ""){
					json.put("_id", arrayPoint.get(i).server_id);
				}
				
				HttpPut httpPut = new HttpPut(url);
				
				httpPut.setHeader("Accept", "application/json");
				httpPut.setHeader("Content-type", "application/json");
				
				httpPut.setEntity(new StringEntity(json.toString()));
				
			    ResponseHandler responseHandler = new BasicResponseHandler();
			    httpclient.execute(httpPut, responseHandler);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
        //Sync server to device
        
        pointsDB.deletePointsForSync();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse;
        
        try {
			httpResponse = httpclient.execute(httpGet);
			
            // receive response as inputStream
            InputStream inputStream = httpResponse.getEntity().getContent();
            
            String result = convertInputStreamToString(inputStream);
            
            JSONObject json = new JSONObject(result);
            
            JSONArray friends = json.getJSONArray("friends");
            
            // Run over all the friends, create an instance and add them to the array in global class
            for(int i = 0; i < friends.length(); i++){
            	long rowID = pointsDB.createPoint(friends.getJSONObject(i).getString(Points.Columns.first_name), 
			            			              friends.getJSONObject(i).getString(Points.Columns.last_name), 
			            			              friends.getJSONObject(i).getDouble(Points.Columns.longitude), 
			            			              friends.getJSONObject(i).getDouble(Points.Columns.langitude), 
			            			              friends.getJSONObject(i).getInt(Points.Columns.is_google) == 1?true:false);
            	pointsDB.SetServerID(rowID, friends.getJSONObject(i).getString("_id"));
            }

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        getContext().sendBroadcast(new Intent(GlobalParams.getInstance().syncFinished));
        
	}
	
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }

}
