package com.manhui;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.manhui.Interceptor.SessionInterceptor;

/**
 * @ClassName: SessionConfiguration
 * @description: 配置拦截器
 * @author:	HeJiayan
 * @date Create in 下午5:31:09 2017年11月1日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Configuration
public class SessionConfiguration extends WebMvcConfigurerAdapter{
	@Override
	public void addInterceptors(InterceptorRegistry registry){
		registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**");
	}
}
