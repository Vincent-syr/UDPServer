/*
 * @Author: Vincent-syr
 * @Date: 2018-09-14 00:36:16
 * @LastEditTime: 2021-03-29 21:55:32
 * @Description: file content
 */
package com.bluepower.narrow_bound;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.cookie.PublicSuffixDomainFilter;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bluepower.utility.Utils.*;




/** 
 * @ClassName: MyAppTask 
 * @Description: TODO
 * @author: Vincent Sean
 * @date: 2018年6月13日 下午3:45:44  
 */
public class MyAppTask {
	// log
	public static Logger log = Logger.getLogger("NBLog");
	public static Map<String, String > test_mMapHeader = new HashMap<>();
	public static Map<String, String> static_mMapHeader = new HashMap<>();
	public static String static_mStrRefreshToken = "";
	
	static {
		log.info("new xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		test_mMapHeader.put("Authorization", "bearer 1f70cdc2c36b29e64a661641e2bb127");
		test_mMapHeader.put("app_key", "sYlozvsUDF9kTmIVMLxLD0_3siga");
		static_mMapHeader = test_mMapHeader;
//		log.info("static_mMapHeader = " + static_mMapHeader);
	}
	
	// authentication info
	static String strIp = "180.101.147.89";
	static String strPort = "8743";
	static String strAppId = "sYlozvsUDF9kTmIVMLxLD0_3siga";
	static String strPassword = "aY5opdPJHb7HZggjTtArE6KsLXAa";
	
	
	
	/***** Data Struct *****/
	// Device Basic Info
	public class DeviceBasicInfo {
		String mStrDeviceId;
		String mStrNodeId;
		String mStrManufacturerId;
		String mStrManufacturerName;
		String mStrModel;
		String mStrDeviceType;
		String mStrProtocolType;
		String mStrGateway;
		String mStrNodeType;
		String mStrStatus;

		// Constructor
		DeviceBasicInfo(String strDeviceId, String strNodeId,
							String strManufacturerId, String strManufacturerName,
							String strModel, String strDeviceType,
							String strProtocolType, String strGateway,
							String strNodeType, String strStatus) {
			mStrDeviceId = strDeviceId;
			mStrNodeId = strNodeId;
			mStrManufacturerId = strManufacturerId;
			mStrManufacturerName = strManufacturerName;
			mStrModel = strModel;
			mStrDeviceType = strDeviceType;
			mStrProtocolType = strProtocolType;
			mStrGateway = strGateway;
			mStrNodeType = strNodeType;
			mStrStatus = strStatus;
		}
	}

	// Service with Property
	public class ServiceInfo {
		String mStrServiceId;
		String mStrServiceType;
		Vector<String> mVecProperty;
		Vector<CommandInfo> mVecCommand;

		// Constructor
		ServiceInfo(String strServiceId, String strServiceType, Vector<String> vProperty, Vector<CommandInfo> vCommand) {
			mStrServiceId = strServiceId;
			mStrServiceType = strServiceType;
			mVecProperty = vProperty;
			mVecCommand = vCommand;
		}
	}

	// Command Info
	public class CommandInfo {
		String mStrCommandName;
		Vector<String> mVecItem;

		// Constructor
		CommandInfo(String strCommand, Vector<String> vItem) {
			mStrCommandName = strCommand;
			mVecItem = vItem;
		}
	}

	// Current Device Data with Time Stamp
	public class CurrentData {
		String mStrDate;
		String mStrTime;
		// Report Data With Property
		Map<String, Object> mMapData;

		// Constructor
		CurrentData(String strDataAndTime, Map<String, Object> mData) {
			mStrDate = getDate(strDataAndTime);
			mStrTime = getTime(strDataAndTime);
			mMapData = mData;
		}
	}

	// History Device Data with Time Stamp
	public class HistoryData {
		String mStrDate;
		String mStrTime;
		Object mObjValue;

		// Constructor
		HistoryData(String strDataAndTime, Object objValue) {
			mStrDate = getDate(strDataAndTime);
			mStrTime = getTime(strDataAndTime);
			mObjValue = objValue;
		}
	}

	// Command Detail
	public class CommandDetail {
		String mStrCommandId;
		String mStrServiceId;
		String mStrMethod;
		Integer mNTimeout;
		String mStrParas;
		String mStrResultCode;

		// Constructor
		CommandDetail(String strCommandId, String strServiceId, String strMethod, Integer nTimeout, String strParas, String strResultCode) {
			mStrCommandId = strCommandId;
			mStrServiceId = strServiceId;
			mStrMethod = strMethod;
			mNTimeout = nTimeout;
			mStrParas = strParas;
			mStrResultCode = strResultCode;
		}
	}

	// Rule Info
	public class RuleInfo {
		String mStrRuleId;
		String mStrRuleName;
		String mStrAuthor;
		String mStrStatus;

		// Constructor
		RuleInfo(String strRuleId, String strRuleName, String strAuthor, String strStatus) {
			mStrRuleId = strRuleId;
			mStrRuleName = strRuleName;
			mStrAuthor = strAuthor;
			mStrStatus = strStatus;
		}
	}
	
	
	/***** Data Member *****/
	// Basic Param of Connection
	String mStrBaseUrl;
	String mStrAppId;
	String mStrPassword;

	// HTTPS Connection Utils
	HttpsUtil mObjHttpsUtil;

	// Authentication
	HashMap<String, String> mMapHeader;
	String mStrRefreshToken;

//	// Log Printer
//	LogPrinter mLogPrinter;

	/***** Public Function *****/
	MyAppTask(String strIp, String strPort, String strAppId, String strPassword) throws Exception {
		// Set Data Member
		mStrBaseUrl = "https://" + strIp + ":" + strPort + "/";
		mStrAppId = strAppId;
		mStrPassword = strPassword;

		// Create And Init HttpsUtil Object
		mObjHttpsUtil = new HttpsUtil();
		mObjHttpsUtil.initSSLConfigForTwoWay();
	}
	
	
	// Authentication
	public boolean authentication() throws Exception {
		// Check Param
		if (!checkParam(mStrBaseUrl) || !checkParam(mStrAppId) || !checkParam(mStrPassword) || mObjHttpsUtil == null) {
			return false;
		}

		// Request URL
		String strUrlLogin = mStrBaseUrl + "iocm/app/sec/v1.1.0/login";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("appId", mStrAppId);
		mParam.put("secret", mStrPassword);
		// Send Request
		String strResult = mObjHttpsUtil.doPostFormUrlEncodedForString(strUrlLogin, mParam);
		log.info("+++ strResult = " + strResult);    // print https request
		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		// transfer json to map
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		log.info("---- mResult = " + mResult);    // print map
		
		if (mResult.get("error_code") != null) {
			return false;
		}

		// Update Data Member
		mMapHeader = new HashMap<String, String>();
		mMapHeader.put("app_key", mStrAppId);
		mMapHeader.put("Authorization", mResult.get("tokenType").toString() + " " + mResult.get("accessToken").toString());
		mStrRefreshToken = mResult.get("refreshToken").toString();
		log.info("mMapHeader = " + mMapHeader);
		log.info("mStrRefreshToken = " + mStrRefreshToken);
		return true;
	}
	
	// Authentication
	/** 
	 * @Title: authentication_v2 
	 * @Description: authentication_v2 store static_mMapHeader as static field
	 * @return
	 * @throws Exception 
	 */
	public boolean authentication_v2() throws Exception {
		// Check Param
		if (!checkParam(mStrBaseUrl) || !checkParam(mStrAppId) || !checkParam(mStrPassword) || mObjHttpsUtil == null) {
			return false;
		}

		// Request URL
		String strUrlLogin = mStrBaseUrl + "iocm/app/sec/v1.1.0/login";
		// Param
		Map<String, String> mParam = new HashMap<String, String>();
		mParam.put("appId", mStrAppId);
		mParam.put("secret", mStrPassword);
		// Send Request
		String strResult = mObjHttpsUtil.doPostFormUrlEncodedForString(strUrlLogin, mParam);
		log.info("+++ strResult = " + strResult);    // print https request
		// Parse Result
		Map<String, Object> mResult = new HashMap<String, Object>();
		// transfer json to map
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		log.info("---- mResult = " + mResult);    // print map
		
		if (mResult.get("error_code") != null) {
			return false;
		}

		// Update Data Member
		static_mMapHeader = new HashMap<String, String>();
		static_mMapHeader.put("app_key", mStrAppId);
		static_mMapHeader.put("Authorization", mResult.get("tokenType").toString() + " " + mResult.get("accessToken").toString());
		static_mStrRefreshToken = mResult.get("refreshToken").toString();
		log.info("static_mMapHeader = " + static_mMapHeader);
		log.info("static_mStrRefreshToken = " + static_mStrRefreshToken);
		return true;
	}
	
	/***** Private Function *****/
	
	/** 
	 * @Title: checkParam 
	 * @Description: check wheather strParam is empty or null
	 * @param strParam
	 * @return 
	 */
	boolean checkParam(String strParam) {
		if (strParam == null || strParam.length() == 0) {
			return false;
		}
		return true;
	}


	/** 
	 * @Title: getTime 
	 * @Description: Get (String ex: 18:20:17)Time from Timestamp
	 * @param strTimestamp
	 * @return 
	 */
	String getTime(String strTimestamp) {
		String strHour = strTimestamp.substring(9, 11);
		String strMinute = strTimestamp.substring(11, 13);
		String strSecond = strTimestamp.substring(13, 15);
		return strHour + ":" + strMinute + ":" + strSecond;
	}

	
	/** 
	 * @Title: getDate 
	 * @Description: Get (String ex:1995/05/14)Date From (String)Timestamp
	 * @param strTimestamp
	 * @return 
	 */
	String getDate(String strTimestamp) {
		String strYear = strTimestamp.substring(0, 4);
		String strMonth = strTimestamp.substring(4, 6);
		String strDate = strTimestamp.substring(6, 8);
		return strYear + "/" + strMonth + "/" + strDate;
	}
	
	
	/** 
	 * @Title: errorHandle 
	 * @Description: store error code and reason in NBError.log
	 * @param mResult
	 * @throws Exception 
	 */
	public static void errorHandle(Map<String, Object> mResult) throws Exception {
		String strErrCode = mResult.get("error_code").toString();
		String strErrDesc = mResult.get("error_desc").toString();
		log.error("Error Code: " + strErrCode + ", Reason: " + strErrDesc);
	}
	
		
	// Parse Result String
	/** 
	 * @Title: parseResultString 
	 * @Description: analyze whethear strResult has error_code, called by postAsyncCommand
	 * add: analyse wheather 
	 * @param strResult
	 * @return
	 * @throws Exception 
	 */
	boolean parseResultString(String strResult) throws Exception {
		if (strResult == null || strResult.length() == 0) {
			return true;
		}
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		if (mResult.get("error_code") != null) {
			errorHandle(mResult);
			return false;
		} 
		else {
			return true;
		}
	}
	
	
	// Parse Array
	Vector<String> parseStringToArray(String strSource) {
		// Check
		if (strSource == null || strSource.length() == 0) {
			return null;
		}
		Vector<String> vList = new Vector<String>(10, 1);
		int nLeftBrace = 0;
		int nCurPartStart = 0;
		for (int i = 0; i < strSource.length(); ++i) {
			if (strSource.charAt(i) == '{') {
				++nLeftBrace;
			} else if (strSource.charAt(i) == '}') {
				--nLeftBrace;
				if (nLeftBrace == 0) {
					String strCurPart = strSource.substring(nCurPartStart, i + 1);
					vList.addElement(strCurPart);
					nCurPartStart = i + 2;
				}
			}
		}
		return vList;
	}
	
	
	
	// Post Asnyc Command
	/** 
	 * @Title: postAsyncCommand 
	 * @Description: post command to NB server and return the result of NB
	 * @param strDeviceId
	 * @param strServiceId
	 * @param strCommand
	 * @param strItem
	 * @param strValue
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public boolean postAsyncCommand(String strDeviceId, String strServiceId, String strCommand, String strItem, String strValue, String strTimeout) throws Exception {
		log.info("-----------------");
		log.info("strServiceId = " + strServiceId);
		log.info("strCommand = " + strCommand);
		log.info("strItem = " + strItem);
		
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId) || !checkParam(strCommand)
				|| !checkParam(strItem) || !checkParam(strValue) || !checkParam(strTimeout)) {
			log.error("Post Aysnc Command, Some Param is NULL.");
			return false;
		}
		
		// 取出末尾空格
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/devices/" + strDeviceId + "/commands";
		// Param
		ObjectNode oValueParam = JsonUtil.convertObject2ObjectNode("{\"" + strItem + "\":\"" + strValue + "\"}");
		Map<String, Object> mParamCommand = new HashMap<String, Object>();
		mParamCommand.put("serviceId", strServiceId);
		mParamCommand.put("method", strCommand);
		mParamCommand.put("paras", oValueParam);
		mParamCommand.put("hasMore", 0);
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("command", mParamCommand);
		mParam.put("expireTime", strTimeout);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, mMapHeader, strRequest);
		log.info("after post command, mMapHeader = " + mMapHeader);
		// Parse result
		return parseResultString(strResult);
	}
	
	
	
	
	
	/** 
	 * @Title: postAsyncCommand_V2 
	 * @Description: this is to test how long a mMapHeader is valid, during one authentication
	 * @param strDeviceId
	 * @param strServiceId
	 * @param strCommand
	 * @param strItem
	 * @param strValue
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public boolean postAsyncCommand_test_v2(String strDeviceId, String strServiceId, String strCommand, String strItem, String strValue, String strTimeout) throws Exception {
		log.info("-----------------");
		log.info("strServiceId = " + strServiceId);
		log.info("strCommand = " + strCommand);
		log.info("strItem = " + strItem);
		
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId) || !checkParam(strCommand)
				|| !checkParam(strItem) || !checkParam(strValue) || !checkParam(strTimeout)) {
			log.error("Post Aysnc Command, Some Param is NULL.");
			return false;
		}
		
		// 取出末尾空格
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/devices/" + strDeviceId + "/commands";
		// Param
		ObjectNode oValueParam = JsonUtil.convertObject2ObjectNode("{\"" + strItem + "\":\"" + strValue + "\"}");
		Map<String, Object> mParamCommand = new HashMap<String, Object>();
		mParamCommand.put("serviceId", strServiceId);
		mParamCommand.put("method", strCommand);
		mParamCommand.put("paras", oValueParam);
		mParamCommand.put("hasMore", 0);
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("command", mParamCommand);
		mParam.put("expireTime", strTimeout);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		log.info("before post, static_mMapHeader = " + static_mMapHeader);
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, static_mMapHeader, strRequest);
		log.info("strResult = " + strResult);
		boolean postFlag = parseResultString(strResult);
		if(!postFlag) {
			return false;
		}
		// expired token case or normal case
		postFlag = true;   // initial postFlag
		Map<String, Object> mResult = new HashMap<String, Object>();
		mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
		// expired case
		if("access token expired".equals(mResult.get("resultmsg"))) {
			log.info("**** token expired, call communicate2IoTPlatformWithAuthenticate()");
			String retCode = communicate2IoTPlatformWithAuthenticate(strServiceId, strValue, strDeviceId, strTimeout);
			// retCode != 0
			if(! "0".equals(retCode)) {  
				postFlag = false;
			}
		}
		// Parse result
		return postFlag;
	}
	
	
	
	/** 
	 * @Title: postAsyncCommand_test_v2 
	 * @Description: TODO
	 * @param strDeviceId
	 * @param strServiceId
	 * @param strCommand
	 * @param strItem
	 * @param strValue
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public String postAsyncCommand_test_v3(String strDeviceId, String strServiceId, String strCommand, String strItem, String strValue, String strTimeout) throws Exception {
		log.info("-----------------");
		log.info("strServiceId = " + strServiceId);
		log.info("strCommand = " + strCommand);
		log.info("strItem = " + strItem);
		
		// Check Param
		if (!checkParam(strDeviceId) || !checkParam(strServiceId) || !checkParam(strCommand)
				|| !checkParam(strItem) || !checkParam(strValue) || !checkParam(strTimeout)) {
			log.error("Post Aysnc Command, Some Param is NULL.");
			return "null param";
		}
		
		// 取出末尾空格
		while (strDeviceId.endsWith(" ")) {
			strDeviceId = strDeviceId.substring(0, strDeviceId.length() - 1);
		}

		// Request URL
		String strUrl = mStrBaseUrl + "iocm/app/cmd/v1.2.0/devices/" + strDeviceId + "/commands";
		// Param
		ObjectNode oValueParam = JsonUtil.convertObject2ObjectNode("{\"" + strItem + "\":\"" + strValue + "\"}");
		Map<String, Object> mParamCommand = new HashMap<String, Object>();
		mParamCommand.put("serviceId", strServiceId);
		mParamCommand.put("method", strCommand);
		mParamCommand.put("paras", oValueParam);
		mParamCommand.put("hasMore", 0);
		Map<String, Object> mParam = new HashMap<String, Object>();
		mParam.put("command", mParamCommand);
		mParam.put("expireTime", strTimeout);
		String strRequest = JsonUtil.jsonObj2Sting(mParam);
		// Send Request
		log.info("before post, static_mMapHeader = " + static_mMapHeader);
		String strResult = mObjHttpsUtil.doPostJsonForString(strUrl, static_mMapHeader, strRequest);
		log.info("strResult = " + strResult);
		return strResult;
	}
	
	
		
	
	
	/** 
	 * @Title: test 
	 * @Description: for test, run the whole post command, include authentication and post value
	 * @throws Exception 
	 */
	public static void test() throws Exception {
		MyAppTask app = new MyAppTask(strIp, strPort, strAppId, strPassword);
		// login
		boolean loginFlag = false;
		loginFlag = app.authentication();
		log.info("strIp=" + strIp + ", strPort = " + strPort
				 + ", strAppId = " + strAppId + ", strPassword = " + strPassword);
		if(!loginFlag) {
			log.error("wrong login input");
			return;
		}
		log.info("login successfully");
		
		// post command
		String strDeviceId = "60c5ccff-6d9c-4e1d-bb1f-7307799ffc35";
		String strServiceId = "MsgSend";
		String strCommand = "Send";
		String strItem = "value";	
		String strValue = "mytest1";
		String strTimeout = "0";
		boolean postFlag = false;
		
		
		String strBase64Value = Base64.getEncoder().encodeToString(strValue.getBytes("utf-8"));
		
		
		postFlag = app.postAsyncCommand(strDeviceId, strServiceId, strCommand, strItem, strBase64Value, strTimeout);
		if (postFlag) {
			log.info("success post command");
		}
	}	
	
	
	
	/** 
	 * @Title: communicate2IoTPlatform 
	 * @Description: authentication with ip password,
		and send msg from caller of funcition. each time the func is called, it will authentication.
	 * @param strService
	 * @param strValue
	 * @param strDeviceId
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public static String communicate2IoTPlatform(String strService, String strValue, String strDeviceId, String strTimeout) throws Exception {
		
		MyAppTask app = new MyAppTask(strIp, strPort, strAppId, strPassword);
		// login
		boolean loginFlag = false;
		loginFlag = app.authentication();
		if(!loginFlag) {
			log.error("wrong login input");
			return "1";
		}
		log.info("login successfully");		
		
//		 post command
		String strServiceId = "MsgSend";
		String strCommand = "Send";
		String strItem = "value";	
//		String strValue = "mytest1";
//		String strTimeout = "0";
		boolean postFlag = false;
		String strBase64Value = Base64.getEncoder().encodeToString(strValue.getBytes("utf-8"));
		log.info("strBase64Value = " + strBase64Value);
				
		try {
			postFlag = app.postAsyncCommand(strDeviceId, strServiceId, strCommand, strItem, strBase64Value, strTimeout);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (postFlag) {
			log.info("success post command");
			return "0";
		}else {
			return "2";
		}
	}
		
	
	
	/** 
	 * @Title: communicate2IoTPlatformV2 
	 * @Description: authentication for one time, and the next time when the func is called,
	 * it only send msg, won't authentication.
	 * @param strService
	 * @param strValue
	 * @param strDeviceId
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public static String communicate2IoTPlatform_v2(String strService, String strValue, String strDeviceId, String strTimeout) throws Exception {
		MyAppTask app = new MyAppTask(strIp, strPort, strAppId, strPassword);

//		 post command
		String strServiceId = "MsgSend";
		String strCommand = "Send";
		String strItem = "value";	
		boolean postFlag = false;
		log.info("before base64 transfer, strValue lenth = " + strValue.length());
		String strBase64Value = Base64.getEncoder().encodeToString(strValue.getBytes("utf-8"));
		log.info("after base64 transfer, strBase64Value lenth = " + strBase64Value.length());
		log.info("strBase64Value = " + strBase64Value);
				
		try {
			postFlag = app.postAsyncCommand_test_v2(strDeviceId, strServiceId, strCommand, strItem, strBase64Value, strTimeout);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (postFlag) {
			log.info("success post command");
			return "0";
		}else {
			return "2";
		}		
	}
	
	
	
	/** 
	 * @Title: communicate2IoTPlatformWithAuthenticate 
	 * @Description: this version of communicate2IoTPlatform use static field "static_mMapHeader"
	 * @param strService
	 * @param strValue
	 * @param strDeviceId
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public static String communicate2IoTPlatformWithAuthenticate(String strService, String strValue, String strDeviceId, String strTimeout) throws Exception {
		long start = System.currentTimeMillis();
		MyAppTask app = new MyAppTask(strIp, strPort, strAppId, strPassword);
		// login
		boolean loginFlag = false;
		loginFlag = app.authentication_v2();
		if(!loginFlag) {
			log.error("wrong login input");
			return "1";
		}
		log.info("login successfully");		
		long endAuthenticate = System.currentTimeMillis();
		log.info("time spend of authentication = " + (endAuthenticate-start) + "ms");
//		 post command
		String strServiceId = "MsgSend";
		String strCommand = "Send";
		String strItem = "value";	
//		String strValue = "mytest1";
//		String strTimeout = "0";
		boolean postFlag = false;
		
//		String strBase64Value = Base64.getEncoder().encodeToString(strValue.getBytes("utf-8"));
//		log.info("strBase64Value = " + strBase64Value);
		
		try {
			postFlag = app.postAsyncCommand_test_v2(strDeviceId, strServiceId, strCommand, strItem, strValue, strTimeout);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (postFlag) {
			log.info("success post command");
			return "0";
		}else {
			return "2";
		}
	}
	
	
	/** 
	 * @Title: communicate2IoTPlatform_v4 
	 * @Description: TODO
	 * @param strValue
	 * @param strService
	 * @param strDeviceId
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public static String communicate2IoTPlatform_v4(byte[] byteValue, String strService, String strDeviceId, String strTimeout) throws Exception {
		
		MyAppTask app = new MyAppTask(strIp, strPort, strAppId, strPassword);
		// login
		boolean loginFlag = false;
		loginFlag = app.authentication();
		if(!loginFlag) {
			log.error("wrong login input");
			return "1";
		}
		log.info("login successfully");		
		
//		 post command
		String strServiceId = "MsgSend";
		String strCommand = "Send";
		String strItem = "value";	

		boolean postFlag = false;
		String strBase64Value = Base64.getEncoder().encodeToString(byteValue);
		log.info("strBase64Value = " + strBase64Value);
				
		try {
			postFlag = app.postAsyncCommand(strDeviceId, strServiceId, strCommand, strItem, strBase64Value, strTimeout);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (postFlag) {
			log.info("success post command");
			return "0";
		}else {
			return "2";
		}
	}
	
	
	
	
	
	/** 
	 * @Title: reAuthentication 
	 * @Description: used to update mMapHeader and other param
	 * @param app
	 * @return
	 * @throws Exception 
	 */
	public static boolean reAuthentication(MyAppTask app) throws Exception {
		boolean loginFlag = false;
		loginFlag = app.authentication_v2();
		if(!loginFlag) {
			log.error("wrong login input");
			return false;
		}
		log.info("login successfully");	
		return loginFlag;
	}
	
	
	/** 
	 * @Title: communicate2IoTPlatform_v5 
	 * @Description: 结合reAuthentication, 可以在mapHeader过期时自动更新mapHeader
	 * @param byteValue
	 * @param strService
	 * @param strDeviceId
	 * @param strTimeout
	 * @return
	 * @throws Exception 
	 */
	public static String communicate2IoTPlatform_v5(byte[] byteValue, String strService, String strDeviceId, String strTimeout) throws Exception {
		 //post command
		MyAppTask app = new MyAppTask(strIp, strPort, strAppId, strPassword);

		String strServiceId = "MsgSend";
		String strCommand = "Send";
		String strItem = "value";	
		String strBase64Value = Base64.getEncoder().encodeToString(byteValue);
		boolean postFlag = false;
		
		// 最多重新登录3次18001223410
		for (int i=0; i<3; i++) {
			String strResult = app.postAsyncCommand_test_v3(strDeviceId, strServiceId, strCommand, strItem, strBase64Value, strTimeout);
			// some post param is null
			if("null param".equals(strResult)) {
				log.error("some post param is null");
				return "2";
			}
			Map<String, Object> mResult = new HashMap<String, Object>();
			mResult = JsonUtil.jsonString2SimpleObj(strResult, mResult.getClass());
			// error code
			if (mResult.get("error_code") != null) {
				errorHandle(mResult);
				return "2";
			} 
			// expired mapMeader case, update mMapHeader
			if ("1010005".equals(mResult.get("resultcode"))) {
				log.info("expired or invalid mMapHeader case");
				boolean loginFlag = reAuthentication(app);
				if(!loginFlag) {
					return "1";
				}
			}
			// successfully post case
			else {
				log.info("successfully post");
				postFlag = true;
				break;
			}			
		}
		return postFlag?"0":"2";
	}

	
	public static void main(String[] args) throws Exception {
		String strDeviceId = "93647e5a-8d63-4730-88db-9f5aed52acf4";
		String strService = "MsgSend";
		String strValue = "mytest1";
		String strTimeout = "0";
		byte[] byteValue = strValue.getBytes();
				
		String resultCode = communicate2IoTPlatform_v5(byteValue, strService, strDeviceId, strTimeout);
		log.info("resultCode = " + resultCode);
//		communicate2IoTPlatform_v2(strService, strValue, strDeviceId, strTimeout);
//		communicate2IoTPlatform(strService, strValue, strDeviceId, strTimeout);
		log.info("finish++++++++++++++++++++++++");
	}
}