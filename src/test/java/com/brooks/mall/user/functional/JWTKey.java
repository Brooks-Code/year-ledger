package com.brooks.mall.user.functional;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/2 13:39
 */
public class JWTKey {
    public static void main(String[] args) {


        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256, new SecureRandom());
            String secret = Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
            System.out.println(secret);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
