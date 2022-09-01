package com.gm.gmall.item;
import com.gm.gmall.common.config.RedissonAutoConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author gym
 * @create 2022/8/29 0029 22:07
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonAutoConfig redissonAutoConfig;
    @Test
    public void test(){

        redisTemplate.opsForValue().setBit("skuInfo:bitMap:"+49,1,true);

        Boolean bit = redisTemplate.opsForValue().getBit("skuInfo:bitMap:"+50, 0);
        System.out.println(bit);
    }
    @Test
    public void test1(){
        RedissonClient redissonClient = redissonAutoConfig.redissonClient();
        RLock lock = redissonClient.getLock("s1");

        lock.lock();
        System.out.println(11111);
        lock.unlock();
    }
    
    @Test
    public void test2() {

    }
}
