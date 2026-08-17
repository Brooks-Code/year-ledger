package com.brooks.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// 开启定时任务支持
@EnableScheduling
public class MallApplication {
    public static void main(String[] args) {
        // 2. 启动 Spring Boot 应用
        SpringApplication.run(MallApplication.class, args);
    }
}