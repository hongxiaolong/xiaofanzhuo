package com.xiaolong.xiaofanzhuo.dataoperations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataEncryptService {

	/**
	 * Hashes the password with MD5.  
	 * @param s
	 * @return
	 */
	public static  String md5(String s) {
	    try {
	     
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        return s;
	    }
	}
}
