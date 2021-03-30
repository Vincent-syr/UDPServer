package com.bluepower.threads;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import com.bluepower.request.UDPPacket;
import com.bluepower.udpserver.UDPServer;
import com.bluepower.utility.HttpUtils;
import com.bluepower.utility.MysqlHelper;

public class WeatherLocationThread implements Runnable {
	private static Log log = LogFactory.getLog(WeatherLocationThread.class);

	private static LinkedList<String> deviceips = new LinkedList<String>();
	private static ReentrantLock  lock = new ReentrantLock();
	
	UDPPacket r = null;
	
	public static String getDeviceip(){
		String ret = null;
		lock.lock();
		if (deviceips.size() > 0) {
			ret = deviceips.removeFirst();
		}
		lock.unlock();
		return ret;
	}
	   
	public static void addDeviceip(String device, String ip) {
		
		if ((UDPServer.ismaster != 1) || device == null || ip == null || device.length() < 5 || ip.length() < 5) {
			return;
		}

		lock.lock();
		if ( (deviceips.size() == 0) || (!deviceips.getLast().equals(device + "-" + ip)) )
			deviceips.addLast(device + "-" + ip);
		lock.unlock();


		log.info("addDeviceip " + ip);
		synchronized (deviceips) {
			deviceips.notifyAll();
		}
		
	}

	public void run() {
		String di = null;
		String device = null;
		String ip = null;
		String[] temp;
		
		BasicHttpParams httpParams = new BasicHttpParams();  
        HttpConnectionParams.setConnectionTimeout(httpParams, 1200);  
        HttpConnectionParams.setSoTimeout(httpParams, 1200);  
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);  
		
		
		while (true) {

			di = null;
			di = getDeviceip(); 
			
			//sleep 500 ms for every operation.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				log.info("WeatherLocationThread e1:" + e1.getMessage());
			}
			
			log.info("get di " + di);
			
			if (di != null) {
				temp = di.split("-");
				if (temp.length < 2)
					continue;
				device = temp[0];
				ip = temp[1];
				
				if (device == null || ip == null || device.length() < 5 || ip.length() < 5)
					continue;
				
				//get http response
				//String url = "http://apis.juhe.cn/ip/ip2addr?ip=" + ip + "&key=34644f41aaa4d9ac56be89119db6d8c8";
				/* JUHE source
				String url = "http://apis.juhe.cn/ip/ip2addr?ip=" + ip + "&key=dcf1d353e9fec084b32ba061ec8be9c5"; //2018.2.22申请juhe数据，每天100次
				String result = "";

				HttpGet request = new HttpGet(url);

				try {
					HttpResponse response = httpclient.execute(request);

					if (response.getStatusLine().getStatusCode() == 200) {
						result = EntityUtils.toString(response.getEntity());
					}

					log.info("WeatherLocationThread httpget:" + result);
				} catch (Exception e) {
					log.info("WeatherLocationThread e:" + e.getMessage());
					continue;
				}
				*/
				///replace
				
				
				///end of replace
				String host = "https://dm-81.data.aliyun.com";
			    String path = "/rest/160601/ip/getIpInfo.json";
			    String method = "GET";
			    String result = "";
			    Map<String, String> headers = new HashMap<String, String>();
			    headers.put("Authorization", "APPCODE " + "9da6b29f84364db9937e3b424ec15670");
			    Map<String, String> querys = new HashMap<String, String>();
			    querys.put("ip", ip);

			    try {
			    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
			    	result = (EntityUtils.toString(response.getEntity()));
			    	log.info("WeatherLocationThread httpget:" + result);
			    } catch (Exception e) {
			    	log.info("WeatherLocationThread e:" + e.getMessage());
					continue;
			    }
			    
			    if ((result == null) || (result.length() < 10)) {
			    	log.info("bad result " + result);
			    	continue;
			    }
				
				//parse city name
				try {
					JSONObject rootObj = new JSONObject(result);
					JSONObject temp1 = null;
					String city;
					String location2 = null;
					String updatedLocation2 = null;
					
					if (rootObj != null) {
						int error_code = rootObj.getInt("code");
						
						if (error_code != 0) {
							log.info("errorcode " + error_code);
							continue;
						}
						
						temp1 = rootObj.getJSONObject("data");
						
						if (temp1 != null) {
							city = temp1.getString("city");
							log.info("city " + city);
							
							//create gateway in database when null
							if (MysqlHelper.getStringField("device", device) == null) {
								log.info("device " + device + " does not exist in database create it!");
								MysqlHelper.createGateway(device);
								
							}
							
							//store to database
							MysqlHelper.setStringField("location", city, device);
							
							//update to map
							UDPServer.entityMap.updateLocation(device, city);
							
							
							//resolve location2
							location2 = MysqlHelper.getStringField("location2", device);
							
							if (location2 == null || location2.length() < 2) {
								// location2 为空; set it
								updatedLocation2 = getCityFromLocation(city);
								
								if(updatedLocation2 != null && updatedLocation2.length() > 1){
									
									MysqlHelper.setStringField("location2", updatedLocation2, device);
									
								}
							} else if (city.contains(location2)) {
								// location2 是 新city的一部分  不需要动作
								
							} else {
								// set(change)
								updatedLocation2 = getCityFromLocation(city);
								
								if(updatedLocation2 != null && updatedLocation2.length() > 1) {
								
									MysqlHelper.setStringField("location2", updatedLocation2, device);
									
								}
							}
	
						}
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					log.info("WeatherLocationThread JSONException " + e.getMessage());
				}
				
				
			} else {
				try {
					// log.info("waiting for requests...");
					synchronized (deviceips) {
						deviceips.wait(120 * 1000);
						log.info("WeatherLocationThread alive.");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info("synchronized incomingRequests exception: " + e.getMessage());
				}
			}// end of if (di != null)
			
		}//end of while (true)
	}
	
	private static String getCityFromLocation(String location) {
		String city = null;
		int provinceLocation = -1;
		String[] temp = null;
		
		
		
		if (location == null || location.length() < 2)
			return null;
		
		city = location;
		//去掉省
		provinceLocation = location.indexOf("省");
		//System.out.println(provinceLocation);
		if (provinceLocation >= 0)
			city = location.substring(provinceLocation + 1, location.length());
		
		//deal with 广西贵港市, replace ...
		
		
		
		//deal with 市
		temp = null;
		if (city != null) {
			temp = city.split("市");
		
			//System.out.println("市 length " + temp.length);
			
			if (temp.length == 1) {
				//only one city
			} else if (temp.length == 2) {
				if (temp[1].contains("县") || city.endsWith("市")) {
					//江苏省苏州市昆山市
					//河南省商丘市夏邑县
					
					if (city.endsWith("市")) {
						city = temp[1] + "市";
					} else
						city = temp[1];
					
				} else
					city = temp[0] + "市";
			} else {
				city = temp[1] + "市";
			}
		}
		//System.out.println(location + "---------------------------- " + city);
		return city;
	}

}