package com.gm.gmall.web.controller;

import com.gm.gmall.common.feignClient.cart.CartFeignClient;


import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author gym
 * @create 2022/9/8 0008 11:21
 */
@Controller
public class CartController {
    @Autowired
    CartFeignClient cartFeignClient;

    @GetMapping("/addCart.html")
    public String addCart(@RequestParam("skuId")Long skuId,@RequestParam("skuNum")Integer skuNum
                            ,Model model){

        Result result = cartFeignClient.addCart(skuId, skuNum);
        model.addAttribute("skuInfo",result.getData());
        model.addAttribute("skuNum",skuNum);
        return "/cart/addCart";
    }

    @GetMapping("/cart.html")
    public String listCart(){

        return "cart/index";
    }

    @GetMapping("cart/deleteChecked")
    public String delCheck(){

        return "cart/cart:ofct";
    }
}
