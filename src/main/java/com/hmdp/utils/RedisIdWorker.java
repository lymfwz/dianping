package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-23-22:24
 */
@Component
public class RedisIdWorker {

    private static final long BEGIN_TIMESTAMP = 1640995200L;
    private static final int COUNT_BITS = 32;

    private StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成全局唯一 ID
     * @param prefixKey
     * @return
     */
    public long nextId(String prefixKey) {
        // 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;

        // 生成序列号
        String timeFormat = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long increment = stringRedisTemplate.opsForValue().increment("incr:" + prefixKey + ":" + timeFormat);

        return timeStamp << COUNT_BITS | increment;
    }

/*    public static void main(String[] args) {
        // 设置起始时间
        long start = LocalDateTime.of(2022, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long cur = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        System.out.println(cur);
        System.out.println(start);
        // System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }*/
}
