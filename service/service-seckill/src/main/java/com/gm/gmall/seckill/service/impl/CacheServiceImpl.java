package com.gm.gmall.seckill.service.impl;
import java.math.BigDecimal;

import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.model.vo.user.UserInfoId;
import com.google.common.collect.Lists;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.DateUtil;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.model.to.SeckillOrderMsg;
import com.gm.gmall.seckill.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gym
 * @create 2022/9/19 0019 21:23
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;
    private Map<Long,SeckillGoods> map=new ConcurrentHashMap<>();//本地缓存

    /**
     * 缓存商品，库存，本地缓存
     * @param seckillGoods
     */
    @Override
    public void cache(List<SeckillGoods> seckillGoods) {
        //绑定redis
        String date = DateUtil.formatDate(new Date());
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(RedisConstant.SECKILL_GOODS + date);
        hashOps.expire(2, TimeUnit.DAYS);
        seckillGoods.stream().forEach(goods -> {
            //缓存redis、库存，缓存本地缓存
            //redis
            hashOps.put(goods.getSkuId()+"", Jsons.toJson(goods));
            //库存
            redisTemplate.opsForValue().setIfAbsent(RedisConstant.SECKILL_GOODS_STOCK+goods.getSkuId(),
                                                        goods.getStockCount().toString(),1,TimeUnit.DAYS);
            //本地缓存
            map.put(goods.getSkuId(),goods);
        });
    }

    /**
     * 清空本地缓存
     */
    @Override
    public void cleanCache() {
        map.clear();
    }

    /**
     * 从缓存中查询是否次商品是秒杀商品
     * @param skuId
     * @return
     */
    @Override
    public SeckillGoods getSeckillOrder(Long skuId) {
        //先从本地缓存查询
        SeckillGoods goods = map.get(skuId);
        if (goods==null){
            //从redis中查询
            syncCache();
            goods=map.get(skuId);
        }
        return goods;
    }

    /**
     * 同步数据到本地缓存
     */
    @Override
    public void syncCache(){
        //从redis中查询数据
        List<SeckillGoods> goods= getRedisCache();
        //同步至本地缓存中
        goods.stream().forEach(seckillGoods -> {
            map.put(seckillGoods.getSkuId(),seckillGoods);
        });
    }

    /**
     * 从redis 中查询当天的秒杀商品
     * @return
     */
    @Override
    public List<SeckillGoods> getRedisCache() {
        //从redis中查询当天的秒杀商品
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(RedisConstant.SECKILL_GOODS + DateUtil.formatDate(new Date()));
        List<Object> values = ops.values();
        List<SeckillGoods> seckillGoods = values.stream().map(o -> Jsons.toObject(o.toString(), SeckillGoods.class))
                .sorted(Comparator.comparing(SeckillGoods::getStartTime))
                .collect(Collectors.toList());
        return seckillGoods;
    }

    /**
     * 从缓存中得到当天秒杀商品
     * @return
     */
    @Override
    public List<SeckillGoods> getList() {
        //先查本地缓存
        List<SeckillGoods> goods = map.values().stream().sorted(Comparator.comparing(SeckillGoods::getStartTime)).collect(Collectors.toList());
        if (goods==null || goods.size()==0){
            syncCache();
            goods=map.values().stream().sorted(Comparator.comparing(SeckillGoods::getStartTime)).collect(Collectors.toList());
        }
        return goods;
    }


}
