package com.brooks.mall.user.util;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import static com.brooks.mall.user.util.MyJDBCSql.getConnection;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/22 13:36
 */
public class DBApi {
    private DBApi() {
    }
    // 雪花ID生成器
    private static final SnowflakeIdGenerator ID_GENERATOR = new SnowflakeIdGenerator(1, 1);
    // 正则表达式-校验非法字符
    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_.]*$");

    /**
     * 新增数据
     * @param tableName 表名
     * @uid 用户ID
     * @param dataMap 业务数据
     * @return 插入数据条数
     */
    public static int create(String tableName, String uid, Map<String, Object> dataMap) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (!SAFE_IDENTIFIER.matcher(tableName.trim()).matches()) {
            throw new IllegalArgumentException("表名包含非法字符: " + tableName);
        }
        if (dataMap == null || dataMap.isEmpty()) {
            throw new IllegalArgumentException("插入数据不能为空");
        }

        // 1. 组装完整字段列表与参数列表（系统字段 + 业务字段一次性完成）
        List<String> columns = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // 强制注入 id
        columns.add("id");
        params.add(ID_GENERATOR.nextId());

        // 按需注入时间字段
        Timestamp now = new Timestamp(System.currentTimeMillis());
        boolean hasCreatedAt = dataMap.containsKey("created_at");
        boolean hasUpdatedAt = dataMap.containsKey("updated_at");
        if (!hasCreatedAt) {
            columns.add("created_at");
            params.add(now);
        }
        if (!hasUpdatedAt) {
            columns.add("updated_at");
            params.add(now);
        }

        // 注入业务字段（LinkedHashMap 保证顺序稳定）
        for (Map.Entry<String, Object> entry : new LinkedHashMap<>(dataMap).entrySet()) {
            String key = entry.getKey();
            if (key == null || key.trim().isEmpty()) continue;
            if (!SAFE_IDENTIFIER.matcher(key.trim()).matches()) {
                throw new IllegalArgumentException("字段名包含非法字符: " + key);
            }
            columns.add(key.trim());
            params.add(entry.getValue());
        }

        // 补充操作人（仅当业务未传入时）
        if (!dataMap.containsKey("created_by")) {
            columns.add("created_by");
            params.add(uid);
        }
        if (!dataMap.containsKey("updated_by")) {
            columns.add("updated_by");
            params.add(uid);
        }

        if (columns.size() <= 3 && !hasCreatedAt && !hasUpdatedAt) {
            throw new IllegalArgumentException("没有有效的业务字段可以插入");
        }

        // 2. 构建 SQL
        StringJoiner colJoiner = new StringJoiner(", ", "(", ")");
        StringJoiner valJoiner = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < columns.size(); i++) {
            colJoiner.add(columns.get(i));
            valJoiner.add("?");
        }
        String sql = "INSERT INTO " + tableName.trim() + " " + colJoiner + " VALUES " + valJoiner;

        // 3. 执行
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                Object val = params.get(i);
                if (val == null) {
                    stmt.setNull(i + 1, Types.NULL);
                } else {
                    stmt.setObject(i + 1, val);
                }
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("数据库新增失败, SQL: " + sql, e);
        }
    }






    /**
     * 安全关闭资源，支持 null 传入，不抛出异常
     */
    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // 静默处理，避免掩盖 try 块中的原始异常
            }
        }
    }
}
