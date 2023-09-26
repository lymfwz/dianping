package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hmdp.entity.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hmdp.utils.RedisConstants.*;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-22-15:15
 * @Description: 解决缓存问题的抽取公共代码
 */
@Slf4j
@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 设置string类型数据写入redis方法
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicExpire(String key, Object value, Long time, TimeUnit unit) {
        // 逻辑过期使用一个封装类RedisData
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    // 缓存穿透 ==> 缓存NULL值
    public <ID, R > R queryWithPassThrough(String prefixKey, ID id, Class<R> type,
                                           Function<ID, R> dbFallBack,
                                           Long time, TimeUnit unit) {
        String key = prefixKey + id;
        // 1. 查缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        // 2. 存在缓存
        if (StringUtils.isNotBlank(jsonStr)) {
            return JSONUtil.toBean(jsonStr, type);
        }


        // ""空字符串StringUtils判断isBlank == true
        if (jsonStr != null) {
            return null; // 店铺不存在
        }


        // 3. 不存在缓存，查数据库
        R r = dbFallBack.apply(id);

        // 4. 不存在
        if (r == null) {
            // 缓存null值
            // stringRedisTemplate.opsForValue().set(key, "", time, unit);
            // ==> 等价
            this.set(key, "", time, unit);

            return null; // 店铺不存在
        }
        // 5. 存在，需要写入缓存
        this.set(key, JSONUtil.toJsonStr(r), time, unit);
        // stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return r;
    }

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


    // 解决缓存击穿问题，采用逻辑过期方法
    public <ID, R> R queryWithLogicExpire(String prefixKey, ID id, Class<R> type,
                                          String prefixLockKey, Long time, TimeUnit unit,
                                          Function<ID, R> dbFallBack) {
        String key = prefixKey + id;
        // 1. 从redis查询缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        // 2. 如果不存在直接返回，因为不是热点key
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        // 3. 查询是否过期
        RedisData redisData = JSONUtil.toBean(jsonStr, RedisData.class);


        JSONObject data = (JSONObject) redisData.getData();
        R r = JSONUtil.toBean(data, type);


        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期，直接返回
            return r;
        }
        // 4. 过期
        // 4.1 首先要获取互斥锁
        Boolean isLock = getLock(prefixLockKey, id, time, unit);
        if (isLock) {
            // 4.2 获取锁成功，开启独立线程
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                        try {
                            // 重建缓存
                            // 查数据库 ==> 谁查谁给我结果
                            R r1 = dbFallBack.apply(id);

                            // 写入redis ==> 进一步封装
                            this.setWithLogicExpire(key, r1, time, unit);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            // 释放锁
                            unLock(prefixLockKey, id);
                        }
                    }
            );
        }


        // 4.2 返回数据 ==> 无论是否可以获取锁重建数据，都直接返回shop
        return r;
    }

    private <ID> Boolean getLock(String prefixLockKey, ID id, Long time, TimeUnit unit) {
        String lockKey = prefixLockKey + id;
        // 设置锁，过期时间
        Boolean getLock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", time, unit);
        return BooleanUtil.isTrue(getLock);
    }

    private <ID> void unLock(String prefixLockKey, ID id) {
        String lockKey = prefixLockKey + id;
        stringRedisTemplate.delete(lockKey);
    }
}
