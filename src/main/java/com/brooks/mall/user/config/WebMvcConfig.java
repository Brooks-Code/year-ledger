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
                        "/user/login",      // 登录接口
                        "/user/register",   // 注册接口
                        "/doc.html"         // Swagger/Knife4j 文档页面
                );
    }
}