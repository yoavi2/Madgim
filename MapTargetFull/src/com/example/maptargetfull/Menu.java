package com.example.maptargetfull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Menu extends Activity {
	
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maimmenu);
        
        new Handler().postDelayed(new Runnable() {
        	 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
            	Intent i = new Intent(Menu.this, MainActivity.class);
        		startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
	}

	public void onclick(View view){
		Intent i = new Intent(Menu.this, MainActivity.class);
		startActivity(i);
	}
	
}
