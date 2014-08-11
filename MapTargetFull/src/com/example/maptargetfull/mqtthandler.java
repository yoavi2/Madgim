package com.example.maptargetfull;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class mqtthandler implements MqttCallback
{
	Context context;
	
	mqtthandler(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		// TODO Auto-generated method stub
	//	Toast.makeText(this.context, "Message Arrived!", Toast.LENGTH_SHORT).show();
		NotificationManager m = (NotificationManager) this.context.getSystemService(context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder b = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.arrow_up_float)
		.setContentTitle("Mqtt")
		.setContentText("Message has arrived!");
		m.notify(123, b.build());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
}
