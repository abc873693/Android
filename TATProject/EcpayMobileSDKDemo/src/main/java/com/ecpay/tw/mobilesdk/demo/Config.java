package com.ecpay.tw.mobilesdk.demo;

import java.util.ArrayList;
import java.util.Arrays;
import com.ecpay.tw.mobilesdk.BANKNAME;
import com.ecpay.tw.mobilesdk.ENVIRONMENT;
import com.ecpay.tw.mobilesdk.STORETYPE;
import android.annotation.SuppressLint;


public class Config {
	public static String LOGTAG = "EcpayMobileSDKDemo";
	
	public static int REQUEST_CODE = 1001;
	
	public static String MerchantID_test = "2000132";
	public static String PlatformID_test = "2000132";
	public static String PlatformMemberNo_test = "222222"; 
	public static String PlatformChargeFee_test = "1";
	public static String AppCode_test = "EcTest";
	public static String AppCode_PlatformID_test = "EcTest";
	public static int TotalAmount_test = 100;
	public static String TradeDesc_test = "Ecpay商城購物";
	public static String ItemName_test = "手機20元X2#隨身碟60元X1";
	
	public static ArrayList<BANKNAME> lstBankName = new ArrayList<BANKNAME>(Arrays.asList(BANKNAME.TAISHIN, BANKNAME.ESUN, BANKNAME.FUBON, BANKNAME.BOT, BANKNAME.CHINATRUST, BANKNAME.FIRST));
	public static ArrayList<STORETYPE> lstStoreType = new ArrayList<STORETYPE>(Arrays.asList(STORETYPE.CVS, STORETYPE.IBON));
	
	public static ArrayList<String> aryBankName = new ArrayList<String>(Arrays.asList("All", BANKNAME.TAISHIN.toString(), BANKNAME.ESUN.toString(), BANKNAME.FUBON.toString(), BANKNAME.BOT.toString(), BANKNAME.CHINATRUST.toString(), BANKNAME.FIRST.toString()));
	public static ArrayList<String> aryStoreType = new ArrayList<String>(Arrays.asList("All", STORETYPE.CVS.toString(), STORETYPE.IBON.toString()));

	public static ENVIRONMENT AppEnvironment = ENVIRONMENT.STAGE;// 介接環境(STAGE測試以及OFFICIAL正式)
	public static ArrayList<String> aryAppEnvironment = new ArrayList<String>(Arrays.asList("Stage", "Official", "Beta"));
	
	@SuppressLint("SimpleDateFormat")
	public static String getMerchantTradeNo(){		
		java.util.Date now = new java.util.Date();
		return new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(now);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getMerchantTradeDate(){
		java.util.Date now = new java.util.Date();
		return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(now);
	}
	
	
	
}
