package com.gm.gmall.starter.cache.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gm.gmall.starter.cache.config.constant.RedisConstant;
import com.gm.gmall.starter.cache.config.utils.Jsons;
import com.gm.gmall.starter.cache.service.CacheService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @author gym
 * @create 2022/8/30 0030 15:51
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Override
    public String getCache(Integer skuId) {
        String s = redisTemplate.opsForValue().get(RedisConstant.SKU_INFO_PRE + skuId);
        if (s.equals("null"))
            return null;
        return s;
    }

    /**
     * 从缓存中获取一个对象并返回给定类型
     * @param info
     * @param returnType
     * @return
     */
    @Override
    public Object getCache(String info, Type returnType)  {
        String s = redisTemplate.opsForValue().get(info);
        if (s==null)
            return null;
        Object o = Jsons.toObject(s,
                new TypeReference<Object>(){
                    public Type getType(){
                        return returnType;
                    }
                });
        return o;
    }

    @Override
    public boolean tryLock(Integer skuId) {
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_PFE + skuId);
        return  lock.tryLock();
    }

    @Override
    public boolean tryLock(String lock) {
       return redissonClient.getLock(lock).tryLock();
    }

    @Override
    public void saveDetail(Integer skuId, Object sqlDetail) {
        if (sqlDetail==null){
            //缓存短一点
            redisTemplate.opsForValue().set(RedisConstant.SKU_INFO_PRE + skuId,"null",60*30, TimeUnit.SECONDS);
        }else {
            String s = Jsons.toJson(sqlDetail);
            redisTemplate.opsForValue().set(RedisConstant.SKU_INFO_PRE + skuId,s,6*60*30, TimeUnit.SECONDS);
        }
    }

    @Override
    public void saveDetail(String info, Object skuInfoDetailTo) {
        if (skuInfoDetailTo == null){
            redisTemplate.opsForValue().set(info,"null",60*30, TimeUnit.SECONDS);
        }else {
            String s = Jsons.toJson(skuInfoDetailTo);
            redisTemplate.opsForValue().set(info,s,6*60*30, TimeUnit.SECONDS);
        }
    }

    @Override
    public void unLock(Integer skuId) {
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_PFE + skuId);
        lock.unlock();
    }

    @Override
    public void unLock(String lock) {
        redissonClient.getLock(lock).unlock();
    }

    @Override
    public boolean bloomContains(Integer skuId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstant.BLOOM_PRE);
        return  bloomFilter.contains(skuId);
    }

    @Override
    public boolean bloomContains(String bloomName, Object val) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomName);
        return bloomFilter.contains(val);

    }
}
