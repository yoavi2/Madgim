package com.example.maptargetfull;
import java.util.UUID;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


public class mqtt_notification extends Service implements MqttCallback, IMqttActionListener {

	private MqttAndroidClient c;

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		android.os.Debug.waitForDebugger();
		
		Intent resultIntent = new Intent(this, Menu.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack
		stackBuilder.addParentStack(Menu.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		String x = "";
		
		if (arg0.equals("insert")) {
			x = "new dot!";
		} 
		
		if (arg0.equals("update")) {
			x = "update dot!";
		}
		
		if (!x.isEmpty()) {
			NotificationManager n =(NotificationManager) this.getBaseContext().getSystemService(NOTIFICATION_SERVICE);
			NotificationCompat.Builder b =  new NotificationCompat.Builder(getBaseContext())
			.setContentText(x)
			.setContentTitle("Sheleg")
			.setSmallIcon(R.drawable.ic_delete)
			.setContentIntent(resultPendingIntent);
			n.notify(1, b.build());
		}
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			public void run() {
				android.os.Debug.waitForDebugger();
				SharedPreferences shar = getBaseContext().getSharedPreferences("set", Context.MODE_PRIVATE);
				
				String ipb = shar.getString("IPB", "0.0.0.0");
				
				c = new MqttAndroidClient(getBaseContext(), "tcp://" + ipb + ":1883", UUID
						.randomUUID().toString());
				
				try {
					c.setCallback(mqtt_notification.this);
					
					MqttConnectOptions opt = new MqttConnectOptions();
					int timeout = Integer.parseInt(shar.getString("TIMEOUT", "30"));
					opt.setConnectionTimeout(timeout);
					
					c.connect(opt, mqtt_notification.this, mqtt_notification.this);
				} catch (MqttSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onFailure(IMqttToken arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess(IMqttToken arg0) {
		try {
			c.subscribe("update", 0);
			c.subscribe("insert", 0);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
