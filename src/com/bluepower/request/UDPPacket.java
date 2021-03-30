package com.bluepower.request;

import java.net.InetAddress;

import com.scwindow.utility.MISCUtility;

public class UDPPacket {

	public static final int PACKET_HEAD_LEN = 96;
	
	public static final int SC_ENT_UNDEFINED = 0;
	public static final int SC_ENT_MOBILE = 1;
	public static final int SC_ENT_GATEWAY = 2;
	public static final int SC_ENT_SERVER = 3;	

	public static final int SC_CNN_DISCONNECTED = 0;
	public static final int SC_CNN_LOCAL = 1;
	public static final int SC_CNN_PASSTHROUGH = 2;
	public static final int SC_CNN_FOWRARD = 3;


	public static final int SC_MGE_KEEPALIVE = 1;
	public static final int SC_MGE_BIND = 2;
	public static final int SC_MGE_CONNECT = 3;
	public static final int SC_MGE_GATEWAY_ROLE = 4;
	public static final int SC_MGE_SERVER_ROLE = 5;
	public static final int SC_MGE_GATEWAY_STATUS = 6;

	
	public static final int SC_DATA_MANAGEMENT = 1;
	public static final int SC_DATA_USER = 2;
	
	
	public static final int SC_CMD_DEFAULT = 0;
	public static final int SC_CMD_REQ_NO_RSP = 1;
	public static final int SC_CMD_REQ_NEED_RSP = 2;
	public static final int SC_CMD_RSP = 3;
	

	public static final int SC_SUCCESS = 0;
	public static final int SC_FAILED = -1;
	public static final int SC_TIMEOUT = 1;
	
	// remote ip
	public InetAddress address;
	// remote port
	public int	port;
	// 接收或要发送的byte[] 的 length
	public int	len;
	// 接收或要发送的byte[]
	public byte[] content;
	
	public UDPPacket(InetAddress address, int port, byte[] content, int len) {
		this.address = address;
		this.port = port;
		this.len = len;
		this.content = new byte[len];
		System.arraycopy(content, 0, this.content, 0, len);		
	}
	
	public UDPPacket() {
		content = new byte[340];
	}
	

	public short getVersion() {		
		return (short) ((short)(content[0] & 0xff) + (short)((content[1] & 0xff) << 8)) ;
	}
	
	public void SwitchFromTo() {
		byte temp[] = new byte[15];
		System.arraycopy(content, 2, temp, 0, 15);
		System.arraycopy(content, 17, content, 2, 15);
		System.arraycopy(temp, 0, content, 17, 15);		
	}
	
	public String getFromId(){
		String fromid = null;		
		fromid = MISCUtility.getStringFromBytes(2, content);		
		return fromid;
	}	

	public int getFromType(){
		String fromid = null;		
		int ret = -1;
		fromid = MISCUtility.getStringFromBytes(2, content);
		if (fromid.length() == 11)
			ret = SC_ENT_MOBILE;
		else if(fromid.length() == 12)
			ret = SC_ENT_GATEWAY;
		
		return ret;		
	}
	
	
	/** 
	 * @Title: getIsNB 
	 * @Description: judge wheather the packet communicator is NB device
	 * @return 
	 */
	public char getIsNB() {
		return (char)(content[48]);
	}
	
	/** 
	 * @Title: getNBId 
	 * @Description: get NBId from content bytes[] (communicate packet).
	 * @return 
	 */
	public String getNBId() {
		String toId = null;
		toId = MISCUtility.getStringFromBytes(49, content);
		return toId;
	}
	
	
	public String getToId(){
		String toid = null;		
		toid = MISCUtility.getStringFromBytes(17, content);		
		return toid;
	}
	
	public int getCmdtype() {		
		return content[32] & 0x07;
	}
	
	public void setCmdtype(int i) {		
		byte bi = (byte)(i & 0x07);
		content[32] = (byte) (content[32] | bi);
	}
	
	
	
	public int getEncry() {		
		return (content[32] >> 3) & 0x07;
	}

	public void setEncry(int i) {		
		byte bi = (byte)((i & 0x07) << 3);
		content[32] = (byte) ((content[32] & 0xc7) | bi);
	}

	
	
	public int getConnType() {		
		return (content[32] >> 6) & 0x03;
	}
	
	public int getDataType() {		
		return content[33] & 0x0f;
	}
	
	public int getSizeType() {		
		return (content[33] >> 4) & 0x0f;
	}

	public int getPktLen() {		
		return (content[34] & 0xff) + ((content[35] & 0xff) << 8);
	}

	public int getSeq() {		
		return (int)(content[36] & 0xff) + (int)((content[37] & 0xff) << 8)  + (int)((content[38] & 0xff) << 16)  + (int)((content[39] & 0xff) << 24) ;
	}
	

	public void setTime() {
		long time = System.currentTimeMillis();
		content[40] = (byte) ((time & 0xff));
		content[41] = (byte) (((time >> 8) & 0xff));
		content[42] = (byte) (((time >> 16) & 0xff));
		content[43] = (byte) (((time >> 24) & 0xff));
		content[44] = (byte) (((time >> 32) & 0xff));
		content[45] = (byte) (((time >> 40) & 0xff));
		content[46] = (byte) (((time >> 48) & 0xff));
		content[47] = (byte) (((time >> 56) & 0xff));
		
	}
	
	

	public int getMagic() {
		int magic = 0;
		magic = (content[PACKET_HEAD_LEN - 4] & 0xff);
		magic += (content[PACKET_HEAD_LEN - 3] & 0xff) << 8;
		magic += (content[PACKET_HEAD_LEN - 2] & 0xff) << 16;
		magic += (content[PACKET_HEAD_LEN - 1] & 0xff) << 24;
		
		return magic;
	}

	public int getMgeCmd() {		

		int cmd = 0;
		cmd = (content[PACKET_HEAD_LEN] & 0xff);
		cmd += (content[PACKET_HEAD_LEN + 1] & 0xff) << 8;
//		cmd += (content[PACKET_HEAD_LEN + 2] & 0xff) << 16;
//		cmd += (content[PACKET_HEAD_LEN + 3] & 0xff) << 24;
		
		return cmd;
	}
	


	/** 
	 * @Title: getMgeInputInt1 
	 * @Description: get inputInt1 filed in manage packet
	 * @return 
	 */
	public int getMgeInputint1() {

		int inputint1 = 0;
		inputint1 = (content[PACKET_HEAD_LEN + 4] & 0xff);
		inputint1 += (content[PACKET_HEAD_LEN + 5] & 0xff) << 8;
		inputint1 += (content[PACKET_HEAD_LEN + 6] & 0xff) << 16;
		inputint1 += (content[PACKET_HEAD_LEN + 7] & 0xff) << 24;
		
		return inputint1;
		
	}
	

	/** 
	 * @Title: setMgeRetInt1 
	 * @Description: set returnInt1 field in manage packet
	 * @param a 
	 */
	public void setMgeRetint1(int a) {
		content[PACKET_HEAD_LEN + 12] = (byte)(a & 0xff);
		content[PACKET_HEAD_LEN + 13] = (byte)((a >> 8) & 0xff);
		content[PACKET_HEAD_LEN + 14] = (byte)((a >> 16) & 0xff);
		content[PACKET_HEAD_LEN + 15] = (byte)((a >> 24) & 0xff);
	}
	

	/** 
	 * @Title: getMgeInputstr1 
	 * @Description: get inputstr1 filed in manage packet
	 * @return 
	 */
	public String getMgeInputstr1() {
		return MISCUtility.getStringFromBytes(PACKET_HEAD_LEN + 20, content);
	}
	
	/** 
	 * @Title: getMgeInputstr1 
	 * @Description: get (byte[]) inputstr1 filed in manage packet
	 * @return 
	 */
	public byte[] getMgeInputstr1Bytes() {
		byte[] ret = new byte[32];
		for (int i = 0; i < 32; i++) {
			ret[i] = (byte) (content[PACKET_HEAD_LEN + 20 + i] & 0xff);
		}
		return ret;
	}
	

	public void setMgeRetstr1(byte a[]) {
		int len = a.length;
		if (len > 32)
			len = 32;
		
		System.arraycopy(a, 0, content, PACKET_HEAD_LEN + 84, len);		
		content[PACKET_HEAD_LEN + 115] = 0;
		content[PACKET_HEAD_LEN + 84 + len] = 0;
	}
	
	
	
	public String toString() {
		return (this.address + "|" + port + "|" + content);
		
	}
}
