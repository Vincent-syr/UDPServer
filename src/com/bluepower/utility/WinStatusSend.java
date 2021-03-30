package com.bluepower.utility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.udpserver.UDPServer;

/** 
 * @ClassName: WinStatusSend 
 * @Description: used to send window status,including open-degree, mode,  to the ip
 * @author: Vincent Sean
 * @date: 2018年6月27日 下午2:12:28  
 */
public class WinStatusSend {
	
	private static Log log = LogFactory.getLog(WinStatusSend.class);
	
	public static void send(String message) {
		byte[] by= message.getBytes();
	    InetAddress destination = null ;
	    try {
	    	destination = InetAddress.getByName("192.168.7.106");
	    } catch (UnknownHostException e) {
	    	log.info(e.getMessage());
	        System.exit(1);
	    }
	    try {
	    	UDPServer.ds.send(new DatagramPacket(by, by.length,destination,8890));
	    } catch (IOException e) {
	    	log.info(e.getMessage());
	    }
	}
}
