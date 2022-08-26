package com.gm.gmall.item.service;

import com.gm.gmall.model.to.SkuDetailTo;

/**
 * @author gym
 * @create 2022/8/26 0026 21:47
 */
public interface SkuDetailService {

    SkuDetailTo getDetail(Integer skuId);
}
