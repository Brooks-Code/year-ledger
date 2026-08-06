package com.brooks.mall.user.config;

import com.brooks.mall.user.interceptor.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthInterceptor)
                // 拦截所有路径
                .addPathPatterns("/**")
                // 排除以下路径
                .excludePathPatterns(
                        "/api/auth/login",      // 排除登录
                        "/api/auth/register",   // 排除注册
                        "/doc.html",            // 排除 Swagger 文档等
                        "/webjars/**"
                );
    }
}