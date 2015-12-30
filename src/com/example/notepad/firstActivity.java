package com.example.notepad;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class firstActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//»•µÙactivity±ÍÃ‚
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.first);
		}
	public void onClickButton(View v) {
		
		Intent intent = new Intent();
	    intent.setClass(this, secondActivity.class); 
	    firstActivity.this.startActivity(intent);
		finish();
	}

}
