package com.brooks.mall.user.util;

import cn.hutool.core.io.resource.ResourceUtil;

import java.io.IOException;
import java.util.Properties;

/**
 *  @description 配置文件参数获取
 *  @author:Brooks Cole
 *  @date:2022/7/19 16:01
 */
public class ConfigLoader {
    private ConfigLoader() {
    }

    /**
     *  配置加载器
     */
    private static final Properties props = new Properties();

    static {
        // 读取 resources/application.properties
        try {
            props.load(ResourceUtil.getStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("加载配置文件失败", e);
        }
    }

    /**
     * @param key 配置项的键
     * @return
     * @description 获取配置项
     */
    public static String get(String key) {
        return props.getProperty(key);
    }

    /**
     * @param key
     * @return
     * @description 获取配置项，并转换为整数
     */
    public static Integer getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    /**
     * @param key
     * @return
     * @description 获取配置项，并转换为布尔值
     */
    public static Boolean getBoolean(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }
}
