package com.bluepower.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.request.UDPPacket;
import com.bluepower.udpserver.UDPServer;
import com.bluepower.utility.EncryptUtility;

/** 
 * @ClassName: ServerClusterThread 
 * @Description: slave刚启动，或定时向master发送心跳
 * @author: Vincent Sean
 * @date: 2018年6月29日 上午11:21:53  
 */
public class ServerClusterThread implements Runnable {
	private static Log log = LogFactory.getLog(ServerClusterThread.class);
	private int i;

	public void run() {
		InetAddress address = null;
		byte[] content = new byte[512];
		UDPPacket r = null;


		//set version
		content[0] = 1 & 0xff;
		content[1] = 0 & 0xff;
		//set from id 2-16, to id 17-31
		//log.info(UDPServer.myname);
		System.arraycopy(UDPServer.myname.getBytes(), 0, content, 2, UDPServer.myname.getBytes().length);
		System.arraycopy("master".getBytes(), 0, content, 17, "master".getBytes().length);		
		//content[32] xxx 1 no rsp, xxx 1 have encry, xx  1 local
		content[32] = (1 & 0x07) | ((0 & 0x07) << 3) | ((1 & 0x03) << 6) ;
		//content[33] xxxx 1 manage data, xxxx 0
		content[33] = (1 & 0x0f) | ((0 & 0xf0) << 4);
		//content length 242
		content[34] = (byte) (244 & 0xff);
		content[35] = 0 & 0xff;
			
		
		//set magic [92] [93] [94] [95]
		content[92] = (byte)(12341234 & 0x000000ff);
		content[93] = (byte)((12341234 & 0x0000ff00) >> 8);
		content[94] = (byte)((12341234 & 0x00ff0000) >> 16);
		content[95] = (byte)((12341234 & 0xff000000) >> 24);

		//create mgmt packet content
		content[96] = (byte)(5 & 0x000000ff);
		content[97] = (byte)(0 & 0x000000ff);
		content[98] = (byte)(0 & 0x000000ff);
		content[99] = (byte)(0 & 0x000000ff);
		
		//subcommand
		content[100] = (byte)(1 & 0x000000ff);
		content[101] = (byte)(0 & 0x000000ff);
		content[102] = (byte)(0 & 0x000000ff);
		content[103] = (byte)(0 & 0x000000ff);
		
		

		try {
			address=InetAddress.getByName(UDPServer.masterip);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			log.info(e2.getMessage());
		}

		
		while (true) {
			try {
				long seq = System.currentTimeMillis()/100;
				content[36] = (byte)(seq & 0xff);
				content[37] = (byte)((seq >> 8) & 0xff);
				content[38] = (byte)((seq >> 16) & 0xff);
				content[39] = (byte)((seq >> 24) & 0xff);
				r = new UDPPacket(address, UDPServer.masterport, content, 512);
				
				//log.info("gaga " + r.getMagic());
				EncryptUtility.sc_encrypt(r);
				//log.info("gege " + r.getMagic());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			try {

				UDPServer.ds.send(new DatagramPacket(r.content, r.len, r.address, r.port));
				Thread.sleep(60 * 1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info(e.getMessage());
			}
		}
	}
}