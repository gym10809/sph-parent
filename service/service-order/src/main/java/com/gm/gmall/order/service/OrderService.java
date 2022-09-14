package com.gm.gmall.order.service;

import com.gm.gmall.model.vo.order.OrderDataVo;
import com.gm.gmall.model.vo.order.OrderMsg;
import com.gm.gmall.model.vo.order.OrderSubmitVo;

/**
 * @author gym
 * @create 2022/9/13 0013 18:27
 */
public interface OrderService {
    OrderDataVo getData();
    String getTradeNo();
    boolean checkToken(String tradeNo);
    String submit(String tradeNo, OrderSubmitVo orderSubmitVo);

    void closeOrder(OrderMsg orderMsg);
}
