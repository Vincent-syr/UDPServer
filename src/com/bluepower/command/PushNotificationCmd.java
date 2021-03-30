package com.bluepower.command;

public class PushNotificationCmd extends UDPSubCommand{
	public final static int 	subCommandType = 15;

	int		type;
	String 	time;
	String  param1;
	String  param2;
	String  param3;
	
	public PushNotificationCmd(int type, String time, String param1, String param2, String param3) {
		this.type = type;
		this.time = time;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;		
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}

	public String getParam1() {		
		return param1;
	}
	
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {		
		return param2;
	}
	
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {		
		return param3;
	}
	
	public void setParam3(String param3) {
		this.param3 = param3;
	}
		
	public String getPushContent(String devicename) {
		String content = null;



		switch (type) {

		// deal with keep alive requests
		case 0:
			content = "插座" + devicename + "在";
			content += time;
			
			if(param1.equals("0"))
				content += "触发定时关闭。";
			else
				content += "触发定时开启。";
			
			break;

		default:
			break;
		}
		
		return content;
		
	}
}
