package com.brooks.mall.user.config;

import com.brooks.mall.user.interceptor.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    // 1. 拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/doc.html",
                        "/api/images/**",
                        "/api/webjars/**",
                        "/api/images/**"
                );
    }

    // 2. 静态资源与前端路由兜底配置  -- 配置静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 图片映射（保持你原来的逻辑，注意加上 /api 前缀）
        String path = fileUploadConfig.getUploadPath();
        if (path != null && !path.endsWith("/")) path += "/";
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:" + path);

        // 2. 前端静态资源 + 路由兜底（核心修改）
        registry.addResourceHandler("/api/**") // 拦截所有 /api 开头的请求
                .addResourceLocations("classpath:/static/") // 指向你的 dist 目录
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        // 如果请求的是文件（如 js, css, png），直接返回
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        // 如果是前端路由（如 /api/login），找不到文件，就返回 index.html
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}