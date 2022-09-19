package com.gm.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.pay.service.AliPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author gym
 * @create 2022/9/16 0016 19:40
 */
@Slf4j
@Controller
@RequestMapping("/api/payment")
public class PayController {
    @Autowired
    AliPayService aliPayService;

    //vviont9159@sandbox.com
    @GetMapping("/alipay/submit/{orderId}")
    @ResponseBody
    public String aliPay(@PathVariable("orderId")Long orderId) throws AlipayApiException {
        String html=aliPayService.getAliPAyHtml(orderId);
        return html;
    }

    /**
     * 同步跳转
     */
    @GetMapping("/alipay/success")
    public String paySuccess(){
        return "redirect:http://gmall.com/pay/success.html";
    }
    /**
     * 异步执行订单更改功能
     */
    @ResponseBody
    @PostMapping("/notify/success")
    public String notifySuccess(@RequestParam Map<String,String> map) throws AlipayApiException {
        boolean isFlag= aliPayService.checkSing(map);
        if (isFlag){
            log.info("修改订单", Jsons.toJson(map));
            aliPayService.sendMsg(map);
            return "success";
        }
        return "error";

    }

}
