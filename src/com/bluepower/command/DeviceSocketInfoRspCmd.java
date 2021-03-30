package com.bluepower.command;

public class DeviceSocketInfoRspCmd extends UDPSubCommand{
	public final static int 	subCommandType = 5;
	
	int 		result;
	int 		type;
	int 		onoff;
	float 		elec;
	
	public DeviceSocketInfoRspCmd(int result, int type, int onoff, float elec) {
		this.result = result;
		this.type = type;
		this.onoff = onoff;
		this.elec = elec;		
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
	
	public int getOnoff() {
		return onoff;
	}
	
	public void setOnoff() {
		
	}
	
	public float getElec() {
		return elec;
	}
	
	public void setElec(float elec) {
		this.elec = elec;
	}
	
	
}
