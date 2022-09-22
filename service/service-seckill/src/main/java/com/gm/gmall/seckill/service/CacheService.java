package com.gm.gmall.seckill.service;

import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.model.to.SeckillOrderMsg;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/19 0019 21:23
 */
public interface CacheService {
    void cache(List<SeckillGoods> seckillGoods);

    void cleanCache();

    SeckillGoods getSeckillOrder(Long skuId);
     void syncCache();
    List<SeckillGoods> getRedisCache();

    List<SeckillGoods> getList();

}
