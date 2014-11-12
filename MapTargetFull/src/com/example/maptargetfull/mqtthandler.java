package com.example.maptargetfull;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.content.Context;
import android.widget.Toast;



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
		while (!client.isConnected())
		{
			try {
				client.connect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			Toast.makeText(this.context, message.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}
}
