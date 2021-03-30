package com.bluepower.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluepower.command.UDPConst;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.*;

public class IOSPushNotification {

	private static Log log = LogFactory.getLog(IOSPushNotification.class);
	private static PushNotificationManager pushManager = null;
	private static Object lock = new Object();

	public static void sendPushNotification(String token, String content) {
		String deviceid = null;
		synchronized (lock) {
			try {
				// 从客户端获取的deviceToken，在此为了测试简单，写固定的一个测试设备标识。
				// String deviceToken =
				// "ab9e011e126838d7e409c022a70bc0d6fd7b167c42e2aa77bc297c579009e48f";
				deviceid = "iPhone" + token;
				log.info("Push Start deviceToken:" + token);
				// 定义消息模式
				PayLoad payLoad = new PayLoad();
				// payLoad.addAlert("this is test 2 3!");
				payLoad.addAlert(content);
				payLoad.addBadge(1);
				payLoad.addSound("default");

				pushManager = PushNotificationManager.getInstance();
				
				pushManager.addDevice(deviceid, token);				
				Device client = pushManager.getDevice(deviceid);

				if (client == null) {
					log.info("sendPushNotification: get client fail " + deviceid);
					return;
				}

				String host = "gateway.push.apple.com";
				// String host = "gateway.push.apple.com";
				int port = 2195;
				String certificatePath = UDPConst.getP12Path();// 前面生成的用于JAVA后台连接APNS服务的*.p12文件位置
				String certificatePassword = "6169121";// p12文件密码。
				pushManager.initializeConnection(host, port, certificatePath,
						certificatePassword,
						SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);

				// 发送推送
				if(client == null) {
					log.info("sendPushNotification: get client fail.");
				}
				
				log.info("push notification: " + client.getToken() + "\n"
						+ payLoad.toString() + " ");
				pushManager.sendNotification(client, payLoad);
				pushManager.stopConnection();
				//pushManager.removeDevice(deviceid);
				log.info("Push End deviceToken:" + token);

			} catch (Exception ex) {
				log.info("sendPushNotification exception:" + ex.getMessage());				
				for (int k = 0; k < ex.getStackTrace().length; k++) {
					log.info(ex.getStackTrace()[k].toString());
				}
			}
		}
	}

}
