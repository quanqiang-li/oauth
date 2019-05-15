package com.oauth.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 常用工具类
 * 
 * @author liqq
 *
 */
public class CommonTools {

	private static Logger log = LoggerFactory.getLogger(CommonTools.class);

	// 18位身份证匹配正则
	public static final String ID_CARD_18_REG = "(^[1-9]\\d{9}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)";

	/**
	 * 获取客户端ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = null;
		try {
			ip = request.getHeader("X-Forwarded-For");
			if (ip != null && ip.indexOf(",") > 0) {
				// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
				ip = ip.substring(0, ip.indexOf(","));
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("X-Real-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		if (StringUtils.isEmpty(ip) || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			ip = getHostIp();
		}
		return ip;
	}

	/**
	 * 获取本机内网IP地址方法
	 * 
	 * @return
	 */
	public static String getHostIp() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress ip = (InetAddress) addresses.nextElement();
					// loopback地址即本机地址，IPv4的loopback范围是127.0.0.0~ 127.255.255.255
					if (ip != null && ip instanceof Inet4Address && !ip.isLoopbackAddress() 
							&& ip.getHostAddress().indexOf(":") == -1) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 检查是否为18位身份证号码
	public static boolean checkIdCard18(String idnum) {
		// 正则表达式检查
		Pattern pattern = Pattern.compile(ID_CARD_18_REG);
		Matcher matcher = pattern.matcher(idnum);
		if (!matcher.matches()) {
			return false;
		}

		// 9开头的18位号码为统一社会信用代码
		if ("9".equals(idnum.substring(0, 1))) {
			return false;
		}

		// 验证码验证
		String strFactor = "68947310526894731";
		String strMod = "10X98765432";

		int[] arrNum = new int[17];
		int[] arrFactor = new int[17];
		for (int i = 0; i < 17; i++) {
			arrNum[i] = Integer.parseInt(idnum.substring(i, i + 1));
			// 实际计算因子需要加1
			arrFactor[i] = Integer.parseInt(strFactor.substring(i, i + 1)) + 1;
		}

		int sum = 0;
		for (int i = 0; i < 17; i++) {
			sum += arrNum[i] * arrFactor[i];
		}
		int pos = sum % 11;
		String checkCode = strMod.substring(pos, pos + 1);
		if (checkCode != null && checkCode.equalsIgnoreCase(idnum.substring(17))) {
			return true;
		}
		return false;
	}

}
