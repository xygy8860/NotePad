package com.example.notepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class secondActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉activity标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.second);
	}
	
	public void onClickButton(View v) {
		
		EditText editText = (EditText)findViewById(R.id.secondEditText);
		String str = editText.getText().toString();
		if("我爱大伟".equals(str)){
			Intent intent = new Intent();
		    intent.putExtra("id", "Love");  
		    intent.setClass(secondActivity.this, NoteEditActivity.class); 
		    secondActivity.this.startActivity(intent);
		    finish();
		}
		
	}

	
}
