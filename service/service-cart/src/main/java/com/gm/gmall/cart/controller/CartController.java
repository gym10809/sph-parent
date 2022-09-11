package com.gm.gmall.cart.controller;

import com.gm.gmall.cart.service.CartService;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/8 0008 21:01
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/cartList")
    public Result cartList(){
        List<CartInfo> cartInfos=cartService.cartLiat();
        return Result.ok(cartInfos);
    }

    @PostMapping("/addToCart/{skuId}/{num}")
    public Result addToCart(@PathVariable("skuId")Long skuId,@PathVariable("num")Integer num){
    cartService.addToCart(skuId,num);
    return Result.ok();
    }

    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable("skuId")Long skuId,@PathVariable("status")Integer status){
        cartService.checkCart(skuId,status);
        return Result.ok();
    }

    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId")Long skuId){
        cartService.deleteCart(skuId);
        return Result.ok();
    }
}
