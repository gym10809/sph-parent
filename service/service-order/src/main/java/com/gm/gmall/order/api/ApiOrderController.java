package com.gm.gmall.order.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.vo.order.OrderDataVo;
import com.gm.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gym
 * @create 2022/9/13 0013 11:16
 */
@RequestMapping("api/inner/rpc/order")
@RestController
public class ApiOrderController {

    @Autowired
    OrderService orderService;

    /**
     * 查询选中商品信息，返回给结算页面
     * @return
     */
    @GetMapping("/getData")
    public Result<OrderDataVo> getData(){
         OrderDataVo vo=  orderService.getData();
        return Result.ok(vo);
    }

}
