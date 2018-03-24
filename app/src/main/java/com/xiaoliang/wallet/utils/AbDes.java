
/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoliang.wallet.utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


// TODO: Auto-generated Javadoc
/**
 * © 2012 amsoft.cn 
 * 名称：AbDes.java 
 * 描述：DES工具类.
 * 
 * @author 还如一梦中
 * @version v1.0
 * @date：2011-11-10 下午11:52:13
 */
public class AbDes {

	private byte[] iv;

	public AbDes(byte[] iv) {
		super();
		this.iv = iv;
	}

	public static AbDes newInstance(byte[] iv) {
		AbDes des = new AbDes(iv);
		return des;
	}

	public String encrypt(byte[] encryptByte, String encryptKey) {
		try {
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] encryptedData = cipher.doFinal(encryptByte);
			return AbBase64.encode(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] decrypt(String encryptString, String encryptKey) {
		try {
			byte[] encryptByte = AbBase64.decode(encryptString);
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			return cipher.doFinal(encryptByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
    	System.out.println(encrypt("864645021589434"));
    	System.out.println(encrypt("啦啦啦"));
    	System.out.println(decrypt("wlSx+vw+UO6LMWdKw4AJsWOh+UE/Gsqg0z7SFHQ0DM3c74LRECa7+FWCIQg1TdmpkmFiLwTOf54gBKLM3BGclwBpoAMogjdZPNv4d6DH41gmnWWFz1sPmEdlTrIhSHMa"));
	}
    
    public static String encrypt(String str){
		AbDes des = AbDes.newInstance("yxs!1sdf".getBytes());
    	String a = null;
		try {
			a = des.encrypt(str.getBytes("UTF-8"),"sxlamt34");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return a;
	}
	public static String decrypt(String str){
		AbDes des = AbDes.newInstance("yxs!1sdf".getBytes());
		String a = null;
		try {
			a = new String(des.decrypt(str,"sxlamt34"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
}
