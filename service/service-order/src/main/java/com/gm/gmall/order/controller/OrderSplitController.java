package com.gm.gmall.order.controller;

import com.gm.gmall.model.vo.order.OrderSplitVo;
import com.gm.gmall.model.vo.order.WareChildOrderVo;
import com.gm.gmall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/18 0018 21:59
 */
@Slf4j
@RequestMapping("/api/order")
@RestController
public class OrderSplitController {
    @Autowired
    OrderService orderService;
    /**
     * 拆单,仓库不同，或者次仓库暂时无货
     */
    @PostMapping("/orderSplit")
    public List<WareChildOrderVo> split( OrderSplitVo orderSplitVo){
        return  orderService.splitOrder(orderSplitVo);
    }
}
