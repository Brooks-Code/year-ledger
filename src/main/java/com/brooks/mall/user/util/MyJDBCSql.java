package com.brooks.mall.user.util;

import java.sql.*;
import java.util.*;

/**
 * TODO
 * @deprecated
 * @ClassName DBSql
 * @Author Brooks Cole
 * @Date 2026/7/24 14:36
 */
public class MyJDBCSql {

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        String url = ConfigLoader.get("spring.datasource.url");
        String username = ConfigLoader.get("spring.datasource.username");
        String password = ConfigLoader.get("spring.datasource.password");

        // 1. 使用 Properties 统一封装连接参数
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);

        // 2. 设置字符集和时区，防止中文乱码和时间差
        props.setProperty("useUnicode", "true");
        props.setProperty("characterEncoding", "UTF-8");
        props.setProperty("serverTimezone", "Asia/Shanghai");

        // 3. 使用 Class.forName 动态加载驱动，兼容 MySQL 5.x 和 8.x
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 8.x 推荐
        } catch (ClassNotFoundException e) {
            // 如果 8.x 驱动不存在，尝试加载 5.x 驱动
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new SQLException("未找到 MySQL JDBC 驱动，请检查 classpath", ex);
            }
        }

        // 4. 使用 DriverManager 获取连接（标准做法）
        return DriverManager.getConnection(url, props);
    }

    /**
     * 支持参数的更新方法
     */
    public static int update(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // 动态设置参数
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL执行失败: " + sql, e);
        }
    }


    /**
     * JDBC 查询数据库（返回 List<Map>）
     */
    public static List<Map<String, Object>> getSql(String getSql) {
        List<Map<String, Object>> dataList = new ArrayList<>();

        // 1. 将 ResultSet 也放入 try-with-resources 中，确保它被自动关闭
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(getSql);
             ResultSet resultSet = statement.executeQuery()) {

            // 2. 将元数据（MetaData）的获取移到循环外面
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 3. 循环内部只负责取值，避免重复获取元数据
            while (resultSet.next()) {
                Map<String, Object> dataMap = new HashMap<>(columnCount); // 指定初始容量，减少扩容开销
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i); // 使用 getObject 保留原始数据类型
                    dataMap.put(columnName, columnValue);
                }
                dataList.add(dataMap);
            }

        } catch (SQLException e) {
            // 5. 优化异常信息，带上 SQL 方便排查
            throw new RuntimeException("SQL查询失败: " + getSql, e);
        }
        return dataList;
    }
}
