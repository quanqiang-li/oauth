package com.oauth.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oauth.config.Constants;
import com.oauth.utils.HttpClientUtils;

/**
 * 用户相关controller,需要权限
 */
@RestController
@RequestMapping("/user")
public class UserController {

	/**
	 * 用户信息,受保护
	 * 
	 * @return java.lang.String
	 */
	@RequestMapping("/getInfo")
	public String userIndex(HttpServletRequest request) {
		String access_token = (String) request.getSession().getAttribute(Constants.SESSION_ACCESS_TOKEN);
		String url = "https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
		Map<String, String> params = new HashMap<>();
		params.put("access_token", access_token);
		String result = HttpClientUtils.requestByPostForm(url, params, 3 * 1000);
		return result;
	}
	
	/**
	 * 移除授权
	 * @param request
	 * @return
	 */
	@RequestMapping("/removeAuth")
	public String removeAuth(HttpServletRequest request){
		String access_token = (String) request.getSession().getAttribute(Constants.SESSION_ACCESS_TOKEN);
		String url = "https://openapi.baidu.com/rest/2.0/passport/auth/revokeAuthorization";
		Map<String, String> params = new HashMap<>();
		params.put("access_token", access_token);
		String result = HttpClientUtils.requestByPostForm(url, params, 3 * 1000);
		if(StringUtils.isEmpty(result)) {
			return "百度服务出错了!";
		}
		JSONObject parseObject = JSON.parseObject(result);
		parseObject.put("description", "1成功，0失败");
		return parseObject.toJSONString();
	}

}
