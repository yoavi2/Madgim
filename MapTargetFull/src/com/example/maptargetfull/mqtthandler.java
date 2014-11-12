package com.example.maptargetfull;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.example.maptargetfull.GlobalParams.markerType;
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
		
		if (topic.equals("insert"))
		{
			JSONObject json = null;
			try {
				JSONArray a = new JSONArray(message.toString());
				json = a.getJSONObject(0);
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
			long rowID = pointsDB.createPoint(
					ps.first_name,
					ps.last_name,
					ps.longitude,
					ps.langitude,
					ps.is_google == 1 ? true : false,
				    ps.pointType);
			pointsDB.SetServerID(rowID, ps.server_id);
			pointsDB.SetSynched(rowID);
			GlobalParams.getInstance().mCurrMap.addMarkerOnLocationOffline(ps.first_name,
					ps.pointType == 1 ? markerType.Tank
							: markerType.Truck, ps.langitude,
					ps.longitude);
			Toast.makeText(this.context, ps.first_name + " added!", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}
}
