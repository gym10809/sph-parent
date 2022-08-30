package com.gm.gmall.item.cache.impl;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.item.cache.CacheService;
import com.gm.gmall.model.to.SkuDetailTo;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
        return   redisTemplate.opsForValue().get("skuInfo:detail:" + skuId);
    }

    @Override
    public boolean tryLock(Integer skuId) {
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_PFE + skuId);
        return  lock.tryLock();
    }

    @Override
    public void saveDetail(Integer skuId, SkuDetailTo sqlDetail) {
        if (sqlDetail==null){
            //缓存短一点
            redisTemplate.opsForValue().set(RedisConstant.SKU_INFO_PRE + skuId,"null",60*30, TimeUnit.SECONDS);
        }else {
            String s = Jsons.toJson(sqlDetail);
            redisTemplate.opsForValue().set(RedisConstant.SKU_INFO_PRE + skuId,s,6*60*30, TimeUnit.SECONDS);
        }
    }

    @Override
    public void unLock(Integer skuId) {
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_PFE + skuId);
        lock.unlock();
    }

    @Override
    public boolean bloomContains(Integer skuId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstant.BLOOM_PRE);
        return  bloomFilter.contains(skuId);
    }
}
