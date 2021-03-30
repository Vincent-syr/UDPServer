package com.bluepower.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Analysts {


	private static Log log = LogFactory.getLog(Analysts.class);
	
	//spliter |||#||||||#|||||
	
	
	//0~200W   20w间隔   10个
	//200~1000W  50w间隔  16个
	//1000w~2000W 100W间隔  10个
	//2000W以上    一个
	//10-0|20-10|140-115|200-78
	
	public HashMap<Integer, Integer> elecDist = null;	
	/*
		ArrayList<Integer> keyList = new ArrayList<Integer>();
		keyList.addAll(elecDist.keySet());
		Collections.sort(keyList); 
	 */
	
	public int[] timeDist = null;
	//9999999|9999999|0|23|.....	
	
	public ArrayList<Integer> elecWave = null;
	//0 1:  elec times; 2 3: elec times; 4 5: elec times;
	//134-18|5-10|138-52
	//功率聚合
	//10w一下自动聚合，10w到100w以上以开始的功率为准，上下浮动10w为误差，100w到500w上下浮动20w为误差，
	//500w以上，上下浮动50为误差
	
	public long lastupdate = 0L;

	public Analysts(){
		elecDist = new HashMap<Integer, Integer>();
		timeDist = new int[24];
		elecWave = new ArrayList<Integer>();
		lastupdate = System.currentTimeMillis();
	}

	public int parseFromString(String src) {
		int success = GlobalVars.FAIL;
		String[] sections = null, items = null;
		
		if (src == null || src.length() < 10) {
			log.info("parseFromString failed length: " + src);
			return success;
		}
		
		sections = src.split("#");
		
		if(sections == null || sections.length < 4) {
			log.info("parseFromString failed: sections" + src);
			return success;
		}
	
		//section 0
		if (sections[0] != null && sections[0].length() < 2) {
			log.info("parseFromString failed: sections[0] elecDist " + src);
			return success;
		} else {
			items = sections[0].split("\\|");
			if (items == null || items.length < 1) {
				log.info("parseFromString failed: sections[0] elecDist fields " + items);
				return success;
			} else {
				String[] fields = null;				
				for (int i = 0; i < items.length; i++) {
					fields = items[i].split("-");
					if (fields == null || fields.length < 2 || fields[0] == null || fields[1] == null) {
						log.info("parseFromString failed: sections[0] elecDist items[" + i 
								+ "] " + items[i]);
						return success;
					}					
					elecDist.put(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
					fields = null;
				}				
			}			
		}		

		//section 1
		if (sections[1] != null && sections[1].length() < 24) {
			log.info("parseFromString failed: sections[1] timeDist " + src);
			return success;
		} else {
			items = null;
			
			items = sections[1].split("\\|");
			if (items == null || items.length != 24) {
				log.info("parseFromString failed: sections[1] timeDist items " + src);
				return success;
			} else {
				for (int i = 0; i < items.length; i++) {
					if(items[i] == null) {
						log.info("parseFromString failed: sections[1] timeDist items[" 
								+ i +"] " + items[i]);
						return success;
					}
					timeDist[i] = Integer.parseInt(items[i]);
				}
			}
		}
		
		//section 2
		if (sections[2] != null && sections[2].length() < 2) {
			log.info("parseFromString failed: sections[2] elecWave " + src);
			return success;
		} else {
			log.info(sections[2]);
			items = sections[2].split("\\|");
			log.info(items.length);
			if (items == null || items.length < 1) {
				log.info("parseFromString failed: sections[2] elecWave fields " + items);
				return success;
			} else {
				String[] fields = null;				 
				for (int i = 0; i < items.length; i++) {
					
					fields = items[i].split("-");
					if (fields == null || fields.length < 2 || fields[0] == null || fields[1] == null) {
						log.info("parseFromString failed: sections[2] elecWave items[" + i 
								+ "] " + items[i]);
						return success;
					}
					elecWave.add(Integer.parseInt(fields[0]));
					elecWave.add(Integer.parseInt(fields[1]));
					fields = null;
				}				
			}			
		}		

		//section 3
		if (sections[3] != null && sections[3].length() < 2) {
			log.info("parseFromString failed: sections[3] lastupdate " + src);
			return success;
		} else {
			lastupdate = Long.parseLong(sections[3]);
		}
		
		return GlobalVars.SUCCESS;
	}
	
	

	public int update(String cmd) {
		//{"bigdata":"state#elec-0|power-0|onoffstate-0"}}
		int elec = -1, power = -1, onoff = -1;
		
		long currenttime = System.currentTimeMillis();
		
		Pattern  p;
        java.util.regex.Matcher m;

        if (cmd == null || cmd.length() <= 1) {
        	log.info("update: cmd=" + cmd);
        	return GlobalVars.FAIL; 
        }
        
		if(cmd.startsWith("state")) {
			
			p = Pattern.compile("state#elec-([0-9]+)\\|power-([0-9]+)\\|onoffstate-([0-9]+)");
			//p = Pattern.compile(cmd);
			m = p.matcher(cmd); 
	        
	        if (m.groupCount() < 3) {
	        	return GlobalVars.FAIL;
	        }
	        
	        if(m.find()) {
	        	elec = Integer.parseInt(m.group(1));
	        	power = Integer.parseInt(m.group(2));
	        	onoff = Integer.parseInt(m.group(3));
	        }
	        
	        if(onoff == 1) {
	        	//elec dist
	        	
	        	int key = 0, value = 0;
	        	if (elec > 0 && elec <= 10) {
	        		key = 10;
	        	} else if (elec <= 200) {
	        		key = ((elec + 19)/20) * 20;
	        	} else if (elec <= 1000) {
	        		key = ((elec + 49)/50) * 50;
	        	} else if (elec <= 2000) {
	        		key = ((elec + 99)/100) * 100;
	        	} else {
	        		key = 2001;
	        	} 
	        	
	        	if(elecDist.get(key) != null) {
	        		elecDist.put(key, elecDist.get(key) + 1);
	        	} else {
	        		elecDist.put(key, 1);
	        	}
	        	
	        	//timeDist
	        	if(elec > 0) {
	        		timeDist[Calendar.getInstance().get(Calendar.HOUR_OF_DAY)]++;
	        	}
	        	
	        	//elecWave
	        	if((elecWave.size() % 2) != 0) {
	        		log.info(" update " + elecWave.size());
	        	}
	        	
	        	if(elecWave.size() == 0) {
	        		elecWave.add(elec);
	        		elecWave.add(1);
	        	} else if(elecWave.size() >= 2 ) {
	        		//check last value
	        		int amplitude = 10;
	        		
	        		if (elec > 100 && elec <= 500)
	        			amplitude = 20;
	        		
	        		if (elec > 500)
	        			amplitude = 50;
	        		
	        		int index = elecWave.size() - 2;
	        		
	        		if(Math.abs(elecWave.get(index) - elec) > amplitude 
	        				|| ((System.currentTimeMillis() - lastupdate) > 300 * 1000) ) {
	        			elecWave.add(elec);
	        			elecWave.add(1);
	        		} else {
	        			elecWave.set(index + 1, elecWave.get(index + 1) + 1);
	        		}
	        		
	        		//if last key is not the same and the last value is 1, check the previous value
	        	}
	        	
	        	if (elecWave.size() > 20) {
	        		elecWave.remove(1);
	        		elecWave.remove(0);
	        	}
	        	
		        lastupdate = System.currentTimeMillis();
	        }
	        
		}
		log.info("guess is: " + guess());
		
		return GlobalVars.SUCCESS;
	}

	public String toString() {
		String ret = "";
		
		//elecDist
		if (elecDist == null || elecDist.keySet().size() == 0) {
			ret += "10-0";
		} else {
			ArrayList<Integer> keyList = new ArrayList<Integer>();
			keyList.addAll(elecDist.keySet());
			Collections.sort(keyList);
			
			for (int i = 0; i < keyList.size() - 1; i++) {
				ret += keyList.get(i) + "-" + elecDist.get(keyList.get(i)) + "|";
			}	
			
			ret += keyList.get(keyList.size() - 1) + "-" + elecDist.get(keyList.get(keyList.size() - 1));
		}		
		ret +="#";
		
		//timeDist
		for (int i = 0; i < 23; i++) {
			ret += timeDist[i] + "|";
		}
		ret += timeDist[23];		
		ret +="#";
		
		
		//timeWave
		if (elecWave == null || elecWave.size() == 0) {
			ret += "0-0";
		}else if (elecWave.size() >= 2) {
			for (int i = 0; i < (elecWave.size()/2 - 1); i++) {			
				ret += elecWave.get(i * 2) + "-" + elecWave.get(i * 2 + 1) + "|";  
			}
			ret += elecWave.get(elecWave.size() - 2) + "-" + elecWave.get(elecWave.size() - 1);
		}
		ret +="#";
		
		ret += lastupdate;
		
		return ret;
	}
	
	public String guess() {
		String ret = "未知";
		int index = 0, latestelec = 0;

		index = elecWave.size() - 2;

		while(index >= 2) {
			latestelec = elecWave.get(index);
			if(latestelec > 2)
				break;
			else
				index = index - 2;
		}
		
		if (latestelec == 0) {
			ret = "未知";
		} else if (latestelec >= 3 && latestelec <= 45) {
			ret = "台灯";
		} else if (latestelec > 45 && latestelec < 70) { 
			ret = "饮水机";
		} else if (latestelec >= 70 && latestelec <= 130) {
			ret = "电视";
		} else if (latestelec >= 200 && latestelec < 300) {
			ret = "大屏显示器";
		} else if (latestelec >= 300 && latestelec <= 600) {
			ret = "饮水机";
		} else if (latestelec >= 1300 && latestelec <= 2200) {
			ret = "热水器";        
		}
		
		log.info("ret = " + latestelec + "|" + ret);
		return ret;
	}
	
}
