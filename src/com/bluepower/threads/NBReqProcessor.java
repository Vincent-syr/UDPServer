package com.bluepower.threads;

import org.apache.log4j.Logger;

import com.bluepower.command.UDPCommand;
import com.bluepower.entity.Entity;
import com.bluepower.narrow_bound.MyAppTask;
import com.bluepower.request.UDPPacket;
import com.bluepower.udpserver.UDPServer;
import com.bluepower.utility.EncryptUtility;

public class NBReqProcessor {
	
	
	public static Logger log = Logger.getLogger(NBReqProcessor.class);

	/** 
	 * @Title: NBReqProcess 
	 * @Description: process the req packet, if it is NB packet
	 * @param r 
	 * @throws Exception 
	 */
	public static void NBReqProcess(UDPPacket r) throws Exception {
		UDPCommand cmd = null;
		Entity entity = null;
		
		// forward packet from gateway or cellphone,  to NB platform 
		if (r.getConnType() == UDPPacket.SC_CNN_FOWRARD) {
			log.info("fromId = " + r.getFromId());
			log.info("toId = " + r.getToId());
			
			// forward packet to NB platform
			if(r.getToId().equals("nb")) {
				// put r into Nb rsp queue
				UDPServer.NbResponses.putRequest(r);
				log.info("r is pushed into NB rsp queue");
				synchronized (UDPServer.NbResponses) {
					UDPServer.NbResponses.notifyAll();
				}
			}
			
			
			// forward packet from nb platform, to gateway or mobile phone
			else if(r.getFromId().equals("nb")) {
				//forward message, forward to entity
				entity = UDPServer.entityMap.get(r.getToId());
				if (entity != null) {
					r.address = entity.address;
					r.port = entity.port;
				} else {
					log.error("toId entity do not exist");
					return;
				}						
				
				//log.info("forward from " + r.getFromId() + " to " + r.getToId());
				// responses is 发送队列
				UDPServer.responses.putRequest(r);
				synchronized (UDPServer.responses) {
					UDPServer.responses.notifyAll();
				}
			}	
			return;
			
		}
		
		else if (r.getConnType() == UDPPacket.SC_CNN_LOCAL) {
			// decrypt
			EncryptUtility.sc_decrypt(r);
//			// print and check r.content
			byte[] tempContent = r.content;
			log.info("when conn_type = local, mge_cmd = " + r.getMgeCmd());
			log.info("tempContent.length = " + tempContent.length);
			for(int i=0; i < tempContent.length; i++) {
				log.info("tempContent[" + i + "] = " +  (Integer.toHexString(0xFF & tempContent[i])).toUpperCase());
			}
			
			log.info("mgecmd==="+r.getMgeCmd()+"____Cmdtype========="+r.getCmdtype()+"______ConnType======="+r.getConnType());

			//direct message, usually this is management cmd
			if (r.getDataType() == UDPPacket.SC_DATA_MANAGEMENT) {
				int mgecmd, inputint;
				//log.info("magic=" + r.getMagic());
				mgecmd = r.getMgeCmd();

				inputint = r.getMgeInputint1();
				//log.info("this cmd is to me cmd " + r.getMgeCmd() + " input int:" + r.getMgeinputint());
				
				if(mgecmd == UDPPacket.SC_MGE_GATEWAY_ROLE) {
					
					if (inputint == 1) { // 我是网关，我属于谁
						log.info("Gateway " + r.getFromId() + " search current server");
						//assign a current server to handle.
						boolean haveassignedserver = false;
						String server = null;
						String gateway = r.getFromId();
//						server = UDPServer.gatewayserverMap.get(gateway);
						r.SwitchFromTo();
						
						
					    // 直接告诉nb device，你归master管
						//new gateway
						Entity e;
						log.info("new assign server for " + gateway + " last global server is:" + UDPServer.lastassignedserver);
						
						server = "master";
						UDPServer.lastassignedserver = server;
						// 由master接管nb设备
						e = UDPServer.serverMap.get(server);
						log.info("choose server = " + server);
						log.info("e.port = " + e.port);
						log.info("e.address = " + e.address);
						log.info("e.address::2222" + e.address.toString().replace("/", "").getBytes());
						r.setMgeRetint1(e.port);
						r.setMgeRetstr1(e.address.toString().replace("/", "").getBytes());
						//e.address
						UDPServer.gatewayserverMap.update(gateway, server);
						log.info("notify gateway server:" + e.address + ":" + e.port + " " + r.getMagic() + " encry:" 
								+ r.getEncry());
						
						
						//add time here, send server time to gateway for time sync
						r.setTime();									
						r.setCmdtype(UDPPacket.SC_CMD_RSP);
						// put r into Nb rsp queue
						UDPServer.NbResponses.putRequest(r);
						synchronized (UDPServer.NbResponses) {
							UDPServer.NbResponses.notifyAll();
						}
						
					}
				} 
			}
		}
	}
}
