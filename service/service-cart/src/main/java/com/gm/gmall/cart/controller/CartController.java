package com.gm.gmall.cart.controller;

import com.gm.gmall.cart.service.CartService;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
