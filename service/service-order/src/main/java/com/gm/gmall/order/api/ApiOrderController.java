package com.gm.gmall.order.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.vo.order.OrderDataVo;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/9/13 0013 11:16
 */
@RequestMapping("api/inner/rpc/order")
@RestController
public class ApiOrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderInfoService orderInfoService;
    /**
     * 查询选中商品信息，返回给结算页面
     * @return
     */
    @GetMapping("/getData")
    public Result<OrderDataVo> getData(){
         OrderDataVo vo=  orderService.getData();
        return Result.ok(vo);
    }

    /**
     * 获取OrderInfo信息
     * @param orderId
     * @return
     */
    @GetMapping("/getOrderInfo")
    Result<OrderInfo> getOrderInfo(@RequestParam("orderId") Long orderId){
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        return Result.ok(orderInfo);
    }
}
