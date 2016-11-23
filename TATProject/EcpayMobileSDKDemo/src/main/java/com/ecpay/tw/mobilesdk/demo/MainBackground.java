package com.ecpay.tw.mobilesdk.demo;

import com.ecpay.tw.mobilesdk.API_ATM;
import com.ecpay.tw.mobilesdk.API_Base;
import com.ecpay.tw.mobilesdk.API_CVS;
import com.ecpay.tw.mobilesdk.API_Credit;
import com.ecpay.tw.mobilesdk.EcpayAsyncTask;
import com.ecpay.tw.mobilesdk.EcpayBackgroundTaskCompleted;
import com.ecpay.tw.mobilesdk.BANKNAME;
import com.ecpay.tw.mobilesdk.BackgroundATM;
import com.ecpay.tw.mobilesdk.BackgroundCVS;
import com.ecpay.tw.mobilesdk.BackgroundCredit;
import com.ecpay.tw.mobilesdk.BackgroundOTP;
import com.ecpay.tw.mobilesdk.STORETYPE;
import com.ecpay.tw.mobilesdk.PAYMENTTYPE;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainBackground extends Activity implements EcpayBackgroundTaskCompleted {
	
	Button btnATM, btnCVS, btnCredit, btnOTP, btnCreditInstall;
	Spinner spnATM, spnCVS;
	View layoutMenu, layoutOTP;
	EditText edtOTP, edtCreditPhone, edtCreditInstallmentPhone;
	TextView txtOtpExpiredTime;
	ProgressDialog pd;
	
	String OtpMerchantID, OtpMerchantTradeNo, OtpTradeNo;
	
	boolean isCounterPayIN = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_background);
		setTitle("訂單產生(幕後)");
		
		btnATM = (Button)findViewById(R.id.btnBackgroundATM);
		btnCVS = (Button)findViewById(R.id.btnBackgroundCVS);
		btnCredit = (Button)findViewById(R.id.btnBackgroundCredit);
		spnATM = (Spinner)findViewById(R.id.spinner_ATM_background);
		spnCVS = (Spinner)findViewById(R.id.spinner_CVS_background);
		layoutMenu = (View) findViewById(R.id.layout_menu);
		layoutOTP = (View)findViewById(R.id.layout_otp);
		btnOTP = (Button)findViewById(R.id.btnOTP);
		edtOTP = (EditText)findViewById(R.id.edtOTP);
		txtOtpExpiredTime = (TextView)findViewById(R.id.txtOtpExpiredTime);
		btnCreditInstall = (Button)findViewById(R.id.btnBackgroundCreditInstallment);
		edtCreditPhone = (EditText)findViewById(R.id.edt_credit_phone);
		edtCreditInstallmentPhone = (EditText)findViewById(R.id.edt_creditInstallment_phone);
		
		btnATM.setOnClickListener(clk_Listener);
		btnCVS.setOnClickListener(clk_Listener);
		btnCredit.setOnClickListener(clk_Listener);
		btnOTP.setOnClickListener(clk_Listener);
		btnCreditInstall.setOnClickListener(clk_Listener);
		
		
		ArrayAdapter<BANKNAME> adpATM = new ArrayAdapter<BANKNAME>(MainBackground.this, android.R.layout.simple_spinner_item, Config.lstBankName);
		adpATM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnATM.setAdapter(adpATM);
		
		ArrayAdapter<STORETYPE> adpCVS = new ArrayAdapter<STORETYPE>(MainBackground.this, android.R.layout.simple_spinner_item, Config.lstStoreType);
		adpCVS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnCVS.setAdapter(adpCVS);
		
		
		pd = new ProgressDialog(MainBackground.this);
		pd.setTitle("提示");
		pd.setMessage("交易處理中");
	}
	
	
	Button.OnClickListener clk_Listener = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int iBtnID = v.getId();
			
			if(iBtnID == R.id.btnBackgroundATM){
				BANKNAME bankName =  BANKNAME.parse2BankName(spnATM.getSelectedItem().toString());
//				if(bankName == BANKNAME.ESUN_Counter)
//					isCounterPayIN = true;
//				else
//					isCounterPayIN = false;
				
				//************** 沒PlatformID ******************
				BackgroundATM oBackgroundATM = new BackgroundATM(Config.MerchantID_test, 			    //廠商編號
																 Config.AppCode_test, 				//App代碼
																 Config.getMerchantTradeNo(), 		//廠商交易編號
																 Config.getMerchantTradeDate(), 	    //廠商交易時間
																 Config.TotalAmount_test, 			    //交易金額
																 Config.TradeDesc_test, 			    //交易描述
																 Config.ItemName_test, 				//商品名稱
																 PAYMENTTYPE.ATM, 					//付款方式
						                                         Config.AppEnvironment, 				//介接環境 : STAGE為測試，OFFICIAL為正式
																 bankName, 							//銀行代碼
																 3);								//選擇性參數，允許繳費有效天數(1~60)
				
				//************** 有PlatformID ******************
//				BackgroundATM oBackgroundATM = new BackgroundATM(Config.MerchantID_test, 			//廠商編號
//																 Config.PlatformID_test,			//特約合作平台商代號
//																 Config.AppCode_PlatformID_test, 	//App代碼
//																 Config.getMerchantTradeNo(), 		//廠商交易編號
//																 Config.getMerchantTradeDate(), 	//廠商交易時間
//																 Config.TotalAmount_test, 			//交易金額
//																 Config.TradeDesc_test, 			//交易描述
//																 Config.ItemName_test, 				//商品名稱
//																 PAYMENTTYPE.ATM, 					//付款方式
//																 Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																 BANKNAME.parse2BankName(spnATM.getSelectedItem().toString()), 				//銀行代碼
//																 3);
				
				//************** 有PlatformID、手續費 ******************
//				BackgroundATM oBackgroundATM = new BackgroundATM(Config.MerchantID_test, 			//廠商編號
//																 Config.PlatformID_test,			//特約合作平台商代號
//																 Config.PlatformChargeFee_test,		//特約合作平台商手續費
//																 Config.AppCode_PlatformID_test, 	//App代碼
//																 Config.getMerchantTradeNo(), 		//廠商交易編號
//																 Config.getMerchantTradeDate(), 	//廠商交易時間
//																 Config.TotalAmount_test, 			//交易金額
//																 Config.TradeDesc_test, 			//交易描述
//																 Config.ItemName_test, 				//商品名稱
//																 PAYMENTTYPE.ATM, 					//付款方式
//																 Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																 BANKNAME.parse2BankName(spnATM.getSelectedItem().toString()), 				//銀行代碼
//																 3);
				
				//************** 有PlatformID、手續費 、平台商會員編號******************
//				BackgroundATM oBackgroundATM = new BackgroundATM(Config.MerchantID_test, 			//廠商編號
//																 Config.PlatformID_test,			//特約合作平台商代號
//																 Config.PlatformMemberNo_test,		//特約合作平台商會員編號
//																 Config.PlatformChargeFee_test,		//特約合作平台商手續費，若無手續費則帶0
//																 Config.AppCode_PlatformID_test, 	//App代碼
//																 Config.getMerchantTradeNo(), 		//廠商交易編號
//																 Config.getMerchantTradeDate(), 	//廠商交易時間
//																 Config.TotalAmount_test, 			//交易金額
//																 Config.TradeDesc_test, 			//交易描述
//																 Config.ItemName_test, 				//商品名稱
//																 PAYMENTTYPE.ATM, 					//付款方式
//																 Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																 BANKNAME.parse2BankName(spnATM.getSelectedItem().toString()), 				//銀行代碼
//																 3);
				
								
				//使用ProgressDialog
				new EcpayAsyncTask<BackgroundATM, API_ATM>(MainBackground.this, API_ATM.class, pd).execute(oBackgroundATM);
				//不使用ProgressDialog
//				new EcpayAsyncTask<BackgroundATM, API_ATM>(MainBackground.this, API_ATM.class).execute(oBackgroundATM);
				
			}else if(iBtnID == R.id.btnBackgroundCVS){
				//************** 沒PlatformID ******************
				BackgroundCVS oBackgroundCVS = new BackgroundCVS(Config.MerchantID_test, 			    //廠商編號
																 Config.AppCode_test, 				//App代碼
																 Config.getMerchantTradeNo(), 		//廠商交易編號
																 Config.getMerchantTradeDate(), 	    //廠商交易時間
																 Config.TotalAmount_test, 			    //交易金額
																 Config.TradeDesc_test, 			    //交易描述
																 Config.ItemName_test, 				//商品名稱
																 PAYMENTTYPE.CVS, 					//付款方式
																 Config.AppEnvironment, 				//介接環境 : STAGE為測試，OFFICIAL為正式
																 STORETYPE.parse2StoreType(spnCVS.getSelectedItem().toString()),					//超商代碼
																 "測試1", 							//交易描述1
																 "測試2");
				
				//************** 有PlatformID ******************
//				BackgroundCVS oBackgroundCVS = new BackgroundCVS(Config.MerchantID_test, 			//廠商編號
//																 Config.PlatformID_test,			//特約合作平台商代號
//																 Config.AppCode_PlatformID_test, 	//App代碼
//																 Config.getMerchantTradeNo(), 		//廠商交易編號
//																 Config.getMerchantTradeDate(), 	//廠商交易時間
//																 Config.TotalAmount_test, 			//交易金額
//																 Config.TradeDesc_test, 			//交易描述
//																 Config.ItemName_test, 				//商品名稱
//																 PAYMENTTYPE.CVS, 					//付款方式
//																 Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																 STORETYPE.parse2StoreType(spnCVS.getSelectedItem().toString()),					//超商代碼
//																 "測試1", 							//交易描述1
//																 "測試2");
				
//				//************** 有PlatformID、手續費 ******************
//				BackgroundCVS oBackgroundCVS = new BackgroundCVS(Config.MerchantID_test, 			//廠商編號
//																 Config.PlatformID_test,			//特約合作平台商代號
//																 Config.PlatformChargeFee_test,		//特約合作平台商手續費
//																 Config.AppCode_PlatformID_test, 	//App代碼
//																 Config.getMerchantTradeNo(), 		//廠商交易編號
//																 Config.getMerchantTradeDate(), 	//廠商交易時間
//																 Config.TotalAmount_test, 			//交易金額
//																 Config.TradeDesc_test, 			//交易描述
//																 Config.ItemName_test, 				//商品名稱
//																 PAYMENTTYPE.CVS, 					//付款方式
//																 Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																 STORETYPE.parse2StoreType(spnCVS.getSelectedItem().toString()),					//超商代碼
//																 "測試1", 							//交易描述1
//																 "測試2");							//交易描述2
				
				//************** 有PlatformID、手續費、平台商會員編號 ******************
//				BackgroundCVS oBackgroundCVS = new BackgroundCVS(Config.MerchantID_test, 			//廠商編號
//																 Config.PlatformID_test,			//特約合作平台商代號
//																 Config.PlatformMemberNo_test,		//特約合作平台商會員編號
//																 Config.PlatformChargeFee_test,		//特約合作平台商手續費，若無則帶0
//																 Config.AppCode_PlatformID_test, 	//App代碼
//																 Config.getMerchantTradeNo(), 		//廠商交易編號
//																 Config.getMerchantTradeDate(), 	//廠商交易時間
//																 Config.TotalAmount_test, 			//交易金額
//																 Config.TradeDesc_test, 			//交易描述
//																 Config.ItemName_test, 				//商品名稱
//																 PAYMENTTYPE.CVS, 					//付款方式
//																 Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																 STORETYPE.parse2StoreType(spnCVS.getSelectedItem().toString()),//超商代碼
//																 "測試1", 							//交易描述1
//																 "測試2",							//交易描述2
//																 "",								//交易描述3
//																 "");								//交易描述4
				
				//使用ProgressDialog
				new EcpayAsyncTask<BackgroundCVS, API_CVS>(MainBackground.this, API_CVS.class, pd).execute(oBackgroundCVS);
				//不使用ProgressDialog
//				new EcpayAsyncTask<BackgroundCVS, API_CVS>(MainBackground.this, API_CVS.class).execute(oBackgroundCVS);
				
			}else if(iBtnID == R.id.btnBackgroundCredit){
				String PhoneNumber = edtCreditPhone.getText().toString();
				if(!PhoneNumber.matches("[0][9][0-9]{2}[0-9]{6}")){
					Toast.makeText(MainBackground.this, "請輸入正確的手機號碼", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//************** 沒PlatformID ******************
				BackgroundCredit oBackgroundCredit = new BackgroundCredit(Config.MerchantID_test, 				//廠商編號
																		  Config.AppCode_test, 				//App代碼 
																		  Config.getMerchantTradeNo(), 		//廠商交易編號
																		  Config.getMerchantTradeDate(), 		//廠商交易時間
																		  Config.TotalAmount_test, 			//交易金額
																		  Config.TradeDesc_test, 				//交易描述
																		  Config.ItemName_test, 				//商品名稱
																		  PAYMENTTYPE.CREDIT, 				//付款方式
						                                                  Config.AppEnvironment, 				//介接環境 : STAGE為測試，OFFICIAL為正式
																		  "亨利", 							//持卡人姓名
																		  PhoneNumber, 						//手機號碼
																		  "4311952222222222", 				//信用卡卡號
																		  "204012", 						//有效年月
																		  "222");							//卡片末三碼
				
				//************** 有PlatformID ******************
//				BackgroundCredit oBackgroundCredit = new BackgroundCredit(Config.MerchantID_test, 			//廠商編號 
//																		  Config.PlatformID_test,			//特約合作平台商代號
//																		  Config.AppCode_PlatformID_test, 	//App代碼 
//																		  Config.getMerchantTradeNo(), 		//廠商交易編號
//																		  Config.getMerchantTradeDate(), 	//廠商交易時間
//																		  Config.TotalAmount_test, 			//交易金額
//																		  Config.TradeDesc_test, 			//交易描述
//																		  Config.ItemName_test, 			//商品名稱
//																		  PAYMENTTYPE.CREDIT, 				//付款方式
//																		  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																		  "亨利", 							//持卡人姓名
//																		  PhoneNumber, 						//手機號碼
//																		  "4311952222222222", 				//信用卡卡號
//																		  "204012", 						//有效年月
//																		  "222");							//卡片末三碼
				
				//************** 有PlatformID、手續費 ******************
//				BackgroundCredit oBackgroundCredit = new BackgroundCredit(Config.MerchantID_test, 			//廠商編號 
//																		  Config.PlatformID_test,			//特約合作平台商代號
//																		  Config.PlatformChargeFee_test,	//特約合作平台商手續費
//																		  Config.AppCode_PlatformID_test, 	//App代碼 
//																		  Config.getMerchantTradeNo(), 		//廠商交易編號
//																		  Config.getMerchantTradeDate(), 	//廠商交易時間
//																		  Config.TotalAmount_test, 			//交易金額
//																		  Config.TradeDesc_test, 			//交易描述
//																		  Config.ItemName_test, 			//商品名稱
//																		  PAYMENTTYPE.CREDIT, 				//付款方式
//																		  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																		  "亨利", 							//持卡人姓名
//																		  PhoneNumber, 						//手機號碼
//																		  "4311952222222222", 				//信用卡卡號
//																		  "204012", 						//有效年月
//																		  "222");							//卡片末三碼
				
				//************** 有PlatformID、手續費、平台商會員編號 ******************
//				BackgroundCredit oBackgroundCredit = new BackgroundCredit(Config.MerchantID_test, 			//廠商編號 
//																		  Config.PlatformID_test,			//特約合作平台商代號
//																		  Config.PlatformMemberNo_test,		//特約合作平台商會員編號
//																		  Config.PlatformChargeFee_test,	//特約合作平台商手續費，若無則帶0
//																		  Config.AppCode_PlatformID_test, 	//App代碼 
//																		  Config.getMerchantTradeNo(), 		//廠商交易編號
//																		  Config.getMerchantTradeDate(), 	//廠商交易時間
//																		  Config.TotalAmount_test, 			//交易金額
//																		  Config.TradeDesc_test, 			//交易描述
//																		  Config.ItemName_test, 			//商品名稱
//																		  PAYMENTTYPE.CREDIT, 				//付款方式
//																		  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//																		  "亨利", 							//持卡人姓名
//																		  PhoneNumber, 						//手機號碼
//																		  "4311952222222222", 				//信用卡卡號
//																		  "204012", 						//有效年月
//																		  "222");							//卡片末三碼
				
				
				
				//使用ProgressDialog
				new EcpayAsyncTask<BackgroundCredit, API_Credit>(MainBackground.this, API_Credit.class, pd).execute(oBackgroundCredit);
				//不使用ProgressDialog
//				new EcpayAsyncTask<BackgroundCredit, API_Credit>(MainBackground.this, API_Credit.class).execute(oBackgroundCredit);
				
			}else if(iBtnID == R.id.btnBackgroundCreditInstallment){
				String PhoneNumber = edtCreditInstallmentPhone.getText().toString();
				if(!PhoneNumber.matches("[0][9][0-9]{2}[0-9]{6}")){
					Toast.makeText(MainBackground.this, "請輸入正確的手機號碼", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//************** 沒PlatformID ******************
				BackgroundCredit oBackgroundCredit = new BackgroundCredit(
						  Config.MerchantID_test, 				//廠商編號
						  Config.AppCode_test, 				//App代碼 
						  Config.getMerchantTradeNo(), 		//廠商交易編號
						  Config.getMerchantTradeDate(), 		//廠商交易時間
						  Config.TotalAmount_test, 			//交易金額
						  Config.TradeDesc_test, 				//交易描述
						  Config.ItemName_test, 				//商品名稱
						  PAYMENTTYPE.CREDIT, 				//付款方式
						  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
						  "亨利", 							//持卡人姓名
						  PhoneNumber, 						//手機號碼
						  "4311952222222222", 				//信用卡卡號
						  "204012", 						//有效年月
						  "222",							//卡片末三碼
						  3,								//分期期數
						  0,								//分期的付款金額
						  false);							//是否使用紅利折抵
				
				//************** 有PlatformID ******************
//				BackgroundCredit oBackgroundCredit = new BackgroundCredit(
//						  Config.MerchantID_test, 			//廠商編號
//						  Config.PlatformID_test,			//特約合作平台商代號
//						  Config.AppCode_PlatformID_test, 	//App代碼 
//						  Config.getMerchantTradeNo(), 		//廠商交易編號
//						  Config.getMerchantTradeDate(), 	//廠商交易時間
//						  Config.TotalAmount_test, 			//交易金額
//						  Config.TradeDesc_test, 			//交易描述
//						  Config.ItemName_test, 			//商品名稱
//						  PAYMENTTYPE.CREDIT, 				//付款方式
//						  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//						  "亨利", 							//持卡人姓名
//						  PhoneNumber, 						//手機號碼
//						  "4311952222222222", 				//信用卡卡號
//						  "204012", 						//有效年月
//						  "222",							//卡片末三碼
//						  3,								//分期期數
//						  0,								//分期的付款金額
//						  false);							//是否使用紅利折抵
				
				//************** 有PlatformID、手續費 ******************
//				BackgroundCredit oBackgroundCredit = new BackgroundCredit(
//						  Config.MerchantID_test, 			//廠商編號
//						  Config.PlatformID_test,			//特約合作平台商代號
//						  Config.PlatformChargeFee_test,	//特約合作平台商手續費
//						  Config.AppCode_PlatformID_test, 	//App代碼 
//						  Config.getMerchantTradeNo(), 		//廠商交易編號
//						  Config.getMerchantTradeDate(), 	//廠商交易時間
//						  Config.TotalAmount_test, 			//交易金額
//						  Config.TradeDesc_test, 			//交易描述
//						  Config.ItemName_test, 			//商品名稱
//						  PAYMENTTYPE.CREDIT, 				//付款方式
//						  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//						  "亨利", 							//持卡人姓名
//						  PhoneNumber, 						//手機號碼
//						  "4311952222222222", 				//信用卡卡號
//						  "204012", 						//有效年月
//						  "222",							//卡片末三碼
//						  3,								//分期期數
//						  0,								//分期的付款金額
//						  false);							//是否使用紅利折抵
				
				//************** 有PlatformID、手續費、平台商會員編號******************
//				BackgroundCredit oBackgroundCredit = new BackgroundCredit(
//						  Config.MerchantID_test, 			//廠商編號
//						  Config.PlatformID_test,			//特約合作平台商代號
//						  Config.PlatformMemberNo_test,		//特約合作平台商會員編號
//						  Config.PlatformChargeFee_test,	//特約合作平台商手續費，若無則帶0
//						  Config.AppCode_PlatformID_test, 	//App代碼 
//						  Config.getMerchantTradeNo(), 		//廠商交易編號
//						  Config.getMerchantTradeDate(), 	//廠商交易時間
//						  Config.TotalAmount_test, 			//交易金額
//						  Config.TradeDesc_test, 			//交易描述
//						  Config.ItemName_test, 			//商品名稱
//						  PAYMENTTYPE.CREDIT, 				//付款方式
//						  Config.AppEnvironment, 			//介接環境 : STAGE為測試，OFFICIAL為正式
//						  "亨利", 							//持卡人姓名
//						  PhoneNumber, 						//手機號碼
//						  "4311952222222222", 				//信用卡卡號
//						  "204012", 						//有效年月
//						  "222",							//卡片末三碼
//						  3,								//分期期數
//						  0,								//分期的付款金額
//						  false);							//是否使用紅利折抵
				
				
				//使用ProgressDialog
				new EcpayAsyncTask<BackgroundCredit, API_Credit>(MainBackground.this, API_Credit.class, pd).execute(oBackgroundCredit);
				//不使用ProgressDialog
//				new EcpayAsyncTask<BackgroundCredit, API_Credit>(MainBackground.this, API_Credit.class).execute(oBackgroundCredit);

			}else if(iBtnID == R.id.btnOTP){
				//************** 沒PlatformID ******************
				BackgroundOTP oBackgroundOTP = new BackgroundOTP(OtpMerchantID, 
																 OtpMerchantTradeNo, 
																 OtpTradeNo, 
																 edtOTP.getText().toString(),
																 Config.AppEnvironment 				//介接環境 : STAGE為測試，OFFICIAL為正式
				);
				
				//************** 有PlatformID ******************
//				BackgroundOTP oBackgroundOTP = new BackgroundOTP(OtpMerchantID,
//																Config.PlatformID_test,
//																 OtpMerchantTradeNo, 
//																 OtpTradeNo, 
//																 edtOTP.getText().toString(), 
//																 Config.AppEnvironment 				//介接環境 : STAGE為測試，OFFICIAL為正式
//				);
				
				//使用ProgressDialog
				new EcpayAsyncTask<BackgroundOTP, API_Credit>(MainBackground.this, API_Credit.class, pd).execute(oBackgroundOTP);
				//不使用ProgressDialog
//				new EcpayAsyncTask<BackgroundOTP, API_Credit>(MainBackground.this, API_Credit.class).execute(oBackgroundOTP);
				
			}
			
		}
		
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layoutOTP.getVisibility() == View.VISIBLE) {
				layoutOTP.setVisibility(View.GONE);
				layoutMenu.setVisibility(View.VISIBLE);
				
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onEcpayBackgroundTaskCompleted(API_Base oApi_Base) {
		// TODO Auto-generated method stub
		String sResult = "";
		
		if(oApi_Base.RtnCode == 2){//ATM 取號成功 or 臨櫃繳款	
			API_ATM oApi_ATM = (API_ATM)oApi_Base;
//			if(isCounterPayIN){
//				sResult = "臨櫃繳款\n\n" + oApi_ATM.toString();
//			}else{
				sResult = "ATM 取號成功\n\n" + oApi_ATM.toString();
//			}
						
		}else if(oApi_Base.RtnCode == 10100073){//超商取號成功
			API_CVS oApi_CVS = (API_CVS)oApi_Base;
			sResult = "CVS 取號成功\n\n" + oApi_CVS.toString();
			
		}else if(oApi_Base.RtnCode == 3){//信用卡OTP驗證
			edtOTP.setText("");
			layoutOTP.setVisibility(View.VISIBLE);
			layoutMenu.setVisibility(View.GONE);		
			
			API_Credit oApi_Credit = (API_Credit)oApi_Base;
			OtpMerchantID = oApi_Credit.MerchantID;
			OtpMerchantTradeNo = oApi_Credit.MerchantTradeNo;
			OtpTradeNo = oApi_Credit.TradeNo;
			txtOtpExpiredTime.setText("驗證碼到期時間：" + oApi_Credit.OtpExpiredTime);
			return;
			
		}else if(oApi_Base.RtnCode == 10000030){//驗證碼錯誤，請再輸入一次
			Toast.makeText(MainBackground.this, oApi_Base.RtnMsg, Toast.LENGTH_SHORT).show();
			edtOTP.setText("");
			
			return;
			
		}else if(oApi_Base.RtnCode == 1){//信用卡付款成功		
			API_Credit oApi_Credit = (API_Credit)oApi_Base;
			sResult = "Credit 付款成功\n\n" + oApi_Credit.toString();
			
		}else{			
			sResult = oApi_Base.toString();
		}
		
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				layoutOTP.setVisibility(View.GONE);
				layoutMenu.setVisibility(View.VISIBLE);	
			}
		}, 1000);
		
		
		Intent it2result = new Intent(MainBackground.this, Result.class);
		it2result.putExtra(Result.EXTRA_RESULT, sResult);
		
		startActivity(it2result);
		
	}
	
	Handler handler = new Handler();
	
	
}
