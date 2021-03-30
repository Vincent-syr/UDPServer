package com.bluepower.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.threads.WorkerThread;

public class MysqlHelper {
	private static Log log = LogFactory.getLog(MysqlHelper.class);
	private static String driver = "com.mysql.jdbc.Driver";
//	private static String url = "jdbc:mysql://182.92.148.166:3306/scwindow?autoReconnect=true&useUnicode=true&characterEncoding=utf-8";
	private static String url = "jdbc:mysql://127.0.0.1:3306/scwindow?autoReconnect=true&useUnicode=true&characterEncoding=utf-8";
	private static String user = "root";
	private static String password = "password";
	private static Connection conn = null;

	public static void init() {
		if (conn == null)
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, user, password);

				if (!conn.isClosed())
					log.info("Succeeded connecting to the Database!");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("init exception: " + e.getMessage());
			}
	}

	public static ArrayList<String> getTokens(String deviceId) {
		ArrayList<String> tokenArray = new ArrayList<String>();
		String token = null;
		if (conn == null)
			init();

		try {
			Statement statement = conn.createStatement();
			//String sql = "select userid from ownership where deviceid=" + deviceId;
			
			String sql = "select ownership.name,ios_token from ownership LEFT JOIN user"
					+ " ON (ownership.userid=user.username) where deviceid='" 
					+ deviceId + "'";


			System.out.println(sql);
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				token = rs.getString("ios_token") + "&&&" + rs.getString("name");
				tokenArray.add(token);
				System.out.println(token);
			}

			rs.close();
			statement.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("getTokens exception:" + e.getMessage());
		}
		return tokenArray;
	}


	public static String getStringField(String field, String deviceId) {
		String fieldvalue = null;
		if (conn == null)
			init();

		try {
			Statement statement = conn.createStatement();
			//String sql = "select userid from ownership where deviceid=" + deviceId;
			
			String sql = "select " + field + " from device where device='" 
					+ deviceId + "'";

			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				fieldvalue = rs.getString(field);
				log.info("getStringField: " + sql + "|" + fieldvalue);
			}

			rs.close();
			statement.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("getStringField exception: " + field + " " + e.getMessage());
			return "failure";
		}
		return fieldvalue;
	}

	public static int setStringField(String field, String bigdata, String deviceId) {
		int result = 1;
		//result 0 success, result 1 failed
		
		if (conn == null)
			init();

		try {
			Statement statement = conn.createStatement();
			//String sql = "select userid from ownership where deviceid=" + deviceId;
			
			String sql = "update device set " + field + "='" + bigdata + "' where device='" 
					+ deviceId + "'";

			int ret = statement.executeUpdate(sql);
			
			if (ret > 0)			
				result = 0;
			
			System.out.println("ret = " + ret);
			
			statement.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block			
			log.info("setStringField exception: " + e.getMessage());
		}
		return result;
	}
	
	

	public static int createGateway(String deviceId) {
		int result = 1;
		//result 0 success, result 1 failed
		
		if (conn == null)
			init();

		try {
			Statement statement = conn.createStatement();
			//String sql = "select userid from ownership where deviceid=" + deviceId;
			//insert into device(device, join_date) values("aaaaaaaaaaaa", NOW())
			String sql = "insert into device(device,join_date, device_type,deviceversion) " + " values('" + deviceId + "', NOW(), -1,-1)";

			int ret = statement.executeUpdate(sql);
			
			if (ret > 0)			
				result = 0;
			
			System.out.println("ret = " + ret);
			
			statement.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block			
			log.info("setStringField exception: " + e.getMessage());
		}
		return result;
	}

	
	public static void uninit() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}
	}

}
