package com.bluepower.command;

public class UDPConst {

	public static final int 	ENTITY_ANY = 0;
	public static final int 	ENTITY_SERVER = 1;
	public static final int 	ENTITY_MOBILE = 2;
	public static final int 	ENTITY_SOCKET = 3;
	public static final int 	ENTITY_REMOTE_CONTROL = 4;
	public static final int 	ENTITY_CAMERA = 5;
	public static final int 	ENTITY_MAX = 5;
	
	public static final int 	DIRECT_CONNECTION = 0;
	public static final int 	PASSTHROUGH_CONNECTION = 1;
	public static final int 	FORWARD_CONNECTION = 2;
	public static final int 	CONNECTION_MAX = 2;
	
	public static final int 	REQUEST_NOT_NEED_RESPONSE = 0;
	public static final int 	REQUEST_NEED_RESPONSE = 1;
	public static final int 	RESPONSE = 2;
	public static final int 	MESSAGE = 3;
	public static final int 	REQUEST_TYPE_MAX = 3;
	

	public static String p12Path = null;

	public static String getP12Path() {
		String system = null;

		if (p12Path == null) {
			system = System.getProperties().getProperty("os.name");
			if (system.toLowerCase().contains("win"))
				p12Path = "E://SmartPlugPush_ServeP12.p12";
			else
				p12Path = "/SmartPlugPush_ServeP12.p12";
		}
		return p12Path;
	}
	
}
