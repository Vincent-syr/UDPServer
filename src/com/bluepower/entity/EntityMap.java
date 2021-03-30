package com.bluepower.entity;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.threads.WeatherLocationThread;
import com.bluepower.threads.WorkerThread;

/** 
 * @ClassName: EntityMap 
 * @Description: <String, Entity>, 	其中String是id，entity是实体，包括手机、gateway，server
 * @author: Vincent Sean
 * @date: 2018年6月29日 上午10:20:48  
 */
public class EntityMap {
	private static Log log = LogFactory.getLog(EntityMap.class);
	public Map<String, Entity> entityMap;
	private ReentrantReadWriteLock lock;
	public long printTime = 0;
	
	public EntityMap() {
		lock = new ReentrantReadWriteLock();
		entityMap = new HashMap<String, Entity>();
	}

	public Entity get(String id) {
		Entity e = null;
		lock.readLock().lock();
		e = entityMap.get(id);
		lock.readLock().unlock();
		return e;
	}

	public void put(String id, Entity entity) {
		lock.writeLock().lock();
		entityMap.put(id, entity);
		lock.writeLock().unlock();
	}

	// update lastping time if already exist
	// create entity if not
	/** 
	 * @Title: update 
	 * @Description: update ip, port and lastping time if already exist, or create entity if the id not exist
	 * @param id
	 * @param type
	 * @param version
	 * @param address
	 * @param port 
	 */
	public void update(String id, int type, String version, InetAddress address, int port) {
		Entity e = null;
		boolean changed = false;
		lock.writeLock().lock();
		
		e = entityMap.get(id);
		
		if (e == null) {
			e = new Entity(type, id, version, address, port);
			entityMap.put(id, e);
			changed = true;
		} else {
			if (e.update(type, id, version, address, port) == 1)
				changed = true;
		}
		
		//send to weather location thread to update
		if ( (changed || e.location == null) && id.length() >= 12) {
			WeatherLocationThread.addDeviceip(id, address.toString().replace("/", ""));
		}

		
		if (true) {
			if (changed) {
				log.info("|##|" + e.entityId + e.address + ":" + e.port + "\t" + e.lastPingTime);
			}
			
						
			if ((System.currentTimeMillis() - printTime) > 600 * 1000) {
				printTime = System.currentTimeMillis();
				log.info("********************************************************");
				for (String key : entityMap.keySet()) {
					log.info(key + entityMap.get(key).address + ":"
							+ entityMap.get(key).port + "\t"
							+ entityMap.get(key).lastPingTime);
				}
				log.info("*******************************************************#");
			}
			//log.info("|##|" + e.entityId + e.address + ":" + e.port + "\t" + e.lastPingTime);
		}

		lock.writeLock().unlock();
	}
	
	public void updateLocation(String id, String location) {
		Entity e = null;
		lock.writeLock().lock();

		e = entityMap.get(id);
		
		if (e == null) {
			return;
		} else {
			e.updateLocation(location);
		}
		
		lock.writeLock().unlock();
	}

}
