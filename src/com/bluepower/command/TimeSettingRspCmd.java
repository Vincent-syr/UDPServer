package com.bluepower.command;

public class TimeSettingRspCmd extends UDPSubCommand{
	public final static int 	subCommandType = 3;
	
	int 	result;
	int		type;
	String 	timezone;
	String 	time;
	
	public TimeSettingRspCmd(int result, int type, String timezone, String time) {
		this.result = result;
		this.type = type;
		this.timezone = timezone;
		this.time = time;
	}

	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getTimezone() {
		return timezone;
	}
	
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	public String getTime() {		
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
}
