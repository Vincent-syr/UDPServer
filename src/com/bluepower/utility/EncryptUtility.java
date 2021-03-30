package com.bluepower.utility;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.bluepower.request.UDPPacket;

public class EncryptUtility {

	public static Cipher cipher = null;

	
	public static void aes_encrypt(byte[] input, byte[] pwd) {
		SecretKeySpec key = new SecretKeySpec(pwd, "AES");
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key);		// 初始化
			byte[] result = cipher.doFinal(input);

			System.arraycopy(result, 0, input, 0, 16);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void aes_decrypt(byte[] input, byte[] pwd) {
		SecretKeySpec key = new SecretKeySpec(pwd, "AES");
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key);		// 初始化
			byte[] result = cipher.doFinal(input);

			System.arraycopy(result, 0, input, 0, 16);
		} catch (Exception e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	

	public static void sc_decrypt(UDPPacket r) {

		if (r.getEncry() != 1)
			return;
		
		sc_encription(r, 0);
		r.setEncry(0);
	}
	
	
	/** 
	 * @Title: sc_encrypt 
	 * @Description: 对packet的内容进行加密，若加密位==1，即已经加密， 则直接返回
	 * @param r 
	 */
	public static void sc_encrypt(UDPPacket r) {

		if (r.getEncry() == 1)
			return;
		
		sc_encription(r, 1);
		r.setEncry(1);
	}
	
	
	
	/** 
	 * @Title: sc_encription 
	 * @Description: if encrypt, mode = 1;  if decrypt, mode = 0
	 * @param r
	 * @param mode 
	 */
	public static void sc_encription(UDPPacket r, int mode) {
		
		byte[] buf = new byte[16];
 		byte[] pwd = new byte[16];
		int seq = r.getSeq();
		
		
		//pwd 0 - 3, seq
		pwd[0] = (byte)(seq & 0xff);
		pwd[1] = (byte)((seq >> 8) & 0xff);
		pwd[2] = (byte)((seq >> 16) & 0xff);
		pwd[3] = (byte)((seq >> 24) & 0xff);
		
		//pwd 4 - 7, fromid
		pwd[4] = (byte)(r.getFromId().getBytes()[0] & 0xff);
		pwd[5] = (byte)((r.getFromId().getBytes()[1]) & 0xff);
		pwd[6] = (byte)((r.getFromId().getBytes()[2]) & 0xff);
		pwd[7] = (byte)((r.getFromId().getBytes()[3]) & 0xff);
		
	    //pwd 8 - 11, toid
		pwd[8] = (byte)(r.getToId().getBytes()[0] & 0xff);
		pwd[9] = (byte)((r.getToId().getBytes()[1]) & 0xff);
		pwd[10] = (byte)((r.getToId().getBytes()[2]) & 0xff);
		pwd[11] = (byte)((r.getToId().getBytes()[3]) & 0xff);
		
		//pwd 12 - 15, fromid
		pwd[12] = (byte)(30000000 & 0xff);
		pwd[13] = (byte)((30000000 >> 8) & 0xff);
		pwd[14] = (byte)((30000000 >> 16) & 0xff);
		pwd[15] = (byte)((30000000 >> 24) & 0xff);
		
		/*System.out.print("pwd ");
		for (int i = 0; i < 15; i++) {
			System.out.print(pwd[i] + " ");
		}
		System.out.println();
		*/
		
		System.arraycopy(r.content, UDPPacket.PACKET_HEAD_LEN - 4, buf, 0, 16);		
		if (mode == 0)
			aes_decrypt(buf, pwd);
		else
			aes_encrypt(buf, pwd);		
		System.arraycopy(buf, 0, r.content, UDPPacket.PACKET_HEAD_LEN - 4, 16);

		
		System.arraycopy(r.content, UDPPacket.PACKET_HEAD_LEN + 12, buf, 0, 16);
		if (mode == 0)
			aes_decrypt(buf, pwd);
		else
			aes_encrypt(buf, pwd);		
		System.arraycopy(buf, 0, r.content, UDPPacket.PACKET_HEAD_LEN + 12, 16);
		
		
	}

}
