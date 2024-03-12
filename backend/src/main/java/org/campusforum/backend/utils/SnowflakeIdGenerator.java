package org.campusforum.backend.utils;

import org.springframework.stereotype.Component;

/**
 * 雪花ID生成器
 * @author ChangxueDeng
 *
 */
@Component
public class SnowflakeIdGenerator {
    // 起始的时间戳，可以设置为项目开始的时间
    private static final long START_TIMESTAMP = 1709543693757L;

    // 每部分占用的位数
    private static final long DATA_CENTER_ID_BITS = 5L; // 数据中心ID占用的位数
    private static final long WORKER_ID_BITS = 5L;      // 工作机器ID占用的位数
    private static final long SEQUENCE_BITS = 12L;      // 序列号占用的位数

    // 每部分的最大值
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 每部分向左的位移
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    // 数据中心ID和工作机器ID
    private final long dataCenterId;
    private final long workerId;

    // 上次生成ID的时间戳和序列号
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    // 默认构造函数，默认使用数据中心ID为1，工作机器ID为1
    public SnowflakeIdGenerator(){
        this(1, 1);
    }

    // 带参构造函数，传入数据中心ID和工作机器ID
    private SnowflakeIdGenerator(long dataCenterId, long workerId) {
        // 检查数据中心ID和工作机器ID是否合法
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    /**
     * 生成一个新的雪花算法ID（加锁保证线程安全）
     * @return 雪花ID
     */
    public synchronized long getNextId() {
        // 获取当前时间戳
        long timestamp = getCurrentTimestamp();
        // 检查时钟是否后退
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
        }
        // 如果是同一毫秒内的生成，则递增序列号
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号达到最大值时，等待下一毫秒再生成
            if (sequence == 0) {
                timestamp = getNextTimestamp(lastTimestamp);
            }
        } else {
            // 不是同一毫秒，则序列号重置为0
            sequence = 0L;
        }
        lastTimestamp = timestamp;

        // 组合各部分生成ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) |
                (dataCenterId << DATA_CENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    // 获取当前时间戳
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    // 获取下一个时间戳，直到大于上次生成ID的时间戳
    private long getNextTimestamp(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    //单例模式
    private static SnowflakeIdGenerator snowUtils = null;

    static {
        snowUtils = new SnowflakeIdGenerator();
    }

    public static synchronized long nextId(){
        return snowUtils.getNextId();
    }

}
