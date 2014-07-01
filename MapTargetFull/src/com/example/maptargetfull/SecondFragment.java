package com.example.maptargetfull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.maptargetfull.PointsDBAccess.Point;


public class SecondFragment extends Fragment implements OnTouchListener, OnItemLongClickListener {
	
    private ProgressDialog pdialog;
    private final int ACTION_GET = 1;
	private final int ACTION_ADD_SOLDIER = 2;
	private boolean isTimeOut = false;
    ListView list;
    ArrayList<String> categories = new ArrayList<String>();
    FriendListAdapter adapter;
	
    public interface onclickListener	{
    	public void onRefreshSelected();
    	    }
    
    public void update(){
    	new callservice(ACTION_GET).execute();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.secondfragment, container, false);
        GlobalParams.getInstance().listFriends = this;
        rootView.setOnTouchListener(this);
        list = (ListView) rootView.findViewById(R.id.mylistview);
	//	adapter = new FriendListAdapter(getActivity());
	//	list.setAdapter(adapter);
		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		list.setOnItemLongClickListener(this);
		list.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				// TODO Auto-generated method stub	
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.contextmenu, arg1);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem arg1) {
				
				Iterator<Integer> iterator = adapter.getSelection().keySet().iterator();
				Collection<Friend> friendsToRemove = new ArrayList<Friend>();
				
				while(iterator.hasNext())
				{
					int key = iterator.next();
					Friend currFriend = GlobalParams.getInstance().getSpecificFriend(key);
					friendsToRemove.add(currFriend);
					new DeleteObject(currFriend).execute();
				}
				GlobalParams.getInstance().deleteFriends(friendsToRemove);
				adapter.clearSelection();
				adapter = new FriendListAdapter(getActivity());
				list.setAdapter(adapter);	
				
				mode.finish();
				
				return false;
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode arg0, int position, long arg2,
					boolean checked) {
				if(checked){
					adapter.setItemSelected(position);
				}
				else{
					adapter.removeSelection(position);
				}				
			}
		});
        list.setOnItemClickListener(new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Object o = list.getItemAtPosition(arg2);
		String s = (String) o;
		Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
	}
});
       

    //  Set loading dialog until the datais received from server
   	     pdialog =new ProgressDialog(getActivity());
   	     pdialog.setMessage("loading list from json rest service :)");
	     pdialog.show();  
	 
	//  call the server to get the data
        new callservice(ACTION_GET).execute();

        return rootView;
    }

    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) 
		{
    		pdialog = new ProgressDialog(getActivity());
    		pdialog.setTitle("Creating...");
    		pdialog.show();
    		new callservice(ACTION_ADD_SOLDIER).execute();
    	Toast.makeText(v.getContext(),"clicked!" ,Toast.LENGTH_SHORT).show();
		}
    	return true;
    }
    private class callservice extends AsyncTask<Void, Void, ArrayList<String>>{
    	
    	private int action;
    	private final String url = "http://172.20.19.192:3000/friends";
    	private final int ACTION_GET = 1;
    	private final int ACTION_ADD_SOLDIER = 2;
    	
    	public callservice(int action){
    		this.action = action;
    	}
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            int timeout = 5; // seconds
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000); // http.connection.timeout
            HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000); // http.socket.timeout
            
            // make GET request to the given URL
            HttpResponse httpResponse;
            switch (action) {
			case (ACTION_GET):
				try {
					GlobalParams.getInstance().clearList();
					
					ArrayList<Point> points =  GlobalParams.getInstance().PointsDBaccess.getPoints(false);
					for (Point point : points) {
						
						Double langitude = point.langitude;
						Double longitude = point.longitude;

						GlobalParams.getInstance().addFriend(new Friend(point.first_name, 0, point.last_name,
																langitude.intValue(), longitude.intValue()));				
					}
						
					
					httpResponse = httpclient.execute(new HttpGet(url));
					
		            // receive response as inputStream
		            InputStream inputStream = httpResponse.getEntity().getContent();
		            
		            String result = convertInputStreamToString(inputStream);
		            
		            JSONObject json = new JSONObject(result);
		            
		            JSONArray friends = json.getJSONArray("friends");
		            
		            // Run over all the friends, create an instance and add them to the array in global class
		            for(int i = 0; i < friends.length(); i++){
		            GlobalParams.getInstance().addFriend(new Friend(friends.getJSONObject(i).getString("firstname"),
		            												90,
		            												friends.getJSONObject(i).getString("_id"),
		            												friends.getJSONObject(i).getInt("locationX"),
		            												friends.getJSONObject(i).getInt("locationY")));
		            }
		            
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//pdialog.hide();
					//showError();
					isTimeOut = true;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case (ACTION_ADD_SOLDIER):
				
				GlobalParams.getInstance().PointsDBaccess.createPoint("martin", "gordon", 150, 480, false);
			//	url with the post data
			    HttpPost httpost = new HttpPost(url);
				
			    //convert parameters into JSON object
			    JSONObject newSoldier = new JSONObject();
			    
			    try {
					newSoldier.put("firstname", "300");
					newSoldier.put("locationX", 200);
					newSoldier.put("locationY", 300);
					//passes the results to a string builder/entity
				    StringEntity se = new StringEntity(newSoldier.toString());

				    //sets the post request as the resulting string
				    httpost.setEntity(se);
				    //sets a request header so the page receving the request
				    //will know what to do with it
				    httpost.setHeader("Accept", "application/json");
				    httpost.setHeader("Content-type", "application/json");

				    //Handles what is returned from the page 
				    ResponseHandler responseHandler = new BasicResponseHandler();
				    httpclient.execute(httpost, responseHandler);
				    
				    GlobalParams.getInstance().clearList();
				    new callservice(ACTION_GET).execute();
					break;
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
			 
	        return categories;
	      
		}
		@Override
		 protected void onPostExecute(ArrayList<String> result) {
			
		 super.onPostExecute(result);
		 adapter = new FriendListAdapter(getActivity());

		 list.setAdapter(adapter);		 
		 pdialog.hide();
		 if (isTimeOut){
		//	 showError();
		 }
		// Helper.getListViewSize(list);
		 }
    	
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
    
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		list.setItemChecked(arg2, true);
		
		if(GlobalParams.getInstance().selectedItem == arg2){
			GlobalParams.getInstance().selectedItem = -1;
		}
		else{
		GlobalParams.getInstance().selectedItem = arg2;
		}
		// start the CAB using the ActionMode.Callback defined above
       // mActionMode = MyListActivityActionbar.this
       //     .startActionMode(mActionModeCallback);
        arg1.setSelected(true);
		return true;
	}
	public void showError(){
		Toast.makeText(getActivity(), "connection failed", Toast.LENGTH_SHORT).show();

	}
}

