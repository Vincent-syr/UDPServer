package com.bluepower.command;

public class TimeSettingReqCmd extends UDPSubCommand{
	public final static int 	subCommandType = 2;
	

	int		type;
	String 	timezone;
	
	public TimeSettingReqCmd(int type, String timezone) {
		this.type = type;
		this.timezone = timezone;
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
}
