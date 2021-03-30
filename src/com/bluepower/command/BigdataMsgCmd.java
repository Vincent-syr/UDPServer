package com.bluepower.command;

public class BigdataMsgCmd extends UDPSubCommand{
	public final static int 	subCommandType = 16;
	
	String bigdata;
	

	public BigdataMsgCmd(String bigdata) {
		this.bigdata = bigdata;
	}
	
	public String getBigdata() {
		return this.bigdata;
	}
	
	public void setBigdata(String bigdata) {
		this.bigdata = bigdata;
	}
}
