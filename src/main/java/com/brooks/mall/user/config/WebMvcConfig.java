package com.brooks.mall.user.config;

import com.brooks.mall.user.interceptor.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserAuthInterceptor userAuthInterceptor;
    @Autowired
    private FileUploadConfig fileUploadConfig;

    // 添加拦截器
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
                        "/api/images/**",       // 排除图片
                        "/webjars/**",           // 排除 Swagger 静态资源
                        "/api/user/users"       // 排除用户列表
                );
    }

    // 配置静态资源映射，让前端能访问到图片
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = fileUploadConfig.getAccessPrefix();
        String path = fileUploadConfig.getUploadPath();

        // 防止配置为空导致启动报错
        if (prefix != null && path != null) {
            // 确保 path 以 / 结尾，防止拼接错误（视具体框架版本而定，通常 file: 协议比较宽容）
            if (!path.endsWith("/")) {
                path = path + "/";
            }

            registry.addResourceHandler("/api/images/**")
                    .addResourceLocations("file:" + path);
            // 禁止 Spring Boot 默认的静态资源处理干扰你
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }
}