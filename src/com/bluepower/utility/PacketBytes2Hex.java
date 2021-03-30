package com.bluepower.utility;

import java.rmi.server.LoaderHandler;

import javax.swing.text.AbstractDocument.Content;

import org.apache.log4j.Logger;

import com.bluepower.request.UDPPacket;
import com.mysql.jdbc.log.Log;

import javapns.test.Test;

/** 
 * @ClassName: PacketBytes2Hex 
 * @Description: it is simulator of mobile phone, produce the 0xpacket  made from mobile phone
 * 		test result : successfule
 * @author: Vincent Sean
 * @date: 2018年7月11日 上午11:23:47  
 */
public class PacketBytes2Hex {
	public static Logger log = Logger.getLogger(PacketBytes2Hex.class);
	
	public static String bytes2Hex() {
		
		// packet Header
		String fromId = "nb";
		String toId = "master";
		byte cmd_type = 1;
		byte encry = 2;
		byte conn_type = 1 ;
		byte data_type = 1;
		char isNB = '1';
//		String resv = "";
		String resv = "93647e5a8d63473088db9f5aed52acf4";
		
		// packet Content
		byte cmd = 4;
		byte inputint = 1;		
		
		// 固定不变的
		byte size_type = 0;
		byte pkt_len = 0;
		byte seq = 29;
		long time;
		int magic = 12341234;
		
		// work block
		String retHex = null;
		UDPPacket packet = new UDPPacket();
		// set version = 1
		packet.content[0]=1;
		packet.content[1]=0;
		
		// set fromId
		byte[] temp = fromId.getBytes();
		log.info("fromId:  temp.length = " + temp.length);
		for(int i=2; i<temp.length+2 && i<=16 ; i++) {
			packet.content[i] = temp[i-2];
		}
		
		// set toId
		temp = toId.getBytes();
		log.info("toId:  temp.length = " + temp.length);
		for(int i=17; i<temp.length+17 && i<=31; i++) {
			packet.content[i] = temp[i-17];
		}
		
		// 32
		byte temp2;
		temp2 = (byte) (cmd_type + (encry << 3) + (conn_type << 6));
		packet.content[32] = temp2;
		
		// 33
		temp2 = (byte) (data_type + (size_type << 4));
		packet.content[33] = temp2;
		
		// 34-35 short pkt_len
		packet.content[34] = pkt_len;
		packet.content[35] = 0;
		
		// 36-39 short int seq
		packet.content[36]=29;
		
		// 48 char isNB
		packet.content[48] = (byte)isNB;
		
		// 49-91 char resv[51]
		temp = resv.getBytes();
		for(int i=49; i<=91 && i<temp.length+49; i++) {
			packet.content[i] = temp[i-49];
		}
		
		// 92-95 magic
		temp = int2byte(magic);
		for(int i=92; i<=95 && i<temp.length+92; i++) {
			packet.content[i] = temp[i-92];
		}
		
		// content of packet
		// 96-99 cmd
		packet.content[96] = cmd;
		
		// 100-103 inputint1
		packet.content[100] = 1;
		
		
//		// check: check if result is ok
//		for(int i=0; i<340; i++) {
//				log.info("packet.content[" + i + "] = 0x" + (Integer.toHexString(0xFF & packet.content[i])).toUpperCase());
//		}
		
		// convert to hex
		StringBuffer sb = new StringBuffer();
		String tempStr;
		for(int i=0; i<packet.content.length; i++) {
			tempStr = Integer.toHexString(0xFF & packet.content[i]).toUpperCase();
			if(tempStr.length() == 1) {
				sb.append("0" + tempStr);
				log.info("packet.content[" + i + "] = 0x0" + tempStr);
			}
			else {
				sb.append(tempStr);
				log.info("packet.content[" + i + "] = 0x" + tempStr);
			}
		}
		retHex = sb.toString();		
		return retHex;
	}
	
	
		// int to byte, check ok
		public static byte[] int2byte(int data) {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (data & 0xff);
			bytes[1] = (byte) ((data >> 8) & 0xff);
			bytes[2] = (byte) ((data >> 16) & 0xff);
			bytes[3] = (byte) ((data >> 24) & 0xff);
			return bytes;
		}
		
		// bytes to int, check ok
		public static int byte2Int(byte[] bytes) {
			return (int) ((0xff & bytes[0]) | 
					(0xff00 & (bytes[1] << 8)) |
					(0xff0000 & (bytes[2] << 16)) |
					(0xff000000 & (bytes[3] << 24))	
					);
		}
		
		
		
	public static void test() {
		String result = bytes2Hex();
		log.info("result.length = " + result.length());
		log.info("result = " + result);
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		test();
		
//		String str = "106E6200000000000006D61737"
//				+ "46572000000000511001D0000000000"
//				+ "0313933363437653561386436333437"
//				+ "333038386462396635616564353261636"
//				+ "63400000000000F24FBC0400010000000"
//				+ "000000000000000000000000000000000"
//				+ "0000000000000000000000000000000000"
//				+ "0000000000000000000000000000000000"
//				+ "00000000000000000000000000000000000"
//				+ "00000000000000000000000000000000000"
//				+ "00000000000000000000000000000000000"
//				+ "00000000000000000000000000 ";
//		System.out.println(str.length());
	}
}



