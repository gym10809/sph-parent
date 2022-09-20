package com.gm.gmall.seckill.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.seckill.service.CacheService;
import com.gm.gmall.seckill.service.SeckillGoodsService;
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
    @GetMapping("/list")
    public Result<List<SeckillGoods>> list(){
       List<SeckillGoods> list= cacheService.getList();
        return Result.ok(list);
    }

    @GetMapping("/goods/detail/{skuId}")
    public Result<SeckillGoods> getOne(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods= cacheService.getSeckillOrder(skuId);
        return Result.ok(seckillGoods);
    }

}
