package com.bluepower.threads;

import java.io.IOException;
import java.net.DatagramPacket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.request.UDPPacket;
import com.bluepower.udpserver.UDPServer;
import com.bluepower.utility.EncryptUtility;

public class SendThread implements Runnable {
	private static Log log = LogFactory.getLog(SendThread.class);
	private int i;

	public void run() {
		UDPPacket r;

		while (true) {
			if ((r=UDPServer.responses.getRequest()) != null) {
				
				try {
					if (r != null) {
						//log.info("Send111: " + r.getMagic());
						EncryptUtility.sc_encrypt(r);
						//log.info("Send222: " + r.getMagic());
						
						UDPServer.ds.send(new DatagramPacket(r.content, r.len, r.address, r.port));
						//log.info("Send: " + r.content);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				r = null;

			} else {
				// waiting for notification
				try {
					//log.info("waiting for responses...");
					synchronized (UDPServer.responses) {
						UDPServer.responses.wait();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
}