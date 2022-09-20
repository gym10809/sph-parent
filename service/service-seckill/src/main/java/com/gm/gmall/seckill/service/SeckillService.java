package com.gm.gmall.seckill.service;

import com.gm.gmall.common.result.ResultCodeEnum;

/**
 * @author gym
 * @create 2022/9/19 0019 20:33
 */
public interface SeckillService {
    String getSeckillSkuIdStr(Long skuId);

    ResultCodeEnum seckillOrder(Long skuId, String skuIdStr);
}
