package com.bluepower.threads;

import org.apache.log4j.Logger;

import com.bluepower.narrow_bound.MyAppTask;
import com.bluepower.request.UDPPacket;
import com.bluepower.udpserver.UDPServer;
import com.bluepower.utility.EncryptUtility;

import net.sf.json.util.NewBeanInstanceStrategy;

/** 
 * @ClassName: NBSendThread 
 * @Description: this Thread is to send NB rsp queue packet to NB platform
 * @author: Vincent Sean
 * @date: 2018年7月2日 下午4:00:17  
 */
public class NBSendThread implements Runnable{
	public static Logger log = Logger.getLogger(NBSendThread.class);
	
	public void run() {
		UDPPacket r;

		while (true) {
			if ((r=UDPServer.NbResponses.getRequest()) != null) {
				// 对r加密
				try {
//					EncryptUtility.sc_encrypt(r);
					String rawNbId = r.getNBId();
					log.info("rawNbId = " + rawNbId);
					// 给NbId添加 "-"
					StringBuffer revisedNbId = new StringBuffer();
					revisedNbId.append(rawNbId.substring(0, 8)).append("-").append(rawNbId.substring(8,12)).append("-").
								append(rawNbId.substring(12,16)).append("-").append(rawNbId.substring(16, 20)).append("-")
								.append(rawNbId.substring(20, 32));
					log.info("revisedNbId = " + revisedNbId.toString());
					
					MyAppTask.communicate2IoTPlatform_v5(r.content, "post", revisedNbId.toString(), "0");
				} catch (Exception e) {
					log.error("", e);
				}
			}
			else {
				try {
					synchronized (UDPServer.NbResponses) {
						UDPServer.NbResponses.wait();
					}
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
	}
}
