package com.scwindow.utility;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MISCUtility {

	public static String getStringFromBytes(int start, byte b[]) {
		String ret = null;
		byte tempb[];
		int i;

		for (i = start; i < b.length; i++) {
			if (b[i] == 0)
				break;
		}

		tempb = new byte[i - start];

		System.arraycopy(b, start, tempb, 0, (i - start));

		return new String(tempb);
	}

	public static String getByteStr(byte[] bs, int length) {
		String ret = "";

		for (int i = 0; i < length; i++) {
			int t = new Integer(bs[i]) & 0xff;
			if (t > 0x0f)
				ret += ("" + Integer.toHexString(t) + " ");
			else
				ret += ("0" + Integer.toHexString(t) + " ");
		}

		return ret;

	}
	

	public static String getByteStr(int start, byte[] bs, int length) {
		String ret = "";

		for (int i = start; i < start + length; i++) {
			int t = new Integer(bs[i]) & 0xff;
			if (t > 0x0f)
				ret += ("" + Integer.toHexString(t) + " ");
			else
				ret += ("0" + Integer.toHexString(t) + " ");
		}

		return ret;

	}

}
