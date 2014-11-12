package com.example.maptargetfull;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;

public class StartupReceiver extends BroadcastReceiver implements IMqttActionListener {
	
	private MqttAndroidClient c;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		c = new MqttAndroidClient(context, "tcp://192.168.43.231:1883", Secure.ANDROID_ID);
		
		try {
			c.setCallback(new mqtthandler(context, c));
			c.connect(this, this);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onFailure(IMqttToken arg0, Throwable arg1) {
		while (!c.isConnected())
		{
			try {
				c.connect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onSuccess(IMqttToken arg0) {
		try {
			c.subscribe("test", 0);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
