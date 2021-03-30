package com.bluepower.udpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.json.JSONException;
import org.json.JSONObject;


import com.bluepower.entity.EntityMap;
import com.bluepower.entity.GatewayServerMap;
import com.bluepower.request.PacketList;
import com.bluepower.request.UDPPacket;
import com.bluepower.threads.NBSendThread;
import com.bluepower.threads.SendThread;
import com.bluepower.threads.ServerClusterThread;
import com.bluepower.threads.WeatherLocationThread;
import com.bluepower.threads.WorkerThread;
import com.bluepower.utility.MysqlHelper;

public class UDPServer {
	public static DatagramSocket ds = null;
	// request and response list
	public static PacketList incomingRequests;  // req queue
	public static PacketList responses;  // rsp queue
	public static PacketList NbResponses;   // NB rsp queue
	public static EntityMap entityMap;   // all entity 的记录
	public static EntityMap serverMap;   // all server 的map记录
	public static GatewayServerMap gatewayserverMap;    // record 每个gateway被分配给了哪个server
	private static Log log = null;
	
	
	public static Timestamp snapTime = new Timestamp(System.currentTimeMillis());	
	public static Map<String, Integer> ipMap = new HashMap<String, Integer>();   // 某个ip请求的次数
	public static Map<String, Integer> blackMap = new HashMap<String, Integer>();
	
	public static int ipRegreshInterval = 3600;
	public static int ipVisitThreshold = 200000;
	
	public static String myname = null;
	public static int ismaster = 0;
	public static String masterip = "182.92.148.166";
	public static int masterport = 20163;
	
	public static String lastassignedserver = null;

	public UDPServer() {
		try {
			ds = new DatagramSocket(20163);
			ds.setReceiveBufferSize(1024 * 1024 * 8);
			log.info("ds buf size = " + ds.getReceiveBufferSize());
			incomingRequests = new PacketList();
			responses = new PacketList();
			NbResponses = new PacketList();
			entityMap = new EntityMap();
			
			if (ismaster == 1) {
				serverMap = new EntityMap();
				gatewayserverMap = new GatewayServerMap();
				// add entity master to serverMap
				serverMap.update("master", 3, 1+"", InetAddress.getByName("localhost"), masterport);
			}
			
		} catch (Exception e) {
			log.error(e);
		}
	}

	@SuppressWarnings("deprecation")
	public void startServer() {
		byte[] recvbuffer = new byte[1024 * 64];
		int i = 0;
		DatagramPacket recvdp = new DatagramPacket(recvbuffer, 1024 * 64);
		UDPPacket request;
		// workerThread, 处理包
		new Thread(new WorkerThread(), "worker1").start();
		new Thread(new WorkerThread(), "worker2").start();
		new Thread(new WorkerThread(), "worker3").start();
		new Thread(new WorkerThread(), "worker4").start();
		new Thread(new WorkerThread(), "worker5").start();
		new Thread(new WorkerThread(), "worker6").start();
		new Thread(new WorkerThread(), "worker7").start();
		new Thread(new WorkerThread(), "worker8").start();
//		new Thread(new NBWorkerThread(), "workder9").start();
		
		// sendThread: 发送包
		new Thread(new SendThread(), "sender").start();
		new Thread(new NBSendThread(), "NB_sender").start();
		// master 和slave通信的线程
		new Thread(new ServerClusterThread(), "sender").start();
		
		while (true)
			try {
				i++;
				ds.receive(recvdp);
				log.info("ds receive from: " + recvdp.getSocketAddress());
				
				// check bytes[] from recvdp
//				byte[] temp = recvdp.getData();
				// 原始byte数组的截取0-340部分
				byte[] recvbytes = Arrays.copyOfRange(recvdp.getData(), 0, 340);
				log.info("recvbytes.length = " + recvbytes.length);
//				log.info("the req byte before decrypt below");
//				for(int j=0; j<recvbytes.length; j++) {
//					log.info("recvbytes[" + j + "] = 0x" + (Integer.toHexString(0xFF & recvbytes[j])).toUpperCase());
//				}
//
				request = new UDPPacket(recvdp.getAddress(), recvdp.getPort(),
						recvdp.getData(), recvdp.getLength());
								
				log.info("request.getIsNB() = " + request.getIsNB());
				
				// over 延迟时间
				if (new Timestamp(System.currentTimeMillis()).getTime() - UDPServer.snapTime.getTime() > ipRegreshInterval * 1000) {			
					UDPServer.ipMap.clear();
					UDPServer.blackMap.clear();
					UDPServer.snapTime = new Timestamp(System.currentTimeMillis());
				}
				
				 // rsq too frequent , put the ip to blackmail
				if (blackMap.get(request.address.toString()) != null) {
					continue;
				}
				
				// 把request放到队列 
				incomingRequests.putRequest(request);
				log.info("req was just pushed to incomingResquest queue");
				synchronized (incomingRequests) {
					// notify all worker thread, all the 8 worker would 抢队首的packet，worker处理
					incomingRequests.notifyAll();
//					log.info("notify all incomingReq lock");
				}
			} catch (Exception e) {
				log.info(e.getMessage());
			}
	}
	
	/** 
	 * @Title: main 
	 * @Description: 	// cmd 启动server时输入的参数： args，slave和master都是这么启动的
		slave start 例子：其中newslave是slave的名字,test.chi^^.cn 是master的url，
		20163是master的端口，通过域名访问master
		java -jar WindowUDPServer20180524.jar newslave test.chinasourcecode.cn 20163
		master start exaple: args[] 只有master
		java -jar WindowUDPServer20180524.jar master

	 * @param args 
	 */
	public static void main(String args[]){
		try {
			log = LogFactory.getLog(UDPServer.class);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		log.info("new xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

		
		// print args 
		for (int i = 0; i < args.length; i++) {
			log.info("args[i" + "] = " + args[i]);
		}
		
		//check master slave role.
		if (args.length >= 1) {
			myname = args[0];
			// role is master
			if (args[0].toLowerCase().equals("master"))
				ismaster = 1;
			else {    // role is slave
				ismaster = 0;
				if (args.length >= 3) {
					masterip = args[1];   //  test.chinasourcecode.cn 
					masterport = Integer.parseInt(args[2]);	// 20163				
				} else {
					log.error("No ip port for slave role.");
					return;
				}
			}			
		} else {
			log.error("No master/slave role input.");
			return ;
		}
		
		log.info("master = " + ismaster);
		
		UDPServer server = new UDPServer();
		
//		// mysql intial
//		MysqlHelper.init();
		log.info("master = " + ismaster);
		
		// target: master server获取每一个云屏的城市，并将城市信息存储到mysql
		// 每个云屏都连master，server拿云屏公网地址，通过公网地址获取城市回填到数据库
		if (ismaster == 1) {
			new Thread(new WeatherLocationThread(), "weatherlocation").start();
		}				
		server.startServer();
	}
}
