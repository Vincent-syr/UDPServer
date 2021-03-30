package com.bluepower.utility;

import java.util.Base64;

import com.mysql.jdbc.log.Log;

/** 
 * @ClassName: convertBase64Str2Hex 
 * @Description: this class convert base64 string to byte[]
 *    test result: successfully
 * @author: Vincent Sean
 * @date: 2018年7月11日 上午11:56:28  
 */
public class convertBase64Str2Hex {
	public static void base64toHex(String base64Str) {
		byte[] rawByte = Base64.getDecoder().decode(base64Str);
		int tempLen = rawByte.length;
		System.out.println("tempLen = " + tempLen);
		for(int i=0; i<rawByte.length; i++) {
			System.out.println("rawByte[" + i + "] = 0x" + Integer.toHexString(rawByte[i]).toUpperCase());
		}
	}
	
	public static void test() {
		String base64Str = "AQAxMzk5NDc4Nzg2OAAAAABuYgAAAAAAAAAAAAAAAADJAfQAVXhFW24AZABvAHcAMTkzNjQ3Z"
				+ "TVhOGQ2MzQ3MzA4OGRiOWY1YWVkNTJhY2Y0AAAAAQAAAP////9JfmY99j3gxJimQ9bgiWuyqlga+hQ9Lfp"
				+ "umPBuZ100jAAAAAAAAAAA/////5YCAwEAAIA/AACAPwAAgL8AAIC/hSpoc38BAAAYAAAAAAAAAAkAAABjAG"
				+ "8AbQAuAHMAYwBhAHAAcAAAAAEAAAAmAAAAYwBvAG0ALgBzAGMAYQBwAHAALwBjAG8AbQAuAHMAYwBhAHAA"
				+ "cAAuAFcAaQBuAGQAbwB3AEwAaQBzAHQAQQBjAHQAaQB2AGkAdAB5AAAAAAD/////AAAAAAAAAAAAAAAAAAA"
				+ "AAP///////////////////////////////9ACAAAABQAACAAAAA=="
				;
		base64toHex(base64Str);
	}
	public static void main(String[] args) {
		test();
	}
}
