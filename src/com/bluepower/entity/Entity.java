package com.bluepower.entity;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;

/** 
 * @ClassName: Entity 
 * @Description: entity description, include id, version, and many attribution 
 * @author: Vincent Sean
 * @date: 2018年6月26日 下午4:08:51  
 */
public class Entity {
	public int type;	// any 0, server 1, mobile 2, socket 3, remote control 4, camera 5
	public String entityId;     // phone number, gateway mac, master name, new slave name, 
	public String entityVersion;
	public String location;  // city
	public InetAddress address; // remote
	public int port;
	public Timestamp lastPingTime;
	public int members;	// ignore
	
	public Entity(int type, String id, String version, InetAddress address, int port) {
		this.type = type;
		this.entityId = id;
		this.entityVersion = version;
		this.address = address;
		this.port = port;
		this.location = null;
		this.lastPingTime = new Timestamp(System.currentTimeMillis());
	}

	public void updateLocation(String location) {
		this.location = location;
	}
	
	/** 
	 * @Title: update 
	 * @Description: update entity的成员变量。server存储很多entity变量，
	 * workder每收到一个包，就会update，更新entity。
	 * @param type
	 * @param id
	 * @param version
	 * @param address
	 * @param port
	 * @return 
	 */
	public int update(int type, String id, String version, InetAddress address, int port) {
		int ret = 0;
		this.type = type;
		this.entityId = id;
		this.entityVersion = version;
		// 不一样时update。
		if (!this.address.toString().equals(address.toString())) {
			this.address = address;
			ret = 1;
		}
		if (this.port != port) {
			this.port = port;
			//ret = 1;
		}
		
		this.lastPingTime = new Timestamp(System.currentTimeMillis());
		return ret;
	}
}
