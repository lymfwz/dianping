package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-25-22:35
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.203.129:6379")
                .setPassword("123456");
        // 创建
        return Redisson.create(config);
    }
}
