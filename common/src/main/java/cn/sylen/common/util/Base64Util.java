package cn.sylen.common.util;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {
	
	public static String encrypt(String s){
		if(StringUtil.isEmpty(s)){
			return "";
		}
		s = new String(Base64.encodeBase64(s.getBytes()));
		return s;
	}
	
	public static String decrypt(String s){
		if(StringUtil.isEmpty(s)){
			return "";
		}
		s = new String(Base64.decodeBase64(s.getBytes()));
		return s;
	}
	
}
