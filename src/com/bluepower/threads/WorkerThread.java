package com.bluepower.threads;

import javax.sound.midi.MidiDevice.Info;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.bluepower.command.UDPCommand;
import com.bluepower.entity.Entity;
import com.bluepower.request.UDPPacket;
import com.bluepower.udpserver.UDPServer;
import com.bluepower.utility.EncryptUtility;
import com.bluepower.utility.WinStatusSend;

public class WorkerThread implements Runnable {
	private static Log log = LogFactory.getLog(WorkerThread.class);
	private static String cmd_type[] = {"default", "req_no_rsp", "req_need_rsp", "rsp"}; //0 default, 1 req no rsp, 2 req need rsp, 3 rsp
	private static String encry_type[] = {"default", "encry", "no_encry"}; //0 default, 1 req no rsp, 2 req need rsp, 3 rsp
	private static String conn_type[] = {"disconnect", "local", "passthrough", "forward"};	// 0 disconnect, 1 local, 2 passthrough, 3 fowrard.
	private static String data_type[] = {"default", "manage_data", "user_data"};	// 0 disconnect, 1 local, 2 passthrough, 3 fowrard.
	//0 default, 1 manage data, 2 user data.
	
	UDPPacket r = null;          

	public void run() {
		UDPCommand cmd = null;
		Entity entity = null;
		
		while (true) {
			try {
				if((r= UDPServer.incomingRequests.getRequest()) != null) {
					synchronized(UDPServer.ipMap) {
						log.info("r is dequeued from incoming request by " + Thread.currentThread().getName());
						Integer times = 0;
											
						times = UDPServer.ipMap.get(r.address.toString());
						if (times == null || times == 0) {
							UDPServer.ipMap.put(r.address.toString(), 1);
						} else if (times < UDPServer.ipVisitThreshold) {
							times++;
							UDPServer.ipMap.put(r.address.toString(), times);
							if(times % 100 == 0)
								log.info("valid ip request:" + r.address + "|" + times + " ipMap size " + UDPServer.ipMap.size());
						} else { //ip address requesed too much sms
							UDPServer.blackMap.put(r.address.toString(), times);
							log.info("invalid ip request:" + r.address + "|" + times  + " blackMap size " + UDPServer.blackMap.size());
							continue;
						}
					}

					// deal with r
					log.info(Thread.currentThread().getName() + "deal with: " + r.getFromId() + "-->" + r.getToId() 
							+ "; isNB: " + r.getIsNB()
							+ "; cmd_type:" + cmd_type[r.getCmdtype()]  //0 default, 1 req no rsp, 2 req need rsp, 3 rsp
							+ "; encry:" + encry_type[r.getEncry()] 
							+ "; conn:" + conn_type[r.getConnType()]
							+ "; data:" + data_type[r.getDataType()]
							+ "; pktlen:" + r.getPktLen() 
							+ "; reallen:" + r.len 
							+ "; seq:" + r.getSeq()
							);
					
					// put the fromid into entityMap
					UDPServer.entityMap.update(r.getFromId(), r.getFromType(), r.getVersion() + "", r.address,
							r.port);
					
					// not NB case
					if (r.getIsNB() !='1') {
						log.info("not NB request");

						// conn_type = forward
						if (r.getConnType() == UDPPacket.SC_CNN_FOWRARD) {
//							log.info("conn_type = " + r.getConnType());
							//forward message, forward to entity
							entity = UDPServer.entityMap.get(r.getToId());
							if (entity != null) {
								r.address = entity.address;
								r.port = entity.port;
							} else {
								continue;
							}						
							
							//log.info("forward from " + r.getFromId() + " to " + r.getToId());
							// response is 发送队列
							UDPServer.responses.putRequest(r);
							synchronized (UDPServer.responses) {
								UDPServer.responses.notifyAll();
							}
							continue;
							
							
							// conn_type = local
						} else if (r.getConnType() == UDPPacket.SC_CNN_LOCAL) {
							//direct message, usually this is management cmd
							if (r.getDataType() == UDPPacket.SC_DATA_MANAGEMENT) {
								int mgecmd, inputint;
								
								// 对r进行解密
								EncryptUtility.sc_decrypt(r);
								//log.info("magic=" + r.getMagic());
								mgecmd = r.getMgeCmd();
								inputint = r.getMgeInputint1();
								log.info("MgeCmd = " + r.getMgeCmd() + " input int = " + r.getMgeInputint1());
								
								// cmd = serverRole
								if(mgecmd == UDPPacket.SC_MGE_SERVER_ROLE) {
									if (inputint == 1) {
										//log.info("server map ##############################################################################");
										UDPServer.serverMap.update(r.getFromId(), r.getFromType(), r.getVersion() + "", r.address,
												r.port);
										//log.info("server map ##############################################################################");
									}
								// cmd = gatewayRole
								}else if(mgecmd == UDPPacket.SC_MGE_GATEWAY_ROLE) {
									
									if (inputint == 1) { //我是网关 我属于谁
										log.info("Gateway " + r.getFromId() + " search current server");
										//assign a current server to handle.
										boolean haveassignedserver = false;
										String server = null;
										String gateway = r.getFromId();
										server = UDPServer.gatewayserverMap.get(gateway);
										
										r.SwitchFromTo();
										//find whether there is already assinged slaveserver.
										if ((server != null) && (server.length() > 2) && (!server.equals("master"))) {
											//have an assigned slaveserver
											log.info("old assign server for " + gateway + " last assigned server is:" + server);
											
											Entity e;
											e = UDPServer.serverMap.get(server);										
											if(e != null) {
												haveassignedserver = true;
												r.setMgeRetint1(e.port);
												r.setMgeRetstr1(e.address.toString().replace("/", "").getBytes());
												//e.address

												UDPServer.gatewayserverMap.update(gateway, server);
												
												//log.info("notify gateway server:" + e.address + ":" + e.port + " " + r.getMagic() + " encry:" 
												//		+ r.getEncry());
											} else {
												haveassignedserver = false;

												log.info("old assign server " + server + " not exist");
											}
											// the gateway has not been assigned any old slave server
										} else {
											haveassignedserver = false;
											log.info("didn't found old assign server besides master for gateway " + gateway);
										}
										// assign a server for the gateway which hasn't been assigned
										if (!haveassignedserver) {
											//new gateway
											String firstserver = null;
											Entity e;
											int nexthit = 0;
											
											log.info("new assign server for " + gateway + " last global server is:" + UDPServer.lastassignedserver);
											
											server = "master";
											
											for (String key : UDPServer.serverMap.entityMap.keySet()) {
												log.info("round " + key + " lastassignedserver:" + UDPServer.lastassignedserver + " firstserver:" + firstserver + " server:" + server);											
												if (key.equals("master"))
													continue;
												
												//寻找第一个不是master的firstserver
												if (firstserver == null) {
													firstserver = key;
													log.info("got firstserver " + key);
													
													//系统第一次分配的时候，分配一个firstserver就ok了
													if (UDPServer.lastassignedserver == null) {
														server = firstserver;
														UDPServer.lastassignedserver = firstserver;
														log.info("global first assign " + key);
														break;
													}
												}
												if (UDPServer.lastassignedserver.equals(key)) {
													nexthit = 1;
													log.info("ready to hit " + key);
													continue;
												}
												
												if (nexthit == 1) {
													server = key;
													UDPServer.lastassignedserver = server;
													log.info("hit " + key);
													break;
												}
												
											}
											
											if (server.equals("master") && firstserver != null) {
												server = firstserver;
												UDPServer.lastassignedserver = server;
											}
											
											log.info("choose server:" + server);

											e = UDPServer.serverMap.get(server);
																					
											if(e != null) {
												r.setMgeRetint1(e.port);
												r.setMgeRetstr1(e.address.toString().replace("/", "").getBytes());
												//e.address
												UDPServer.gatewayserverMap.update(gateway, server);
												log.info("notify gateway server:" + e.address + ":" + e.port + " " + r.getMagic() + " encry:" 
														+ r.getEncry());
											} else {
												r.setMgeRetint1(UDPPacket.SC_FAILED);
												log.info("gateway server not ready error");
											}
										}
										
										//add time here, send server time to gateway for time sync
										r.setTime();
										
								
										r.setCmdtype(UDPPacket.SC_CMD_RSP);
										UDPServer.responses.putRequest(r);
										synchronized (UDPServer.responses) {
											UDPServer.responses.notifyAll();
										}
										
									} else if (inputint == 2) { //我是手机  网关在哪里									
										String server = UDPServer.gatewayserverMap.get(r.getMgeInputstr1());
										r.SwitchFromTo();
										
										if (server == null || server.length() < 2) {
											log.info("didn't find server for " + r.getMgeInputstr1() + ", asked by mobile " + r.getToId());
											r.setMgeRetint1(UDPPacket.SC_FAILED);										
										} else {
											log.info("found server for " + r.getMgeInputstr1() + ", asked by mobile " + r.getToId());
											Entity e;
											e = UDPServer.serverMap.get(server);
											
											if(e != null) {
												r.setMgeRetint1(e.port);
												r.setMgeRetstr1(e.address.toString().replace("/", "").getBytes());
												//e.address
												log.info("notify gateway server:" + e.address + ":" + e.port + " " + r.getMagic() + " encry:" 
														+ r.getEncry());
												//UDPServer.gatewayserverMap.put(r.getFromId(), server);
											}
										}
										
										r.setCmdtype(UDPPacket.SC_CMD_RSP);
										UDPServer.responses.putRequest(r);
										synchronized (UDPServer.responses) {
											UDPServer.responses.notifyAll();
										}
										
									} else if (inputint == 3) { //我是网关  我来向你报道(报道：第一次向该server发送消息)
										r.SwitchFromTo();
										r.setCmdtype(UDPPacket.SC_CMD_RSP);
										r.setMgeRetint1(UDPPacket.SC_SUCCESS);
										UDPServer.responses.putRequest(r);
										synchronized (UDPServer.responses) {
											UDPServer.responses.notifyAll();
										}
									}
									
								} else if(mgecmd == UDPPacket.SC_MGE_GATEWAY_STATUS) {  // send window status package
									byte[] b = r.getMgeInputstr1Bytes();	
									String message =  "WorkerThread status: " + r.getFromId()  + 
											"; warning-" + b[0] + 
											"; cur_progress-" + b[1] + 
											"; cur_mode-" + b[2] +
											"; cur_speed-" + b[3] +
											"; des_progress-" + b[4] + 
											"; des_mode-" + b[5] + 
											"; des_speed-" + b[6] + 
											"; trigger-" + b[7] + 
											"; timepingkai-" + b[8] + 
											"; timeshangxuan-" + b[9] 
											;
									log.info(message);
									// send message
									WinStatusSend.send(message);
									//getMgeinputstr1bytes
									/*
									 *     m_pkt->inputstr1[0] = (raining) & 0xff;;                                                             
	    m_pkt->inputstr1[1] = (cur_progress) & 0xff;;                                                        
	    m_pkt->inputstr1[2] = (cur_mode) & 0xff;;                                                            
	    m_pkt->inputstr1[3] = (cur_speed) & 0xff;;                                                           
	    m_pkt->inputstr1[4] = (des_progress) & 0xff;;                                                        
	    m_pkt->inputstr1[5] = (des_mode) & 0xff;;                                                            
	    m_pkt->inputstr1[6] = (des_speed) & 0xff;;
	    m_pkt->inputstr1[7] = (trigger) & 0xff;;                                                             
	    m_pkt->inputstr1[8] = (time_pingkai) & 0xff;;                                                        
	    m_pkt->inputstr1[9] = (time_shangxuan) & 0xff;; 
									 * 
									 * 
									 */
									
								}
							}
							
						}
					} 
					
					
					// nb case
					else if(r.getIsNB() =='1'){
						log.info("it is NB Request");
						NBReqProcessor.NBReqProcess(r);
					}
				}
				else {
					// waiting for notification
					//log.info(Thread.currentThread().getName() + " idle");
					
					// 如果该worker没抢到packet的情况，则休息60s，60s后再去
					try {
//						 log.info(Thread.currentThread().getName() + " waiting for requests...");
						synchronized (UDPServer.incomingRequests) {
							UDPServer.incomingRequests.wait(60 * 1000);
//							log.info(Thread.currentThread().getName() + " was notified");
//							Object object = new Object();
//							object.n
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.info("synchronized incomingRequests exception: " + e.getMessage());
					}
				}         
			} catch (Exception e) {
				log.info("Worker run exception: " + e.getMessage());
				
				for (int k = 0; k < e.getStackTrace().length; k++) {
					log.info(e.getStackTrace()[k].toString());
				}
			}

		}
	}

}