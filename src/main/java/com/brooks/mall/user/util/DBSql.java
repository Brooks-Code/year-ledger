package com.brooks.mall.user.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库操作工具类 (基于 HikariCP + 原生 JDBC + 雪花ID自动注入)
 * @Author Brooks Cole
 */
public class DBSql {

    private static final HikariDataSource DATA_SOURCE;
    private static final SnowflakeIdGenerator ID_WORKER = new SnowflakeIdGenerator(1,1);

    // 匹配 INSERT 语句中字段列表的正则（忽略大小写）
    private static final Pattern INSERT_COLUMNS_PATTERN =
            Pattern.compile("INSERT\\s+INTO\\s+\\S+\\s*\\(([^)]+)\\)", Pattern.CASE_INSENSITIVE);

    // ==================== 1. 连接池初始化 ====================
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.get("spring.datasource.url"));
        config.setUsername(ConfigLoader.get("spring.datasource.username"));
        config.setPassword(ConfigLoader.get("spring.datasource.password"));

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);

        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "UTF-8");
        config.addDataSourceProperty("serverTimezone", "Asia/Shanghai");

        DATA_SOURCE = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    // ==================== 2. 增 / 改 (Create & Update) ====================

    /**
     * 执行 INSERT / UPDATE 语句
     * ✅ INSERT 时若 id 字段对应参数为 null，自动生成雪花ID并替换
     */
    public static int update(String sql, Object... params) {
        Object[] finalParams = autoFillSnowflakeId(sql, params);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, finalParams);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL更新失败: " + sql, e);
        }
    }

    /**
     * 批量执行 INSERT / UPDATE（带事务）
     * ✅ 每组参数都会独立检查并填充雪花ID
     */
    public static int[] batchUpdate(String sql, List<Object[]> paramsList) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                for (Object[] params : paramsList) {
                    Object[] finalParams = autoFillSnowflakeId(sql, params);
                    setParameters(ps, finalParams);
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                conn.commit();
                return results;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL批量更新失败: " + sql, e);
        }
    }

    // ==================== 3. 删 (Delete) ====================

    public static int deleteById(String tableName, Long id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        return update(sql, id);
    }

    public static int deleteByIds(String tableName, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "DELETE FROM " + tableName + " WHERE id IN (" + placeholders + ")";
        return update(sql, ids.toArray());
    }

    // ==================== 4. 查 (Read) ====================

    public static List<Map<String, Object>> query(String sql, Object... params) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnLabel(i).toLowerCase(), rs.getObject(i));
                    }
                    dataList.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL查询失败: " + sql, e);
        }
        return dataList;
    }

    public static Map<String, Object> pageQuery(String baseSql, int pageNum, int pageSize, Object... params) {
        Map<String, Object> result = new HashMap<>(4);
        int offset = (pageNum - 1) * pageSize;

        String countSql = baseSql.replaceAll("(?i)^SELECT\\s+.+?\\s+FROM", "SELECT COUNT(*) FROM");
        List<Map<String, Object>> countResult = query(countSql, params);
        long total = ((Number) countResult.get(0).values().iterator().next()).longValue();

        String pageSql = baseSql + " LIMIT ? OFFSET ?";
        Object[] pageParams = Arrays.copyOf(params, params.length + 2);
        pageParams[params.length] = pageSize;
        pageParams[params.length + 1] = offset;
        List<Map<String, Object>> records = query(pageSql, pageParams);

        result.put("total", total);
        result.put("records", records);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }

    // ==================== 5. 雪花ID自动填充核心逻辑 ====================

    /**
     * 智能填充雪花ID：
     * 1. 仅对 INSERT 语句生效
     * 2. 解析字段列表，找到 id 字段的索引位置
     * 3. 若该位置参数为 null，则生成雪花ID替换
     * 4. 若非 INSERT 或无 id 字段，原样返回参数
     */
    private static Object[] autoFillSnowflakeId(String sql, Object... params) {
        if (params == null || params.length == 0) return params;

        Matcher matcher = INSERT_COLUMNS_PATTERN.matcher(sql.trim());
        if (!matcher.find()) return params; // 非 INSERT 语句，直接返回

        String columnsStr = matcher.group(1);
        String[] columns = columnsStr.split(",");

        int idIndex = -1;
        for (int i = 0; i < columns.length; i++) {
            if ("id".equalsIgnoreCase(columns[i].trim())) {
                idIndex = i;
                break;
            }
        }

        // SQL 中没有 id 字段，或参数个数不匹配，不干预
        if (idIndex < 0 || idIndex >= params.length) return params;

        // ⭐️ 核心：仅在参数为 null 时生成雪花ID，已有值则保留
        if (params[idIndex] == null) {
            Object[] newParams = Arrays.copyOf(params, params.length);
            newParams[idIndex] = ID_WORKER.nextId();
            return newParams;
        }

        return params;
    }

    // ==================== 6. 内部工具方法 ====================

    private static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}