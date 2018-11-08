package com.manhui.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.User;
import com.manhui.util.SendRequest;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @ClassName: SessionInterceptor
 * @description: 添加拦截器
 * @author:	HeJiayan
 * @date Create in 下午5:28:30 2017年11月1日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class SessionInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//登陆登出不做拦截
		if(request.getRequestURI().equals("/login") || request.getRequestURI().equals("/login.action") 
				|| request.getRequestURI().equals("/admin/login") || request.getRequestURI().equals("/admin/login.action") 
				|| request.getRequestURI().equals("/admin/logout") || request.getRequestURI().equals("/admin/logout.action") 
				|| request.getRequestURI().equals("/dashboard.html") || request.getRequestURI().equals("/unreadMessageCount")){
			return true;
		}else{
			//判断token是否失效
			User user = (User) CacheManager.getCacheInfo("user").getValue();
			JSONObject json = new JSONObject();
			json.put("user_id", user.getUserId());
			json.put("token", user.getToken());
			
			String result = SendRequest.post(json, "/sys/out/check_token", "power");
			JSONObject resultJson = (JSONObject) JSONValue.parse(result); 
			Integer respCode = (Integer) resultJson.get("respCode");
			if(respCode == 1){
				//验证Session是否有效
				HttpSession session = request.getSession();
				if(session.isNew()){
					response.sendRedirect("/login?flag=timeout");
					return false;
				}else{
					return true;
				}
			}else{
				response.sendRedirect("/login?flag=timeout");
				return false;
			}
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
