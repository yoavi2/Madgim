package com.example.maptargetfull;

import java.util.ArrayList;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import us.ba3.me.Location;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.widget.Toast;

import com.example.maptargetfull.GlobalParams.markerType;
import com.example.maptargetfull.PointsDBAccess.Point;
import com.example.maptargetfull.PointsDBAccess.PointForSync;
import com.example.maptargetfull.SQLiteDB.Points;



public class mqtthandler implements MqttCallback
{
	Context context;
	MqttAndroidClient client;
	
	mqtthandler(Context context, MqttAndroidClient client) {
		// TODO Auto-generated method stub
		this.context = context;
		this.client = client;
	}
	
	@Override
	public void connectionLost(Throwable cause) {
//		while (!client.isConnected())
//		{
//			try {
//				client.connect();
//			} catch (MqttException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		Toast.makeText(this.context, "FAIL", Toast.LENGTH_LONG).show();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		// TODO Auto-generated method stub
	//	Toast.makeText(this.context, "Message Arrived!", Toast.LENGTH_SHORT).show();
//		NotificationManager m = (NotificationManager) this.context.getSystemService(context.NOTIFICATION_SERVICE);
//		NotificationCompat.Builder b = new NotificationCompat.Builder(context)
//		.setSmallIcon(R.drawable.google_map_icon)
//		.setContentTitle("Mqtt")
//		.setContentText("Message has arrived!");
//		m.notify(123, b.build());
		JSONObject json = null;
		PointsDBAccess pdb = new PointsDBAccess( this.context );
		Point p = pdb.new Point();
		if (topic.equals("insert"))
		{	
			try {
				json = new JSONObject(message.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			PointsDBAccess pointsDB = new PointsDBAccess(this.context);
			PointForSync ps = pointsDB.new PointForSync();
			ps.first_name = json.getString(
					Points.Columns.first_name);
			ps.last_name = json.getString(
					Points.Columns.last_name);
			ps.longitude = json.getDouble(
					Points.Columns.longitude);
			ps.langitude = json.getDouble(
					Points.Columns.langitude);
			ps.is_google = json.getInt(
					Points.Columns.is_google);
			ps.server_id = json.getString(
					"_id");
			ps.pointType = json.getInt(
					Points.Columns.point_type);
			
//			if (GlobalParams.getInstance().doesLocationExist(ps.longitude, ps.langitude))
//			{
//				return;
//			}
			
			long rowID = pointsDB.createPoint(
					ps.first_name,
		 			ps.last_name,
					ps.longitude,
					ps.langitude,
					ps.is_google == 1 ? true : false,
				    ps.pointType);
			pointsDB.SetServerID(rowID, ps.server_id);
			pointsDB.SetSynched(rowID);
			
			p.first_name = ps.first_name;
			p.last_name	= ps.last_name;
			p.longitude = ps.longitude;
			p.langitude = ps.langitude;
			p.pointType	= ps.pointType;
			p.rowID = rowID;
			
			GlobalParams.getInstance().addPoint(p);
			
			GlobalParams.getInstance().mCurrMap.addMarkerOnLocationOffline(ps.first_name,
					ps.pointType == 1 ? markerType.Tank
							: markerType.Truck, ps.langitude,
					ps.longitude);
			
			// Add the marker to the markers list
			GlobalParams.getInstance().AddMarker(ps.first_name, new Location(ps.langitude, ps.longitude));
			
			// Reload current fragment
			Fragment frg = null;
			frg = GlobalParams.getFragment().findFragmentByTag("list_frame");
			final FragmentTransaction ft = GlobalParams.getFragment().beginTransaction();
			ft.detach(frg);
			ft.attach(frg);
			ft.commit();
			
			Toast.makeText(this.context, ps.first_name + " נוסף", Toast.LENGTH_SHORT).show();
		}
		else if (topic.equals("update")) 
		{
			try {
				json = new JSONObject(message.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			PointsDBAccess pointsDB = new PointsDBAccess(this.context);
			PointForSync ps = pointsDB.new PointForSync();
			ps.first_name = json.getString(
					Points.Columns.first_name);
			ps.last_name = json.getString(
					Points.Columns.last_name);
			ps.longitude = json.getDouble(
					Points.Columns.longitude);
			ps.langitude = json.getDouble(
					Points.Columns.langitude);
			ps.is_google = json.getInt(
					Points.Columns.is_google);
			ps.server_id = json.getString(
					"_id");
			ps.pointType = json.getInt(
					Points.Columns.point_type);
			ps.is_deleted = json.getInt(Points.Columns.is_deleted);
			
//			if (GlobalParams.getInstance().doesLocationExist(ps.longitude, ps.langitude))
//			{
//			 	return;
//			}
			
			long rowid = pointsDB.getRowIdbyServerId(ps.server_id);
			pointsDB.updatePoint(rowid, ps.first_name, ps.last_name, ps.longitude, ps.langitude, ps.is_google==1?true:false, ps.pointType);
			pointsDB.SetServerID(rowid, ps.server_id);
			pointsDB.SetSynched(rowid);
			
			if (ps.is_deleted == 1)
			{
				GlobalParams.getInstance().deletePointByRowid(rowid);
				GlobalParams.getInstance().mCurrMap.removeMarkerOnLocationOffline(ps.first_name);
				
				// Remove the marker from the markers list
				GlobalParams.getInstance().RemoveMarker(ps.first_name);
				GlobalParams.getCurrMap().deletePoint(ps.first_name);
				
				// Reload current fragment
				Fragment frg = null;
				frg = GlobalParams.getFragment().findFragmentByTag("list_frame");
				final FragmentTransaction ft = GlobalParams.getFragment().beginTransaction();
				ft.detach(frg);
				ft.attach(frg);
				ft.commit();
				
				Toast.makeText(this.context, ps.first_name + " נמחק", Toast.LENGTH_SHORT).show();
			}
			else
			{
				p.first_name = ps.first_name;
				p.last_name	= ps.last_name;
				p.longitude = ps.longitude;
				p.langitude = ps.langitude;
				p.pointType	= ps.pointType;
				p.rowID = rowid;
				GlobalParams.getInstance().updatePoint(p);
				
				// Update the markers list
				GlobalParams.getInstance().UpdateMarker(ps.first_name, new Location(ps.langitude, ps.longitude));
				
				// Reload current fragment
				Fragment frg = null;
				frg = GlobalParams.getFragment().findFragmentByTag("list_frame");
				final FragmentTransaction ft = GlobalParams.getFragment().beginTransaction();
				ft.detach(frg);
				ft.attach(frg);
				ft.commit();
				
				Toast.makeText(this.context, ps.first_name + " updated!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}
}
