package com.gm.gmall.cart.api;

import com.gm.gmall.cart.service.CartService;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gym
 * @create 2022/9/8 0008 11:32
 */
@RestController
@RequestMapping("api/inner/rpc/cart")
@Slf4j
public class ApiCartController {
    @Autowired
    CartService cartService;

    @GetMapping("/addCart")
    public Result<SkuInfo> addCart(@RequestParam("skuId") Long skuId,@RequestParam("skuNum") Integer skuNum){
         SkuInfo skuInfo= cartService.addCart(skuId,skuNum);
        return Result.ok(skuInfo);
    }
}
