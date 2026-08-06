package com.brooks.mall.user.functional;

import com.brooks.mall.MallApplication;
import com.brooks.mall.user.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/2 13:39
 */
@SpringBootTest
public class TokenUtil {

    @Autowired
    private JwtUtil jwtUtil;
    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(872764824453517312L, null);
        System.out.println(token);

    }

    /**
     * 测试生成 Token
     * (原始方法)
     */
    private static void ysbf(String[] args) {
        // 1. 启动 Spring 容器 (假设你的主启动类是 MallUserApplication)
        // 注意：如果 TokenUtil 就在主启动类里，直接用 args 即可；如果是单独的类，可能需要指定主类
        ConfigurableApplicationContext context = SpringApplication.run(MallApplication.class, args);

        try {
            // 2. 从容器中手动获取 JwtUtil 的 Bean
            JwtUtil jwtUtil = context.getBean(JwtUtil.class);

            // 3. 现在 jwtUtil 已经被 Spring 初始化过了，secret 也有值了
            String token = jwtUtil.generateToken(872764824453517312L, null);
            System.out.println("生成的 Token: " + token);
        } finally {
            // 4. 测试完关闭容器
            context.close();
        }
    }
}