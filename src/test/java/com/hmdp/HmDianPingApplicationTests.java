package com.hmdp;

import cn.hutool.cache.Cache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.entity.User;
import com.hmdp.service.IShopService;
import com.hmdp.service.IUserService;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@SpringBootTest
class HmDianPingApplicationTests {

    @Autowired
    private ShopServiceImpl shopService;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Autowired
    private IUserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void hotKeyBuild() {
        // 将热点key放入缓存
        Shop shop = shopService.getById(1L);
        cacheClient.setWithLogicExpire(CACHE_SHOP_KEY + shop.getId(), shop, 20L, TimeUnit.SECONDS);
    }

    private ExecutorService es = Executors.newFixedThreadPool(500);


    @Test
    void testRedisIdWorker() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println(id);
            }
            countDownLatch.countDown();
        };
        long begin = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        countDownLatch.await();
        long end = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        System.out.println("cost time : " + (end - begin));
    }

    @Test
    void createTokens() throws IOException {
        Long p = 17802906832L;
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\java\\redis\\tokens.txt", false)));
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setPhone(p + i + "");
            user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
            userService.save(user);
            // 6. 保存用户信息到 Redis 中 (DTO == > 删减不必要用户信息)
            String token = UUID.randomUUID().toString(true);
            String tokenKey = LOGIN_USER_KEY + token;
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            // ==更改后的代码==
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,
                    new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));


            // 这里有一个问题，就是上面的 userMap 生成默认是什么字段就转到map也是什么字段，如 Long，但是redis里面要求string字段才行
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            // token对应的用户信息设置一个过期时间
            stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
            bufferedWriter.write(token + "\n");
        }
        bufferedWriter.close();
    }

    @Test
    void createTokensWithUsers() throws IOException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(User::getId, 1010);
        List<User> list = userService.list(queryWrapper);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("D:\\java\\redis\\tokens.txt", false)
        ));
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            String token = UUID.randomUUID().toString(true);
            String tokenKey = LOGIN_USER_KEY + token;
            // ==更改后的代码==
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,
                    new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));


            // 这里有一个问题，就是上面的 userMap 生成默认是什么字段就转到map也是什么字段，如 Long，但是redis里面要求string字段才行
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            // token对应的用户信息设置一个过期时间
            stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.HOURS);
            if (i < list.size() - 1) {
                bufferedWriter.write(token + "\n");
            } else bufferedWriter.write(token);
        }
        bufferedWriter.close();
    }

    @Test
    void twst() {

    }
}
