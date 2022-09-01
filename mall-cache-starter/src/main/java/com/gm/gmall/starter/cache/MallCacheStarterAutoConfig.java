package com.gm.gmall.starter.cache;





import com.gm.gmall.starter.cache.aspect.CacheAspect;
import com.gm.gmall.starter.cache.service.CacheService;
import com.gm.gmall.starter.cache.service.impl.CacheServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author gym
 * @create 2022/9/2 0002 0:42
 */
@EnableAspectJAutoProxy
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class MallCacheStarterAutoConfig {
    @Autowired
    RedisProperties redisProperties;
    @Bean
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }

    @Bean
    public CacheService cacheService(){
        return new CacheServiceImpl();
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config=new Config();
        config.useSingleServer().setAddress("redis://"+redisProperties.getHost()+":"+redisProperties.getPort())
                .setPassword("gm123456");
        RedissonClient redisson= Redisson.create(config);

        return redisson;
    }
}
