package com.oauth.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.oauth.utils.CommonTools;

/**
 * 
 * @author 2476056494@qq.com
 *
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

	ThreadLocal<Long> threadTime = new ThreadLocal<>();

	private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

	/**
	 * 进入controller层之前拦截请求
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 记录开始时间
		threadTime.set(System.currentTimeMillis());
		HttpSession session = request.getSession();

		// 获取session中存储的token
		String accessToken = (String) session.getAttribute(Constants.SESSION_ACCESS_TOKEN);

		boolean result = false;
		if (!StringUtils.isEmpty(accessToken)) {
			// 简单理解,session有access_token就认为登录成功了,但是如果请求随便传一个任意的值都可以通过,生产不要这么简单
			result = true;
		} else {
			// 如果token不存在，则跳转等登录页面
			response.getWriter().write("you hava not auth,please login first");
			result = false;
		}
		log.info("拦截path:{},ip:{},结果:{}", request.getServletPath(), CommonTools.getIp(request), result);
		return result;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		long time = System.currentTimeMillis() - threadTime.get();
		log.info("拦截path:{},ip:{},耗时:{}ms", request.getServletPath(), CommonTools.getIp(request), time);
		threadTime.remove();
	}

}
