package com.gm.gmall.order.service;

import com.gm.gmall.model.vo.order.OrderDataVo;

/**
 * @author gym
 * @create 2022/9/13 0013 18:27
 */
public interface OrderService {
    OrderDataVo getData();
    String getTradeNo();
}
