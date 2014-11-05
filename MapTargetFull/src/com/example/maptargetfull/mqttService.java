package com.example.maptargetfull;

//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//
//import com.google.android.gms.common.annotation.KeepName;
//
//import android.app.IntentService;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.widget.Toast;
//
//
//public class mqttService extends Service {
//    public static final String BROKER_URL = "tcp://192.168.43.231:1883";
//    //public static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
//
//    /* In a real application, you should get an Unique Client ID of the device and use this, see
//    http://android-developers.blogspot.de/2011/03/identifying-app-installations.html */
//    public static final String clientId = "android-client";
//
//    public static final String TOPIC = "MQTT Examples";
//    private MqttClient mqttClient;
//
//
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//    
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//    	// TODO Auto-generated method stub
//    	return super.onStartCommand(intent, flags, startId);
//    }
//    
//    @Override
//    public void onStart(Intent intent, int startId) {
//    	android.os.Debug.waitForDebugger();
//        try {
//            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
//
//            mqttClient.setCallback(new mqtthandler(this));
//            MqttConnectOptions o = new MqttConnectOptions();
//            o.setCleanSession(true);
//            mqttClient.connect(o);
//            
//            //Subscribe to all subtopics of homeautomation
//            mqttClient.subscribe(TOPIC);
//
//
//        } catch (MqttException e) {
//            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
//        
//        super.onStart(intent, startId);
//    }
//    
//    @Override
//    public void onDestroy() {
//        try {
//            mqttClient.disconnect(0);
//        } catch (MqttException e) {
//            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
//    }
//}
