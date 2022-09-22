package com.gm.gmall.seckill.scheduled;

import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.seckill.service.CacheService;
import com.gm.gmall.seckill.service.SeckillGoodsService;
import com.gm.gmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/19 0019 21:15
 */
@Service
@Slf4j
public class ScheduledSeckill {


    @Autowired
    SeckillGoodsService service;
    @Autowired
    CacheService cacheService;
    /**
     * 每天凌晨更新当天的秒杀商品,商品库存、、、、
     */
//    @Scheduled(cron = "0 0 2 * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void updateSeckillGoods(){
        List<SeckillGoods> seckillGoods = service.getList();
        //当天的秒杀商品存入redis中,商品库存、本地缓存商品
        log.info("开始同步秒杀商品");
        cacheService.cache(seckillGoods);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void clean(){
        cacheService.cleanCache();
    }
}
