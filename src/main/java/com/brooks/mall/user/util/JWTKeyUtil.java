package com.brooks.mall.user.util;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * TODO
 * 创建密钥生成器，并设置密钥算法名称为 HmacSHA256
 * @Author mr.yang
 * @Date 2026/8/2 13:39
 */
public class JWTKeyUtil {

     // 私有构造函数，防止实例化
    private JWTKeyUtil() {
    }

    // 密钥算法名称
    private static final String HMAC_SHA256_ALGO = "HmacSHA256";

    public static String getSecret() {
        String secret = "";
        try {
            // 创建密钥生成器,参数：HmacSHA256为密钥算法名称
            KeyGenerator keyGen = KeyGenerator.getInstance(HMAC_SHA256_ALGO);
            // 初始化密钥生成器，密钥长度为 256 位，随机数源为 SecureRandom
            keyGen.init(256, new SecureRandom());
            // 生成密钥
            secret = Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return secret;
    }

}
