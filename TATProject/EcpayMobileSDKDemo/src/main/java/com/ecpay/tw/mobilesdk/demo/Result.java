package com.ecpay.tw.mobilesdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Result extends Activity {
	public static String EXTRA_RESULT = "ShowResult";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
		String str = getIntent().getExtras().getString(EXTRA_RESULT);
		
		TextView txt = (TextView)findViewById(R.id.txtResult);
		txt.setText(str);
		
	}
	
}
