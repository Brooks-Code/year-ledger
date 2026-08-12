package com.brooks.mall.user.functional;


import java.util.Scanner;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/12 13:55
 */
public class GadgetUtil {
    public static void main(String[] args) {

    }

    /**
     * 获取 Scanner 对象
     * 作用：获取用户输入
     */
    private static void getScanner() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要执行的命令：\n");
        int input = scanner.nextInt();
        int output = scanner.nextInt();
        int a = input + output;
        System.out.print("执行结果："+ a);
        // 关闭 Scanner
        scanner.close();
    }
}
