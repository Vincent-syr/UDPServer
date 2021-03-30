package com.bluepower.command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.threads.SendThread;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONCommandHelper {
	private static Log log = LogFactory.getLog(JSONCommandHelper.class);

	public static UDPCommand  JSONToObject(String json) {
		//String json = "{'cmd':2,'cn':{'name':'zmh'},'ct':2,'fr':'13718811156','ft':2,'fv':'v1','md5':'mdddddddd','rt':2,'sq':'123456789','to':'server','tt':1,'tv':'s1','vs':'1'}";
		UDPCommand cmd = null;
		JSONObject jo = null, jo_subcmd = null;

		if (json == null || json.equals("")) {
			log.info("JSONToObject: null input.");
			return null;
		}
		
		jo = JSONObject.fromObject(json);
		try {			
			if (checkClassFields(jo, UDPCommand.class)) {
				cmd = new UDPCommand(
						jo.getString("vs"),
						jo.getString("fr"), jo.getInt("ft"), jo.getString("fv"),
						jo.getString("to"), jo.getInt("tt"), jo.getString("tv"),
						jo.getInt("ct"), jo.getInt("rt"), jo.getString("sq"), jo.getString("md5"),
						jo.getInt("cmd"), null						
						);				
			} else {
				log.info(json);
				return null;
			}

			//only parse head for forward connection
			if (cmd.ct == UDPConst.FORWARD_CONNECTION)
				return cmd;
			
			jo_subcmd = JSONObject.fromObject(jo.get("cn"));
			//log.info("JSONToObject subcommand " + cmd.getCmd());
			switch (cmd.getCmd()) {
			case KeepAliveReqCmd.subCommandType :	
				if(checkClassFields(jo_subcmd, KeepAliveReqCmd.class)) {
					if(jo_subcmd.containsKey("bigdata"))
						cmd.cn = new KeepAliveReqCmd(jo_subcmd.getString("bigdata"));
					else
						cmd.cn = new KeepAliveReqCmd(null);
				}
				break;
			case KeepAliveRspCmd.subCommandType :
				if(checkClassFields(jo_subcmd, KeepAliveRspCmd.class))
					cmd.cn = new KeepAliveRspCmd();
				break;
			case DeviceSocketInfoReqCmd.subCommandType :
				if(checkClassFields(jo_subcmd, DeviceSocketInfoReqCmd.class))
					cmd.cn = new DeviceSocketInfoReqCmd(jo_subcmd.getInt("type"));
				break;
			case DeviceSocketInfoRspCmd.subCommandType : 
				if(checkClassFields(jo_subcmd, DeviceSocketInfoRspCmd.class))
					cmd.cn = new DeviceSocketInfoRspCmd(jo_subcmd.getInt("result"),
							jo_subcmd.getInt("type"),
							jo_subcmd.getInt("onoff"),
							(float)jo_subcmd.getDouble("elec"));
				break;

			case TimeSettingReqCmd.subCommandType : 
				if(checkClassFields(jo_subcmd, TimeSettingReqCmd.class))
					cmd.cn = new TimeSettingReqCmd(jo_subcmd.getInt("type"),
							jo_subcmd.getString("timezone"));
				break;
			case TimeSettingRspCmd.subCommandType : 
				if(checkClassFields(jo_subcmd, TimeSettingRspCmd.class))
					cmd.cn = new TimeSettingRspCmd(jo_subcmd.getInt("result"),
							jo_subcmd.getInt("type"),
							jo_subcmd.getString("timezone"),
							jo_subcmd.getString("time"));
				break;

			case PushNotificationCmd.subCommandType : 
				if(checkClassFields(jo_subcmd, PushNotificationCmd.class))
					cmd.cn = new PushNotificationCmd(jo_subcmd.getInt("type"),
							jo_subcmd.getString("time"),
							jo_subcmd.getString("param1"),
							jo_subcmd.getString("param2"),
							jo_subcmd.getString("param3"));
				break;
				
			case BigdataMsgCmd.subCommandType:
				if(checkClassFields(jo_subcmd, BigdataMsgCmd.class))
					cmd.cn = new BigdataMsgCmd(jo_subcmd.getString("bigdata"));
				break;
				
				
			default:
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cmd.cn == null) {
			log.info(json);
			return null;
		}
		
		return cmd;
	}
	
	private static boolean checkClassFields(JSONObject jo, Class c) {
		boolean ret = true;
		
		Field[] fields = c.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].getName().equals("subCommandType") || fields[i].getName().equals("bigdata"))
				continue;
			
			if(jo.get(fields[i].getName()) == null){
				log.info("checkClassFields: class " + c + " missing " + fields[i].getName());
				ret = false;
				break;
			}
		}	
		return ret;
	}
	
	public static String ObjectToJSON(Object o) {
		JSONObject jsonObject = JSONObject.fromObject(o);
		 
		return jsonObject.toString();
	}
}
