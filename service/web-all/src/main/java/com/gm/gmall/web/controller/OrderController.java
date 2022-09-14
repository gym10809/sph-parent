package com.gm.gmall.web.controller;

import com.gm.gmall.common.feignClient.order.OrderFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.vo.order.OrderDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author gym
 * @create 2022/9/13 0013 10:21
 */
@Controller
public class OrderController {
    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/trade.html")
    public String order(Model model){
        Result<OrderDataVo> data = orderFeignClient.getData();
        OrderDataVo orderDataVo = data.getData();
        //商品信息
        model.addAttribute("detailArrayList",orderDataVo.getDetailArrayList());
        //商品数量
        model.addAttribute("totalNum",orderDataVo.getTotalNum());
        //商品总金额
        model.addAttribute("totalAmount",orderDataVo.getTotalAmount());
        //用户地址
        model.addAttribute("userAddressList",orderDataVo.getUserAddressList());
        //用户交易号
        model.addAttribute("tradeNo",orderDataVo.getTradeNo());

        return "order/trade";
    }
}
