package com.gm.gmall.web.controller;

import com.gm.gmall.common.feignClient.order.OrderFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author gym
 * @create 2022/9/14 0014 9:27
 */
@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;
    @GetMapping("/pay.html")
    public String pay(@RequestParam("orderId")Long orderId, Model model){
        Result<OrderInfo> data = orderFeignClient.getOrderInfo(orderId);
        Date ttl = data.getData().getExpireTime();
        Date cur = new Date();
        if(cur.before(ttl)){
            model.addAttribute("orderInfo",data.getData());
            return "payment/pay";
        }

        return "payment/error";

    }
}
