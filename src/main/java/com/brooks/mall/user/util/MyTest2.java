package com.brooks.mall.user.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/5/22 10:08
 */
public class MyTest2 {
    /**
     * 生成随机数
     */
    public static Integer pattern;



    private MyTest2() {
    }





    /**
     * 覆盖指定文件的全部内容
     *
     * @param filePath   文件的完整路径 (必须位于U盘上)
     * @param newContent 新的内容，将完全替换旧内容
     */
    public static void overwriteFileContent(String filePath, String newContent) {
        Path path = Paths.get(filePath);

        // 1. 检查路径是否指向一个文件
        if (!Files.isRegularFile(path)) {
            System.err.println("错误: 指定路径不是一个有效的文件: " + filePath);
            if (Files.isDirectory(path)) {
                System.err.println("提示: 路径指向的是一个目录，而非文件。");
            }
            return;
        }

        // 2. 检查父目录是否存在 (即U盘是否挂载在该路径)
        Path parentDir = path.getParent();
        if (parentDir == null || !Files.exists(parentDir)) {
            System.err.println("错误: 文件的父目录不存在，可能U盘未正确连接或挂载点已改变: " + parentDir);
            return;
        }

        // 3. 执行覆盖写入
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) { // FileWriter默认覆盖模式
            writer.write(newContent);
            System.out.println("文件内容已成功被覆盖: " + filePath);
            System.out.println("--- 写入的新内容如下 ---");
            System.out.println(newContent);
            System.out.println("--- 内容结束 ---");

        } catch (IOException e) {
            System.err.println("写入文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 持续复制图片并重命名
     *
     * @param sourcePath 源图片路径
     * @param targetDir  目标目录
     * @param sum        持续复制的图片数量
     */
    public static void duplicateImageContinuously(String sourcePath, String targetDir, Integer sum) {
        Path src = Paths.get(sourcePath);
        Path destDir = Paths.get(targetDir);

        // 校验源文件和目标目录
        if (!Files.isRegularFile(src)) {
            System.err.println("错误: 源图片不存在: " + sourcePath);
            return;
        }
        if (!Files.isDirectory(destDir)) {
            System.err.println("错误: 目标目录不存在: " + targetDir);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        // 获取源图片的扩展名
        String extension = getExtension(sourcePath);
        int count = 0;
        while (count < sum) {
            try {
                String newName = "img_" + LocalDateTime.now().format(formatter) + "_" + count + extension;
                Path destFile = destDir.resolve(newName);

                Files.copy(src, destFile, StandardCopyOption.REPLACE_EXISTING);
                count++;
                System.out.println("已复制第 " + count + " 个文件: " + newName);

                // 可选：添加短暂延迟避免过快写入损伤U盘
                Thread.sleep(10);
            } catch (IOException e) {
                System.err.println("复制失败: " + e.getMessage());
                break;
            } catch (InterruptedException e) {
                System.out.println("复制任务被中断，共复制 " + count + " 个文件");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filePath
     * @return
     */
    private static String getExtension(String filePath) {
        // 获取文件名
        int dotIndex = filePath.lastIndexOf('.');
        return dotIndex >= 0 ? filePath.substring(dotIndex) : ".jpg";
    }



    /**
     * 反射机制
     */
    private static void myFanshe() {
        try {
            Class<MyTest2> myTest2Class = MyTest2.class;
            //创建 MyTest2 类的实例
            //MyTest2 myTest2 = myTest2Class.newInstance();
            //使用构造函数反射
            Constructor<MyTest2> declaredConstructor = myTest2Class.getDeclaredConstructor();
            //获取声明的 getSql 方法（即使为 private）
            Method getSql = myTest2Class.getDeclaredMethod("getSql");
            //设置方法可访问，以绕过 private 修饰符的限制
            getSql.setAccessible(true);
            //调用该方法并执行其逻辑
            Object invoke = getSql.invoke(declaredConstructor);
            System.out.println(invoke);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
