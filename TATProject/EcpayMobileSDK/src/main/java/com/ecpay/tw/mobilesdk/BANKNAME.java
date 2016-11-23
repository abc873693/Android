package com.ecpay.tw.mobilesdk;

/** 2016-10-05
 * TAISHIN	        ATM_台新
 * ESUN	                ATM_玉山
 * BOT	                ATM_台灣銀行
 * FUBON	        ATM_台北富邦
 * CHINATRUST	ATM_中國信託
 * FIRST	               ATM_第一銀行
 */
public enum BANKNAME {
	TAISHIN("TAISHIN"),
	ESUN("ESUN"),
	FUBON("FUBON"),
	BOT("BOT"),
	CHINATRUST("CHINATRUST"),
    FIRST("FIRST");
	
	private BANKNAME(String name){
    	this.setName(name);
    }
    private String name;
    public String getName() {
		return name;
	}
    public void setName(String name){
    	this.name = name;
    }
    public String toString(){
    	return this.getName();
    }
    
    public static BANKNAME parse2BankName(String str){
    	BANKNAME bankName = null;
    	//
    	if(str.equalsIgnoreCase("TAISHIN"))
    		bankName = BANKNAME.TAISHIN;
    	else if(str.equalsIgnoreCase("ESUN"))
    		bankName = BANKNAME.ESUN;
    	else if(str.equalsIgnoreCase("FUBON"))
    		bankName = BANKNAME.FUBON;
    	else if(str.equalsIgnoreCase("BOT"))
    		bankName = BANKNAME.BOT;
    	else if(str.equalsIgnoreCase("CHINATRUST"))
    		bankName = BANKNAME.CHINATRUST;
    	else if(str.equalsIgnoreCase("FIRST"))
    		bankName = BANKNAME.FIRST;
		//
    	return bankName;
    }
}
