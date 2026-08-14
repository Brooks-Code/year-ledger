package com.brooks.mall.user;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/10 17:13
 */
public class Ar {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        System.out.println("数组中所有元素的和为：" + sum);
    }
}
