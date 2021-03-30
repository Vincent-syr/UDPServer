package com.bluepower.request;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.threads.WorkerThread;

public class PacketList {

	private static Log log = LogFactory.getLog(PacketList.class);
	
	private List<UDPPacket> requests;
	
	private ReentrantLock  lock;
	
	public PacketList() {
		requests = new LinkedList<UDPPacket>();
		lock = new ReentrantLock();
	}
	
	
	public void putRequest(UDPPacket r) {
		lock.lock();
		requests.add(r);
		lock.unlock();
		log.info("after push, putRequest: requests size = " + requests.size());
	}
	
	
	/** 
	 * @Title: getRequest 
	 * @Description: remove first element of requests queue
	 * @return 
	 */
	public UDPPacket getRequest(){
		UDPPacket r = null;
		lock.lock();
		if (requests.size() > 0) {
			r = requests.remove(0);
		}
		lock.unlock();
		return r;
	}
}
