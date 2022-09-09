package com.gm.gmall.common.feignClient.cart;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author gym
 * @create 2022/9/8 0008 11:26
 */
@RequestMapping("api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    @GetMapping("/addCart")
     Result<SkuInfo> addCart(@RequestParam("skuId") Long skuId, @RequestParam("skuNum") Integer skuNum);


}
