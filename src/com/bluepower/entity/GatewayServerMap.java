package com.bluepower.entity;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.threads.WorkerThread;

public class GatewayServerMap {
	private static Log log = LogFactory.getLog(GatewayServerMap.class);
	public Map<String, String> map;
	private ReentrantReadWriteLock lock;
	
	private static long updatetimes = 0;

	public GatewayServerMap() {
		lock = new ReentrantReadWriteLock();
		map = new HashMap<String, String>();
	}

	public String get(String gateway) {
		String e = null;
		lock.readLock().lock();
		e = map.get(gateway);
		lock.readLock().unlock();
		return e;
	}

	public void put(String gateway, String server) {
		lock.writeLock().lock();
		map.put(gateway, server);
		lock.writeLock().unlock();
	}

	// update lastping time if already exist
	// create entity if not
	public void update(String gateway, String server) {
		String e = null;
		boolean changed = false;
		lock.writeLock().lock();

		updatetimes++;

		e = map.get(gateway);
		if (e == null) {
			changed = true;
		} 

		map.put(gateway, server);

		if ( changed || ((updatetimes % 200) == 100) ) {
			log.info("********************************************************");
			for (String key : map.keySet()) {
				log.info(key + "|" + map.get(key));
			}
			log.info("*******************************************************#");
			//log.info("|##|" + e.entityId + e.address + ":" + e.port + "\t" + e.lastPingTime);
		}

		lock.writeLock().unlock();
	}
}
