package com.bluepower.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utility {

	public static String md5Post = "BPower";
	
	public static String getMd5(String text) {
		String result = null;
		String textWithPost = text + md5Post;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(textWithPost.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// result = buf.toString(); //md5 32bit
			// result = buf.toString().substring(8, 24))); //md5 16bit
			result = buf.toString().substring(8, 24);
			//System.out.println("mdt 16bit: " + buf.toString().substring(8, 24));
			//System.out.println("md5 32bit: " + buf.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}

}
