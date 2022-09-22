package com.gm.gmall.seckill.service;

import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.to.SeckillOrderMsg;
import com.gm.gmall.model.to.SeckillOrderSub;

/**
 * @author gym
 * @create 2022/9/19 0019 20:33
 */
public interface SeckillService {
    String getSeckillSkuIdStr(Long skuId);

    ResultCodeEnum seckillOrder(Long skuId, String skuIdStr);

    ResultCodeEnum checkOrder(Long  skuId);

    SeckillOrderMsg getMsg(Long skuId);

    Long submit(OrderInfo orderInfo);
}
