package com.gm.gmall.common.feignClient.order;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.vo.order.OrderDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/9/13 0013 11:15
 */
@RequestMapping("api/inner/rpc/order")
@FeignClient("service-order")
public interface OrderFeignClient {
    /**
     * 查询选中商品信息，返回给结算页面
     * @return
     */
    @GetMapping("/getData")
     Result<OrderDataVo> getData();

    @GetMapping("/getOrderInfo")
    Result<OrderInfo> getOrderInfo(@RequestParam("orderId") Long orderId);

    @PostMapping("/submit")
    Result<Long> submit(@RequestBody OrderInfo orderInfo);
}
