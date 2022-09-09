package com.gm.gmall.cart.service;

import com.gm.gmall.model.cart.CartInfo;
import com.gm.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/8 0008 18:57
 */
public interface CartService {
    SkuInfo addCart(Long skuId, Integer skuNum);

    List<CartInfo> cartLiat();
}
