package com.ecpay.tw.mobilesdk.demo;

import com.ecpay.tw.mobilesdk.BANKNAME;
import com.ecpay.tw.mobilesdk.CREDITTYPE;
import com.ecpay.tw.mobilesdk.CreateTrade;
import com.ecpay.tw.mobilesdk.OptionalATM;
import com.ecpay.tw.mobilesdk.OptionalCVS;
import com.ecpay.tw.mobilesdk.OptionalCreditInstallment;
import com.ecpay.tw.mobilesdk.OptionalCreditPeriodAmount;
import com.ecpay.tw.mobilesdk.PAYMENTTYPE;
import com.ecpay.tw.mobilesdk.PERIODTYPE;
import com.ecpay.tw.mobilesdk.PaymentActivity;
import com.ecpay.tw.mobilesdk.STORETYPE;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Main extends Activity {
	
	Button btnAll, btnATM, btnCVS, btnCredit, btnCreditInstallment, btnCreditPeriodAmount;
	Spinner spnATM, spnCVS;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle("訂單產生(幕前)");
		
		btnAll = (Button)findViewById(R.id.btnAll);
		btnATM = (Button)findViewById(R.id.btnATM);
		btnCVS = (Button)findViewById(R.id.btnCVS);
		btnCredit = (Button)findViewById(R.id.btnCredit);
		btnCreditInstallment = (Button)findViewById(R.id.btnCreditInstallment);
		btnCreditPeriodAmount = (Button)findViewById(R.id.btnCreditPeriodAmount);		
		spnATM = (Spinner)findViewById(R.id.spinner_ATM);
		spnCVS = (Spinner)findViewById(R.id.spinner_CVS);
		
		btnAll.setOnClickListener(clk_Listener);
		btnATM.setOnClickListener(clk_Listener);
		btnCVS.setOnClickListener(clk_Listener);
		btnCredit.setOnClickListener(clk_Listener);
		btnCreditInstallment.setOnClickListener(clk_Listener);
		btnCreditPeriodAmount.setOnClickListener(clk_Listener);
		
		ArrayAdapter<String> adpATM = new ArrayAdapter<String>(Main.this,  android.R.layout.simple_spinner_item, Config.aryBankName);
		adpATM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnATM.setAdapter(adpATM);
		
		ArrayAdapter<String> adpCVS = new ArrayAdapter<String>(Main.this, android.R.layout.simple_spinner_item, Config.aryStoreType);
		adpCVS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnCVS.setAdapter(adpCVS);
		
	}
	
	Button.OnClickListener clk_Listener = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub		
			PAYMENTTYPE paymentType = null;
			Intent intent = new Intent(Main.this, PaymentActivity.class);
			
			int iBtnID = v.getId();
			if(iBtnID == R.id.btnAll){
				paymentType = PAYMENTTYPE.ALL;				
				
			}else if(iBtnID == R.id.btnATM){
				//自動櫃員機(ATM)
				paymentType = PAYMENTTYPE.ATM;
				
				//選擇性參數，允許繳費有效天數(1~60)
//				OptionalATM oOptionalATM = new OptionalATM(7);
				OptionalATM oOptionalATM = new OptionalATM(7, BANKNAME.parse2BankName(spnATM.getSelectedItem().toString()));
				intent.putExtra(PaymentActivity.EXTRA_OPTIONAL, oOptionalATM);	
				
			}else if(iBtnID == R.id.btnCVS){
				//超商代碼(CVS)
				paymentType = PAYMENTTYPE.CVS;
				
				//選擇性參數，交易描述1~4，會出現在超商繳費平台螢幕上
//				OptionalCVS oOptionalCVS = new OptionalCVS("測試1", "測試2", "", "", null);
				OptionalCVS oOptionalCVS = new OptionalCVS("測試1", "測試2", "", "", STORETYPE.parse2StoreType(spnCVS.getSelectedItem().toString()));
				intent.putExtra(PaymentActivity.EXTRA_OPTIONAL, oOptionalCVS);
				
			}else if(iBtnID == R.id.btnCredit){	
				//信用卡			
				paymentType = PAYMENTTYPE.CREDIT;
				
			}else if(iBtnID == R.id.btnCreditInstallment){	
				//信用卡(分期)
				paymentType = PAYMENTTYPE.CREDIT;
				intent.putExtra(PaymentActivity.EXTRA_OPTIONAL_CREDITTYPE, CREDITTYPE.INSTALLMENT);
				
				OptionalCreditInstallment oCreditInstallment = new OptionalCreditInstallment(3,			//分期期數 
																							 0, 		//分期的付款金額
																							 false, 	//是否使用紅利折抵
																							 false);	//是否為銀聯卡交易(需向歐付寶提出申請，才能使用)
				intent.putExtra(PaymentActivity.EXTRA_OPTIONAL, oCreditInstallment);
			}else if(iBtnID == R.id.btnCreditPeriodAmount){
				//信用卡(定期定額)
				paymentType = PAYMENTTYPE.CREDIT;
				intent.putExtra(PaymentActivity.EXTRA_OPTIONAL_CREDITTYPE, CREDITTYPE.PERIODAMOUNT);
				
				//EX:每個月扣1次100元，總共要扣12次
				OptionalCreditPeriodAmount oPeriodAmount = new OptionalCreditPeriodAmount(Config.TotalAmount_test,	//每次授權金額(當此參數有設定金額，需與TotalAmount相同) 
																						  PERIODTYPE.MONTH, 		//週期種類
																						  1, 						//執行頻率
																						  12);						//執行天數
				intent.putExtra(PaymentActivity.EXTRA_OPTIONAL, oPeriodAmount);
			}
			
			//************** 沒PlatformID ******************
			CreateTrade oCreateTrade = new CreateTrade(
					Config.MerchantID_test,				//廠商編號 
					Config.AppCode_test, 				//App代碼
					Config.getMerchantTradeNo(), 			//廠商交易編號
					Config.getMerchantTradeDate(), 		//廠商交易時間
					Config.TotalAmount_test, 				//交易金額
					Config.TradeDesc_test, 				//交易描述
					Config.ItemName_test, 				//商品名稱
					paymentType, 						//預設付款方式
					Config.AppEnvironment);				//介接環境 : STAGE為測試，OFFICIAL為正式
			
//			//************** 有PlatformID ******************
//			CreateTrade oCreateTrade = new CreateTrade(
//					Config.MerchantID_test,				//廠商編號
//					Config.PlatformID_test,				//特約合作平台商代號
//					Config.AppCode_PlatformID_test, 		//App代碼
//					Config.getMerchantTradeNo(), 			//廠商交易編號
//					Config.getMerchantTradeDate(), 		//廠商交易時間
//					Config.TotalAmount_test, 				//交易金額
//					Config.TradeDesc_test, 				//交易描述
//					Config.ItemName_test, 				//商品名稱
//					paymentType, 						//預設付款方式
//					Config.AppEnvironment);				//介接環境 : STAGE為測試，OFFICIAL為正式
			
//			//************** 有PlatformID、手續費 ******************
//			CreateTrade oCreateTrade = new CreateTrade(
//					Config.MerchantID_test,				//廠商編號 
//					Config.PlatformID_test,				//特約合作平台商代號
//					Config.PlatformChargeFee_test,		//特約合作平台商手續費
//					Config.AppCode_PlatformID_test, 	//App代碼
//					Config.getMerchantTradeNo(), 		//廠商交易編號
//					Config.getMerchantTradeDate(), 		//廠商交易時間
//					Config.TotalAmount_test, 			//交易金額
//					Config.TradeDesc_test, 				//交易描述
//					Config.ItemName_test, 				//商品名稱
//					paymentType, 						//預設付款方式
//					Config.AppEnvironment);				//介接環境 : STAGE為測試，OFFICIAL為正式
			
			//************** 有PlatformID、手續費、平台商會員編號 ******************
//			CreateTrade oCreateTrade = new CreateTrade(
//					Config.MerchantID_test,				//廠商編號 
//					Config.PlatformID_test,				//特約合作平台商代號
//					Config.PlatformMemberNo_test,		//特約合作平台商會員編號
//					Config.PlatformChargeFee_test,		//特約合作平台商手續費，若無手續費則帶0
//					Config.AppCode_PlatformID_test, 	//App代碼
//					Config.getMerchantTradeNo(), 		//廠商交易編號
//					Config.getMerchantTradeDate(), 		//廠商交易時間
//					Config.TotalAmount_test, 			//交易金額
//					Config.TradeDesc_test, 				//交易描述
//					Config.ItemName_test, 				//商品名稱
//					paymentType, 						//預設付款方式
//					Config.AppEnvironment);				//介接環境 : STAGE為測試，OFFICIAL為正式
			
			intent.putExtra(PaymentActivity.EXTRA_PAYMENT, oCreateTrade);
			startActivityForResult(intent, Config.REQUEST_CODE);			
			
		}};
	
	
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
//			super.onActivityResult(requestCode, resultCode, data);
			Log.d(Config.LOGTAG, "requestCode=" + requestCode + " , resultCode=" + resultCode);
			if(requestCode == Config.REQUEST_CODE){
				if(resultCode == PaymentActivity.RESULT_EXTRAS_NULL){
	            	Log.d(Config.LOGTAG, "EXTRA_PAYMENT is NULL ");
	            }else if (resultCode == PaymentActivity.RESULT_EXTRAS_CANCEL) {
	                Log.d(Config.LOGTAG, "The user canceled.");
	            } 
			}
						
		}
		
}
