package com.bluepower.utility;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.utility.GlobalVars;


public class SocketBigData {

	private static Log log = LogFactory.getLog(SocketBigData.class);
	
	public Integer	manualon = 0, manualoff = 0;
	public Integer	localon = 0, localoff = 0;
	public Integer	remoteon = 0, remoteoff = 0;
	public Integer	timmeron = 0, timmeroff = 0;
	
	// standbytotal time is used to desc total seconds saved, it
	// is calculated by record the time scope between laststandby 
	// time and the next turn on action. 

	public Integer	standbyoff = 0;
	public Long standbytotaltime = 0L, laststandby = 0L;

	// total power is used to record the total power of the device
	// temppower is upadted by every heart beat, and will be added
	// to totalpower when the lastupdatetime swifted to next day.
	 
	public Integer	totalpower = 0, temppower = 0;
	public Long totalontime = 0L; //onoff is 1
	public Long totalworkingtime = 0L; //elec is not 0
	public Long lastupdatetime = 0L;
	public Integer	highelec = 0, lowelec = 0;
	//totalontime: ++ if lastupdatetime--current less than 5 min, and current heartbeat is on
	//totalworkingtime: ++ if lastupdatetime--current less than 5 min, and current heartbeat's gonglv is large than 1
	//average elec is calculated by totalpower+temppower/totalworkingtime
	
	public Long lastremoteofftime = 0L;
	public Long remotesavedtime = 0L;
	//last remote off time record the remote off time
	//remotesavedtime accmulates the time between last remote off time and next on time
	
	public void initBigData() {

    	manualon = 0;
    	manualoff = 0;
    	
    	localon = 0;
    	localoff = 0;
    	
    	remoteon = 0;
    	remoteoff = 0;    	

    	timmeron = 0;
    	timmeroff = 0;
    	
    	standbyoff = 0;
    	standbytotaltime = 0L;
    	laststandby = 0L;

    	totalpower = 0;
    	temppower = 0;

    	totalontime = 0L;
    	lastupdatetime = (new java.sql.Date(System.currentTimeMillis())).getTime();
    	
    	lastremoteofftime = 0L;
    	remotesavedtime = 0L;
		
	}
	
	public int parseFromString(String src) {
		int success = GlobalVars.FAIL;
		
		if (src == null || src.length() < 10) {
			initBigData();
			return success;
		}
		
        Pattern  p = Pattern.compile("manual:([0-9]+)\\|([0-9]+)#local:([0-9]+)\\|([0-9]+)#"
        		+ "remote:([0-9]+)\\|([0-9]+)#timmer:([0-9]+)\\|([0-9]+)#"
        		+ "standby:([0-9]+)\\|([0-9]+)\\|([0-9]+)#"
        		+ "power:([0-9]+)\\|([0-9]+)\\|([0-9]+)\\|([0-9]+)\\|([0-9]+)\\|([0-9]+)\\|([0-9]+)\\|([0-9]+)\\|([0-9]+)"        		
        		);
       
        java.util.regex.Matcher m;// = p.matcher("manual:0|1#local:0|1#remote:0|1#timmer:0|1#standby:1|1427811|1427810957771" + "#power:1000|100|12341234|888|1427810957771|99|8"); 
        
        m = p.matcher(src);
        
        //log.info("match count:" + m.groupCount());
        
        if (m.groupCount() < 20) {
        	initBigData();
        	return success;
        }

    	
        if(m.find()) { 
        	
        	manualon = Integer.parseInt(m.group(1));
        	manualoff = Integer.parseInt(m.group(2));
        	
        	localon = Integer.parseInt(m.group(3));
        	localoff = Integer.parseInt(m.group(4));
        	
        	remoteon = Integer.parseInt(m.group(5));
        	remoteoff = Integer.parseInt(m.group(6));
        	

        	timmeron = Integer.parseInt(m.group(7));
        	timmeroff = Integer.parseInt(m.group(8));
        	
        	standbyoff = Integer.parseInt(m.group(9));
        	standbytotaltime = Long.parseLong(m.group(10));
        	laststandby = Long.parseLong(m.group(11));

        	totalpower = Integer.parseInt(m.group(12));
        	temppower = Integer.parseInt(m.group(13));

        	totalontime = Long.parseLong(m.group(14));
        	
        	totalworkingtime = Long.parseLong(m.group(15));
        	lastupdatetime = Long.parseLong(m.group(16));   
        	highelec = Integer.parseInt(m.group(17));
        	lowelec = Integer.parseInt(m.group(18));
        	

        	lastremoteofftime = Long.parseLong(m.group(19));
        	remotesavedtime = Long.parseLong(m.group(20));

            //java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());            
            //System. out.println(currentDate .getTime());
        } 
        
		return GlobalVars.SUCCESS;
	}
	
	@SuppressWarnings("deprecation")
	public int update(String cmd) {
		//{"bigdata":"state#elec-0|power-0|onoffstate-0"}}
		//"bigdata":"onoffevent#timmer-1[mnual,timmer,applocal,appremote,standby- 0,1]"
		//"bigdata":"configevent#network[network, boot]"
		int elec = -1, power = -1, onoff = -1;
		//long currenttime = (new java.sql.Date(System.currentTimeMillis())).getTime();
		String type = null;
		
		long currenttime = System.currentTimeMillis();
		java.sql.Date currentdate = new java.sql.Date(currenttime), lastdate;
		
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

	        log.info(m.groupCount());
	        
	        if (m.groupCount() < 3) {
	        	return GlobalVars.FAIL;
	        }
	        
	        if(m.find()) {
	        	elec = Integer.parseInt(m.group(1));
	        	power = Integer.parseInt(m.group(2));
	        	onoff = Integer.parseInt(m.group(3));
	        }

        	//calculate power saving for onoff state
        	if (laststandby != 0 && onoff == 1) {
        		if (System.currentTimeMillis() > laststandby) {
        			standbytotaltime += System.currentTimeMillis() - laststandby;	        			
        		}
        		laststandby = 0L;
        	}
        	
        	if (lastremoteofftime != 0 && onoff == 1) {
        		if (System.currentTimeMillis() > lastremoteofftime) {
        	        lastdate = new java.sql.Date(lastremoteofftime);
        	        
        	        if (lastdate.getDate() != currentdate.getDate()) 
        	        	remotesavedtime += 1000 * 3600 * 6;
        	        else
        	        	remotesavedtime += System.currentTimeMillis() - lastremoteofftime;        			
        		}
        		lastremoteofftime = 0L;
        	}
        
	        //temppower
	        //check whether today and lastupdate time is the same date, if yes only update temppower
	        //or else, add the temppower to the totalpower
	        if (lastupdatetime <= 0) {      	
	        	lastupdatetime = currenttime;
	        }
	        lastdate = new java.sql.Date(lastupdatetime);
        	
	        if (lastdate.getDate() != currentdate.getDate()) {
	        	totalpower = totalpower + temppower;
	        	temppower = 0;
	        } else {
	        	
	        	//don't update temppower at midnight
	        	if ((power >= temppower) && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 1)) {
	        		temppower = power;	        		
	        	}
	        	
	        }

	        //totalontime totalworktime
	        //update in 5 minutes, or else don't update
	        log.info("currenttime:" + currenttime + "    lastupdatetime" + lastupdatetime + " totalontime" + totalontime);
	        if ((currenttime > lastupdatetime)  && (currenttime - lastupdatetime < 300000) ) {
	        	if (onoff == 1)
	        		totalontime = totalontime + (currenttime - lastupdatetime);
	        	
	        	if (elec > 0)
	        		totalworkingtime = totalworkingtime + (currenttime - lastupdatetime);	        	
	        }

	        //update high low elec
	        if(elec > 0) {
	        	if (elec > highelec)
	        		highelec = elec;
	        	
	        	if (lowelec == 0)
	        		lowelec = elec;
	        	
	        	if (elec < lowelec)
	        		lowelec = elec;	        	
	        }
	        
	        lastupdatetime = currenttime;
	        //new java.sql.Date(System.currentTimeMillis());
			
		} else if(cmd.startsWith("onoffevent")) {
			//"bigdata":"onoffevent#timmer-1[mnual,timmer,applocal,appremote,standby- 0,1]"

			p = Pattern.compile("onoffevent#(.+)-([0-9]+)");
			m = p.matcher(cmd); 

	        log.info(m.groupCount());
	        
	        if (m.groupCount() < 2) {
	        	return GlobalVars.FAIL;
	        }
	        
	        if(m.find()) {
	        	type = m.group(1);
	        	onoff = Integer.parseInt(m.group(2));
	        	
	        	
	        	//calculate power saving for onoff events
	        	if (laststandby != 0 && onoff == 1) {
	        		if (System.currentTimeMillis() > laststandby) {
	        			standbytotaltime += System.currentTimeMillis() - laststandby;	        			
	        		}
	        		laststandby = 0L;
	        	}
	        	
	        	if (lastremoteofftime != 0 && onoff == 1) {
	        		if (System.currentTimeMillis() > lastremoteofftime) {

	        	        lastdate = new java.sql.Date(lastremoteofftime);
	        	        
	        	        if (lastdate.getDate() != currentdate.getDate()) 
	        	        	remotesavedtime += 1000 * 3600 * 6;
	        	        else
	        	        	remotesavedtime += System.currentTimeMillis() - lastremoteofftime;	        			
	        		}
	        		lastremoteofftime = 0L;
	        	}
	        	
	        	if (type.equals("manual") || type.equals("mnual")) {
	        		if (onoff == 1)
	        			manualon++;
	        		else
	        			manualoff++;
	        	} else if (type.equals("applocal")) {
	        		if (onoff == 1)
	        			localon++;
	        		else
	        			localoff++;
	        	} else if (type.equals("timmer")) {
	        		if (onoff == 1)
	        			timmeron++;
	        		else
	        			timmeroff++;
	        	} else if (type.equals("appremote")) {
	        		if (onoff == 1)
	        			remoteon++;
	        		else {
	        			remoteoff++;
	        			if (lastremoteofftime == 0) {
	        				lastremoteofftime = System.currentTimeMillis();
	        			}
	        		}
	        	} else if (type.equals("standby")) {
	        		if (onoff == 0) {
	        			standbyoff++;
	        			laststandby =  System.currentTimeMillis(); 
	        		}
	        	}
	        }	        

		} else if(cmd.startsWith("configevent")) {
			
		} else {			
			return GlobalVars.FAIL;
		}
		
		return GlobalVars.SUCCESS;
	}
	
	public String toString() {
		
		String ret = "";
		
		ret += "manual:" + manualon + "|" + manualoff + "#";
		ret += "local:" + localon + "|" + localoff + "#";
		ret += "remote:" + remoteon + "|" + remoteoff + "#";
		ret += "timmer:" + timmeron + "|" + timmeroff + "#";
		ret += "standby:" + standbyoff + "|" + standbytotaltime + "|" + laststandby + "#";
		ret += "power:" + totalpower + "|" + temppower + "|" + totalontime + "|" + totalworkingtime + "|" + lastupdatetime 
				+ "|" + highelec + "|" + lowelec + "|" + lastremoteofftime + "|" + remotesavedtime;
		
		
		System.out.println(
				"manual:" + manualon + "|" + manualoff + "#" +
				"local:" + localon + "|" + localoff + "#" +
				"remote:" + remoteon + "|" + remoteoff + "#" +
				"timmer:" + timmeron + "|" + timmeroff + "#" + 
				"standby:" + standbyoff + "|" + standbytotaltime + "|" + laststandby + "#" + 
				"power:total-" + totalpower + "|temp-" + temppower + "|ontime-" + totalontime/1000 + "|workingtime-" + totalworkingtime/1000 + "|lastupdate-" 
				+ lastupdatetime 
				+ "|high-" + highelec + "|low-" + lowelec
				 + "|lastremoteoff-" + lastremoteofftime + "|saved" + remotesavedtime
				);
		
		return ret;
	}

}
