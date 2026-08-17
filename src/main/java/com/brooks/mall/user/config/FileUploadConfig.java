package com.brooks.mall.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.access-prefix}")
    private String accessPrefix;

    // Getter 方法
    public String getUploadPath() {
        return uploadPath;
    }

    public String getAccessPrefix() {
        return accessPrefix;
    }
}