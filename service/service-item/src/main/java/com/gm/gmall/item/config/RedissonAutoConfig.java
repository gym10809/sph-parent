package com.gm.gmall.item.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * @author gym
 * @create 2022/8/30 0030 14:17
 */
@AutoConfigureAfter(RedisConfiguration.class)
@Configuration
public class RedissonAutoConfig {
    @Autowired
    RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient(){
        Config config=new Config();
        config.useSingleServer().setAddress("redis://"+redisProperties.getHost()+":"+redisProperties.getPort())
                .setPassword("gm123456");
        RedissonClient redisson= Redisson.create(config);

        return redisson;

    }
}
