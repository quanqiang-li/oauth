package com.oauth.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.oauth.config.Constants;
import com.oauth.model.AuthorizationResponse;
import com.oauth.utils.HttpClientUtils;

/**
 * 登录
 * 
 */
@Controller
public class LoginController {


	/**
	 * 登录验证（实际登录调用认证服务器）
	 * @param request HttpServletRequest
	 * @return org.springframework.web.servlet.ModelAndView
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request) {
		// 当前系统请求认证服务器成功之后返回的Authorization Code
		String code = request.getParameter("code");

		HttpSession session = request.getSession();
		// code为空，则说明当前请求不是认证服务器的回调请求，重定向到首页
		if (StringUtils.isEmpty(code)) {
			return "redirect:/index.html";
		} else {
			// 1. 通过Authorization Code获取Access Token
			String url = "https://openapi.baidu.com/oauth/2.0/token";
			Map<String, String> params = new HashMap<>();
			params.put("grant_type", "authorization_code");
			params.put("code", code);
			params.put("client_id", "SarHMyM7qFPCGF2NwBrcqcP5");
			params.put("client_secret", "nbzTptX9nzEShXO9Ialh82sdbnn6co7m");
			params.put("redirect_uri", "http://localhost:7080/login");
			String result = HttpClientUtils.requestByPostForm(url, params, 3 * 1000);
			AuthorizationResponse response = JSON.parseObject(result, AuthorizationResponse.class);
			// 2. 如果正常返回
			if (response != null && !StringUtils.isEmpty(response.getAccess_token())) {
				System.out.println(response);
				// 2.1 将Access Token存到session
				session.setAttribute(Constants.SESSION_ACCESS_TOKEN, response.getAccess_token());
				return "redirect:/user.html";
			}
		}
		return "redirect:/index.html";
	}

}
