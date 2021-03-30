package com.bluepower.command;

public class KeepAliveReqCmd extends UDPSubCommand{
	public final static int 	subCommandType = 0;
	
	String bigdata;
	

	public KeepAliveReqCmd(String bigdata) {
		this.bigdata = bigdata;
	}
	
	public String getBigdata() {
		return this.bigdata;
	}
	
	public void setBigdata(String bigdata) {
		this.bigdata = bigdata;
	}
}
