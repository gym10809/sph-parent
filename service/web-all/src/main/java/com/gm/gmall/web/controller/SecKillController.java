package com.gm.gmall.web.controller;

import com.gm.gmall.common.feignClient.seckill.SeckillFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.model.to.SeckillOrderMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/19 0019 19:13
 */
@Controller
public class SecKillController {
    @Autowired
    SeckillFeignClient seckillFeignClient;

    /**
     * 首页显示当天的秒杀商品
     * @param model
     * @return
     */
    @GetMapping("/seckill.html")
    public String secKill(Model model){
        Result<List<SeckillGoods>> list = seckillFeignClient.list();
        model.addAttribute("list",list.getData());
        return "seckill/index";
    }
    /**
     * 显示秒杀中的某个商品
     */

    @GetMapping("/seckill/{skuId}.html")
    public String one(@PathVariable("skuId")Long skuId,
                                    Model model){
       SeckillGoods seckillGoods=  seckillFeignClient.getOne(skuId).getData();
        model.addAttribute("item",seckillGoods);

        return "seckill/item";
    }

    /**
     *提交秒杀 进入秒杀等待队列，等待秒杀商品
     * /seckill/queue.html?skuId=39&skuIdStr=3912022-09-19
     * @param skuId
     * @param skuIdStr
     * @param model
     * @return
     */
    @GetMapping("/seckill/queue.html")
    public String queue(@RequestParam("skuId")Long skuId
                        ,@RequestParam("skuIdStr")String skuIdStr
                        ,Model model){
            model.addAttribute("skuId",skuId);
            model.addAttribute("skuIdStr",skuIdStr);
        return "seckill/queue";
    }

    @GetMapping("/seckill/trade.html")
    public String trade(Model model,@RequestParam("skuId")String skuId){
        SeckillOrderMsg seckillOrderMsg=  seckillFeignClient.getOrdedrMsg(Long.parseLong(skuId)).getData();
        model.addAttribute("userAddressList",seckillOrderMsg.getUserAddressListList());
        model.addAttribute("totalAmount",seckillOrderMsg.getTotalAmount());
        model.addAttribute("totalNum",1);
        model.addAttribute("detailArrayList",seckillOrderMsg.getDetailArrayList());

        return "seckill/trade";
    }
}
