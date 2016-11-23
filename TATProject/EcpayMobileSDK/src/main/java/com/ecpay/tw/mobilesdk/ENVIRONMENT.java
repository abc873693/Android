package com.ecpay.tw.mobilesdk;

public enum ENVIRONMENT {
	OFFICIAL("https://payment.ecpay.com.tw/"),
    STAGE("https://payment-stage.ecpay.com.tw/"),
    BETA("http://payment-beta.ecpay.com.tw/");
    private ENVIRONMENT(String name){
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
}
