package com.hmdp.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import com.hmdp.service.ILock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-25-20:48
 */
public class SimpleRedisLock implements ILock {

    private static final String PREFIX_KEY = "lock:"; // 每个人的锁前缀

    private static String name; // 特定字段，如用户id
    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";
    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean tryLock(long timeout, TimeUnit unit) {
        // 获取当前线程
        String thread = ID_PREFIX + Thread.currentThread().getId();
        // set nx
        Boolean isLock = stringRedisTemplate.opsForValue()
                .setIfAbsent(PREFIX_KEY + name, thread, timeout, unit);

        // 使用工具类返回是否获取成功
        return BooleanUtil.isTrue(isLock);
    }

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }
    @Override // 使用Lua脚本
    public void unLock() {
        // 调用脚本
        stringRedisTemplate.execute(UNLOCK_SCRIPT,
                Collections.singletonList(PREFIX_KEY + name),
                ID_PREFIX + Thread.currentThread().getId());
    }

/*    @Override
    public void unLock() {
        // 获取 当前线程要释放的锁的 value
        String value = ID_PREFIX + Thread.currentThread().getId();
        // 获取 redis中的 ==> 比对value
        String v1 = stringRedisTemplate.opsForValue()
                .get(PREFIX_KEY + name);// key : order:lock:userId / value : uu + threadId
        if (value.equals(v1)) { // 确认是自己的锁
            // 释放锁
            stringRedisTemplate.delete(PREFIX_KEY + name);
        }
    }*/
}
