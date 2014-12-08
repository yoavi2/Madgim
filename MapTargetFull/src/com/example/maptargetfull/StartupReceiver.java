package com.example.maptargetfull;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;

public class StartupReceiver extends BroadcastReceiver {
	
	private MqttAndroidClient c;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		android.os.Debug.waitForDebugger();
		Intent serviceStartIntent = new Intent();
		serviceStartIntent.setClassName(context, "com.example.maptargetfull.mqtt_notification");
		context.startService(serviceStartIntent);
	}

}
