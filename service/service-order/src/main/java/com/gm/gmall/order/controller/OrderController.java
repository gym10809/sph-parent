package com.gm.gmall.order.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.vo.order.OrderSplitVo;
import com.gm.gmall.model.vo.order.OrderSubmitVo;
import com.gm.gmall.model.vo.order.WareChildOrderVo;
import com.gm.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/14 0014 0:20
 */
@RequestMapping("/api/order")
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;
    @PostMapping("/auth/submitOrder")
    public Result  submitOrder(@RequestParam("tradeNo") String tradeNo,
                               @RequestBody OrderSubmitVo orderSubmitVo){

        String orderId = orderService.submit(tradeNo,orderSubmitVo);
        return Result.ok(orderId);
    }



}
