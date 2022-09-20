package com.gm.gmall.common.feignClient.seckill;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/19 0019 19:52
 */
@RequestMapping("/api/inner/rpc/seckill")
@FeignClient(value = "service-seckill")
public interface SeckillFeignClient {
    @GetMapping("/list")
     Result<List<SeckillGoods>> list();

    @GetMapping("/goods/detail/{skuId}")
    Result<SeckillGoods> getOne(@PathVariable("skuId") Long skuId);
}
