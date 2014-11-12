package com.example.maptargetfull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maimmenu);
       
	}
	public void onclick(View view){
		Intent i = new Intent(Menu.this, MainActivity.class);
		startActivity(i);
	}
	
}
