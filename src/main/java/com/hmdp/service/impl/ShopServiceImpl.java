package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    @Override
    public Result queryById(Long id) {
        // 缓存穿透解决方案 ==> 缓存null值
        // Shop shop = queryWithPassThrough(id);


        Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class,
                this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);


        // 缓存击穿解决 ==> 互斥锁
//        Shop shop = queryWithMutex(id);
//        if (shop == null) {
//            return Result.fail("店铺不存在");
//        }

        // 缓存击穿解决 ==> 逻辑过期 （热点key，不考虑缓存未命中问题）
         // Shop shop = queryWithLogicExpire(id);

//        Shop shop = cacheClient.queryWithLogicExpire(CACHE_SHOP_KEY, id, Shop.class,
//                LOCK_SHOP_KEY, LOCK_SHOP_TTL, TimeUnit.MINUTES,
//                this::getById);
        if (shop == null) {
            return Result.fail("店铺不存在");
        }

        return Result.ok(shop);
    }

/*    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    private Shop queryWithLogicExpire(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1. 从redis查询缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        // 2. 如果不存在直接返回，因为不是热点key
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        // 3. 查询是否过期
        RedisData redisData = JSONUtil.toBean(jsonStr, RedisData.class);


        JSONObject data = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(data, Shop.class);


        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期，直接返回
            return shop;
        }
        // 4. 过期
        // 4.1 首先要获取互斥锁
        Boolean isLock = getLock(id);
        if (isLock) {
            // 4.2 获取锁成功，开启独立线程
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                        try {
                            // 重建缓存
                            this.saveShop2Redis(id, 20L);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            // 释放锁
                            unLock(id);
                        }
                    }
            );
        }


        // 4.2 返回数据 ==> 无论是否可以获取锁重建数据，都直接返回shop
        return shop;
    }

    private void saveShop2Redis(Long id, Long expireTime) throws InterruptedException {
        // 1. 查询数据库
        Shop shop = getById(id);
        Thread.sleep(200);
        // 2. 封装
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireTime));
        // 3. 写入 redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }*/

    private Shop queryWithMutex(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1. 查询缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        // 2. 缓存非空
        if (StringUtils.isNotBlank(jsonStr)) {
            Shop shop = JSONUtil.toBean(jsonStr, Shop.class);
            return shop;
        }
        // 3. 缓存非空 ==> 但是”“空字符串 (缓存穿透解决)
        if (jsonStr != null) {
            return null;
        }
        // 4. 缓存空，需要重建数据，涉及对数据库操作，可能存在大量热点key ==> 缓存击穿问题
        // 4.1 获取锁
        Shop shop = null;
        try {
            boolean lock = getLock(id);
            if (!lock) { // 没获取到
                Thread.sleep(50); // 休眠，重新获取锁

                // 这里必须return，否则无效了
                return queryWithMutex(id);
            }
            // 4.2 获取到锁
            // 4.2.1 查询数据库
            // 模拟重建
            Thread.sleep(200);
            shop = getById(id);
            // 4.2.2 查询数据为空
            if (shop == null) {
                // 缓存 NULL 值
                stringRedisTemplate.opsForValue().set(key, "", CACHE_SHOP_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 4.2.3 查询数据不为空
            // 缓存数据库数据到缓存
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 5. 释放锁
            unLock(id);
        }

        return shop;
    }

    public Boolean getLock(Long id) {
        String lockKey = LOCK_SHOP_KEY + id;
        // 设置锁，过期时间
        Boolean getLock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(getLock);
    }

    public void unLock(Long id) {
        String lockKey = LOCK_SHOP_KEY + id;
        stringRedisTemplate.delete(lockKey);
    }


    /*@Deprecated
    private Shop queryWithPassThrough(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1. 查缓存
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        // 2. 存在缓存
        if (StringUtils.isNotBlank(jsonStr)) {
            Shop shop = JSONUtil.toBean(jsonStr, Shop.class);
            return shop;
        }


        // ""空字符串StringUtils判断isBlank == true
        if (jsonStr != null) {
            return null; // 店铺不存在
        }


        // 3. 不存在缓存，查数据库
        Shop shop = getById(id);
        // 4. 不存在
        if (shop == null) {
            // 缓存null值
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null; // 店铺不存在
        }
        // 5. 存在，需要写入缓存
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return shop;
    }*/

    @Override
    public Result updateShop(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("更新商铺失败");
        }
        // 1. 写入数据库
        updateById(shop);

        // 2. 删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }
}
