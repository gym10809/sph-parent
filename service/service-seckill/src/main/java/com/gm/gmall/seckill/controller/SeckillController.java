package com.gm.gmall.seckill.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.to.SeckillOrderMsg;
import com.gm.gmall.model.to.SeckillOrderSub;
import com.gm.gmall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/9/19 0019 20:25
 */
@RestController
@RequestMapping("/api/activity/seckill/auth")
public class SeckillController {

    @Autowired
    SeckillService service;
    /**
     * 获取秒杀码
     * @param skuId
     * @return
     */
    @RequestMapping("/getSeckillSkuIdStr/{skuId}")
    public Result<String> getSeckillSkuIdStr(@PathVariable("skuId")Long skuId){
        String s=service.getSeckillSkuIdStr(skuId);
        return Result.ok(s);
    }
    /**
     * ?skuIdStr=null
     *开始进行秒杀，判断
     */
    @PostMapping("/seckillOrder/{skuId}")
    public Result seckill(@PathVariable("skuId")Long skuId,
                          @RequestParam("skuIdStr")String skuIdStr){
         ResultCodeEnum codeEnum= service.seckillOrder(skuId,skuIdStr);
        return Result.build("",codeEnum);
    }

    /**
     * 判断秒杀状态
     * @param skuId
     * @return
     */
    @GetMapping("/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId")Long skuId){
        ResultCodeEnum codeEnum=service.checkOrder(skuId);
        return Result.build("",codeEnum);
    }
    @PostMapping("/submitOrder")
    public Result submit(@RequestBody OrderInfo orderInfo){
        Long orderId=service.submit(orderInfo);
        return Result.ok(orderId);
    }
}
