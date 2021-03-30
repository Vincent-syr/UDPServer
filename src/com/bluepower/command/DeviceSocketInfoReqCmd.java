package com.bluepower.command;

public class DeviceSocketInfoReqCmd extends UDPSubCommand{
	public final static int 	subCommandType = 4;
	
	int		type;
	
	public DeviceSocketInfoReqCmd(int type) {
		this.type = type;
	}
	
	
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
}
