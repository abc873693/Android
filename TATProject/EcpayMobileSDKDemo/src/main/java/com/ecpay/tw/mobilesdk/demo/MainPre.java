package com.ecpay.tw.mobilesdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ecpay.tw.mobilesdk.ENVIRONMENT;

public class MainPre extends Activity {

	Button btnForeGround, btnBackGround;

	protected Spinner spnAppEnvironment;
	protected EditText edtAppCode, edtMerchantID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_pre);
		
		btnForeGround = (Button)findViewById(R.id.btnForeGround);
		btnBackGround = (Button)findViewById(R.id.btnBackGround);
		
		btnForeGround.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it2main = new Intent(MainPre.this, Main.class);
				startActivity(it2main);
			}
		});
		
		btnBackGround.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it2mainBackground = new Intent(MainPre.this, MainBackground.class);
				startActivity(it2mainBackground);
			}
		});

		//App界接環境
		spnAppEnvironment = (Spinner)findViewById(R.id.spnAppEnvironment);
		ArrayAdapter<String> adpAppEnvironment = new ArrayAdapter<String>(MainPre.this, android.R.layout.simple_spinner_item, Config.aryAppEnvironment);
		adpAppEnvironment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnAppEnvironment.setAdapter(adpAppEnvironment);
		spnAppEnvironment.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			//
			@Override
			public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
				String appEnvironment = adapterView.getSelectedItem().toString();
				if (appEnvironment.equalsIgnoreCase("OFFICIAL"))
					Config.AppEnvironment = ENVIRONMENT.OFFICIAL;
				else if (appEnvironment.equalsIgnoreCase("BETA"))
					Config.AppEnvironment = ENVIRONMENT.BETA;
				else
					Config.AppEnvironment = ENVIRONMENT.STAGE;
			}
			//
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		//App代碼
		edtAppCode = (EditText)findViewById(R.id.edtAppCode);
		edtAppCode.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable text) {
				Config.AppCode_test = text.toString();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		//特店編號, 廠商編號
		edtMerchantID = (EditText)findViewById(R.id.edtMerchantID);
		edtMerchantID.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable text) {
				Config.MerchantID_test = text.toString();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
	}
	
}
