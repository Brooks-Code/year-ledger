package com.brooks.mall.user.util;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author: Brooks Cole
 * @date: 2026/07/08
 * @description: 智普API工具类
 */
public class ZhiPuAPIUtil {
    private ZhiPuAPIUtil() {

    }

    // API URL
    private static final String API_URL = ConfigLoader.get("zhipu.api.url");
    // 模型名称
    private static final String MODEL_NAME = ConfigLoader.get("zhipu.model");
    // 硬编码仅用于测试，生产环境请通过环境变量注入
    private static final String ZHIPU_API_KEY = ConfigLoader.get("zhipu.api.key");
    // 请求超时时间
    private static final int TIMEOUT_MILLIS = ConfigLoader.getInt("zhipu.timeout"); // 60秒超时

    /**
     * @author: Brooks Cole
     * @date: 2026/07/08
     * @description: 智普API大模型调用
     * @param: filePath 文件名路径 --只识别.txt/.docx
     * @prompt: 提示词
     */
    public static String ZhiPuWithHuToolHttp(String filePath, String prompt) {
        JSONObject jsonObj = new JSONObject();
        String apiKey = ZHIPU_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("错误：未设置API密钥");
            //jsonObj.put(Result.RESULT, false);
            return jsonObj.toString();
        }
        //fileName = "D:\\ActionSoft\\workspace\\myPrompt.txt"; // .txt 类型  ---所在路径
        //fileName = "D:\\ActionSoft\\workspace\\w27产销平衡委员会会议纪要.docx"; // .docx 类型 ---所在路径

        // 读取本地 prompt 文件内容，支持 .txt 和 word文档
        String rawText = null;
        //错误提示
        IOException lastException = null;


        try {
            //文件
            File file = new File(filePath);
            // 先尝试读取 .txt 文件
            if (file.exists() && !filePath.endsWith(".docx")) {
                try {
                    rawText = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
                    System.out.println("✅ 成功读取文本文件: " + filePath);
                } catch (IOException e) {
                    lastException = e;
                }
            }
            // 如果没找到 .txt，尝试 .docx
            if (rawText == null) {
                if (file.exists()) {
                    try {
                        // 读取 Word 文件
                        rawText = readDocxFile(filePath);
                        System.out.println("✅ 成功读取 Word 文件: " + filePath);
                    } catch (IOException e) {
                        lastException = e;
                    }
                }
            }
            // 都失败则报错
            if (rawText == null) {
                System.err.println("❌ 所有候选文件均读取失败：.txt 和 .docx");
                if (lastException != null) {
                    System.err.println("最后错误: " + lastException.getMessage());
                }
                //jsonObj.put(Result.RESULT, false);
                //jsonObj.put(Result.MSG, "最后错误: " + lastException.getMessage());
                return jsonObj.toString();
            }
            // 提示词 + 文本
            String instruction = prompt + rawText;

            // 设置消息
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.set("role", "user");
            message.set("content", instruction);
            messages.add(message);

            // 构造请求体，强制返回 JSON
            JSONObject body = new JSONObject();
            body.set("model", MODEL_NAME);
            body.set("messages", messages);
            //要求API返回 JSON 格式响应,便于后续解析
            body.set("response_format", JSONUtil.createObj().set("type", "json_object")); // 关键！
            // 🔍 调试：打印请求体
            System.out.println("请求体: " + body.toStringPretty());
            HttpResponse response = null;
            try {
                response = HttpRequest.post(API_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .body(body.toString()) // 确保 toString 正确
                        .timeout(TIMEOUT_MILLIS)
                        .execute();

                // 检查状态码
                if (!response.isOk()) {
                    System.err.println("HTTP请求失败，状态码: " + response.getStatus());
                    System.err.println("响应内容: " + response.body());
                    //jsonObj.put(Result.RESULT, response.getStatus());
                    //jsonObj.put(Result.MSG, "响应内容: " + response.body());
                    return jsonObj.toString();
                }

                // 解析响应
                JSONObject result = JSONUtil.parseObj(response.body());

                if (result.containsKey("error")) {
                    System.err.println("API返回错误: " + result.get("error"));
                    //jsonObj.put(Result.RESULT, false);
                    //jsonObj .put(Result.MSG, "API返回错误: " + result.get("error"));
                    return jsonObj.toString();
                }

                if (!result.containsKey("choices") || result.getJSONArray("choices").isEmpty()) {
                    System.err.println("响应中缺少有效回复内容");
                    //jsonObj.put(Result.RESULT, false);
                    //jsonObj.put(Result.MSG, "响应中缺少有效回复内容");
                    return jsonObj.toString();
                }

                JSONObject choice = result.getJSONArray("choices").getJSONObject(0);
                Object contentObj = choice.getByPath("message.content");
                if (!(contentObj instanceof String) || ((String) contentObj).isEmpty()) {
                    System.err.println("未能提取有效回答内容");
                    //jsonObj.put(Result.RESULT, false);
                    //jsonObj.put(Result.MSG, "未能提取有效回答内容");
                    return jsonObj.toString();
                }

                String content = (String) contentObj;
                //jsonObj.put(Result.RESULT, true);
                //jsonObj.put(Result.DATA, content);

            } catch (HttpException e) {
                System.err.println("网络请求异常: " + e.getMessage());
                e.printStackTrace();//打印异常栈
            } catch (Exception e) {
                System.err.println("未知异常: " + e.getMessage());
                e.printStackTrace();//打印异常栈
            } finally {
                if (response != null) {
                    // 关闭响应
                    response.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj.toString();
    }

    /**
     * @author: Brooks Cole
     * @date: 2026/07/08
     * @description: 读取 .docx 文件
     */
    public static String readDocxFile(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String paragraphText = paragraph.getText();
                if (paragraphText != null) {
                    text.append(paragraphText);
                }
                text.append("\n");
            }
            return text.toString();
        }
    }

}
