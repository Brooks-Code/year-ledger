package com.brooks.mall.user.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    // 使用 ThreadLocalRandom 保证线程安全和高性能
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * @description 获取随机数
     * @param a   开始数字
     * @param b   结束数字
     * @param sum 随机数生成数量
     */
    public static List<Integer> getRandom(int a, int b, int sum) {
        int i = 0;
        List<Integer> list = new ArrayList<>();
        while (i < sum) {
            Random rand = new Random();
            int randomThreeDigit = rand.nextInt(b - a + 1) + a;
            list.add(randomThreeDigit);
            i++;
        }
        return list;
    }
}