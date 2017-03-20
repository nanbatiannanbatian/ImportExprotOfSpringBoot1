package com.wuxianedu.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.wuxianedu.interceptor.LoginInterceptor;
@Component
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

	  @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        // 注册拦截器
	        InterceptorRegistration loginInterceptor = registry.addInterceptor(new LoginInterceptor());
	        // 配置拦截的路径
	        loginInterceptor.addPathPatterns("/**");
	     // 配置不拦截的路径
	        loginInterceptor.excludePathPatterns("/login/**");
	        loginInterceptor.excludePathPatterns("/login/**");
	        loginInterceptor.excludePathPatterns("/tocontroller/**");
	    }
	
}
