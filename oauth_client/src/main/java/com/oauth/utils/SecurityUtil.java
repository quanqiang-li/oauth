package com.oauth.utils;

import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecurityUtil {

	/**
	 * PBKDF2WithHmacSHA1 哈希算法,固定盐值aisinozx,哈希次数1024
	 * 
	 * @param src
	 * @return 32位长度的16进制字符串
	 * @throws Exception
	 */
	public static String pbkdf2(String src) {
		if (src == null || "".equals(src)) {
			return null;
		}
		byte[] salt = "aisinozx".getBytes();
		KeySpec spec = new PBEKeySpec(src.toCharArray(), salt, 1024, 128);
		byte[] hash = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = factory.generateSecret(spec).getEncoded();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytesToHexString(hash);
	}

	/**
	 * 字节转16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		System.out.println(pbkdf2("1qaz2wsx"));
	}
}
