package com.brooks.mall.user.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 雪花算法 UUID 生成器
 * <p>
 * 结构: 0 | timestamp(41) | datacenterId(5) | workerId(5) | sequence(12)
 * 总长: 64 bit (long)
 * @author: Brooks Cole
 * @date: 2026/07/23 10:01
 */
public class SnowflakeIdGenerator {

    // ==================== 位分配常量 ====================
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS     = 5L;
    private static final long SEQUENCE_BITS      = 12L;

    // ==================== 最大值 ====================
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31
    private static final long MAX_WORKER_ID     = ~(-1L << WORKER_ID_BITS);     // 31
    private static final long SEQUENCE_MASK     = ~(-1L << SEQUENCE_BITS);      // 4095

    // ==================== 位移偏移 ====================
    private static final long WORKER_ID_SHIFT     = SEQUENCE_BITS;                          // 12
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;         // 17
    private static final long TIMESTAMP_SHIFT     = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS; // 22

    // ==================== 起始时间戳 (2020-01-01 00:00:00 UTC) ====================
    private static final long EPOCH = 1577836800000L;

    // ==================== 实例字段 ====================
    private final long datacenterId;
    private final long workerId;

    private long sequence         = 0L;
    private long lastTimestamp    = -1L;

    // ==================== 时间转换 ====================
    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * @param datacenterId 数据中心ID [0, 31]
     * @param workerId     机器ID [0, 31]
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException(
                "datacenterId must be between 0 and " + MAX_DATACENTER_ID);
        }
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                "workerId must be between 0 and " + MAX_WORKER_ID);
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 线程安全地获取下一个雪花 ID
     */
    public synchronized long nextId() {
        long currentTimestamp = currentTimeMillis();

        // 时钟回拨保护
        if (currentTimestamp < lastTimestamp) {
            long offset = lastTimestamp - currentTimestamp;
            if (offset <= 5) {
                // 回拨 ≤5ms，短暂等待追赶
                try {
                    Thread.sleep(offset << 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during clock drift wait", e);
                }
                currentTimestamp = currentTimeMillis();
                if (currentTimestamp < lastTimestamp) {
                    throw new IllegalStateException(
                        "Clock moved backwards. Refusing to generate id for " 
                        + (lastTimestamp - currentTimestamp) + "ms");
                }
            } else {
                throw new IllegalStateException(
                    "Clock moved backwards. Refusing to generate id for " 
                    + offset + "ms");
            }
        }

        // 同一毫秒内序列号递增；新毫秒则重置
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒序列耗尽，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)

             | (datacenterId << DATACENTER_ID_SHIFT)
             | (workerId << WORKER_ID_SHIFT)
             | sequence;
    }

    /**
     * 解析雪花 ID 的各组成部分
     * @param id 雪花 ID
     * @return 解析结果（时间戳、数据中心ID、机器ID、序列号）
     */
    public static IdParts parse(long id) {
        long timestamp    = (id >> TIMESTAMP_SHIFT) + EPOCH;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long workerId     = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence     = id & SEQUENCE_MASK;
        return new IdParts(timestamp, datacenterId, workerId, sequence);
    }

    // ==================== 内部方法 ====================

    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private long waitNextMillis(long lastTs) {
        long ts = currentTimeMillis();
        while (ts <= lastTs) {
            ts = currentTimeMillis();
        }
        return ts;
    }

    // ==================== 解析结果 DTO ====================
    public static class IdParts {
        private final long timestamp;
        private final long datacenterId;
        private final long workerId;
        private final long sequence;

        IdParts(long timestamp, long datacenterId, long workerId, long sequence) {
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        public long getTimestamp()    { return timestamp; }
        public long getDatacenterId() { return datacenterId; }
        public long getWorkerId()     { return workerId; }
        public long getSequence()     { return sequence; }

        @Override
        public String toString() {
            return "IdParts{timestamp=" + timestamp
                 + ", datacenterId=" + datacenterId
                 + ", workerId=" + workerId
                 + ", sequence=" + sequence + "}";
        }
    }

    /**
     * 获取格式化的北京时间字符串
     */
    public static String getFormattedTime(long id) {
        IdParts parts = parse(id);
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(parts.getTimestamp()), ZONE_SHANGHAI);
        return dateTime.format(FORMATTER);
    }

    /**
     * 便捷获取时间戳
     */
    public static long getTimestamp(long id) {
        return parse(id).getTimestamp();
    }
}