package com.gm.gmall.seckill.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.model.to.SeckillOrderMsg;
import com.gm.gmall.seckill.service.CacheService;
import com.gm.gmall.seckill.service.SeckillGoodsService;
import com.gm.gmall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/19 0019 19:35
 */
@RestController
@RequestMapping("/api/inner/rpc/seckill")
public class SecKillApiController {

    @Autowired
    CacheService cacheService;
    @Autowired
    SeckillService service;
    /**
     * 得到当天秒杀商品
     * @return
     */
    @GetMapping("/list")
    public Result<List<SeckillGoods>> list(){
       List<SeckillGoods> list= cacheService.getList();
        return Result.ok(list);
    }

    /**
     * 得到秒杀商品的详情
     * @param skuId
     * @return
     */
    @GetMapping("/goods/detail/{skuId}")
    public Result<SeckillGoods> getOne(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods= cacheService.getSeckillOrder(skuId);
        return Result.ok(seckillGoods);
    }

    /**
     * 支付页面的信息
     * @return
     */
    @GetMapping("/payOrderMsg")
    Result<SeckillOrderMsg> getOrdedrMsg(@RequestParam("skuId")Long skuId){
        SeckillOrderMsg seckillOrderMsg=service.getMsg(skuId);
        return Result.ok(seckillOrderMsg);
    }
}
