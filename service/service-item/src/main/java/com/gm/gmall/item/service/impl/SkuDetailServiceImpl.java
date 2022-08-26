package com.gm.gmall.item.service.impl;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.item.feign.ItemFeignClient;
import com.gm.gmall.item.service.SkuDetailService;
import com.gm.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gym
 * @create 2022/8/26 0026 21:48
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    ItemFeignClient itemFeignClient;

    @Override
    public SkuDetailTo getDetail(Integer skuId) {
        Result<SkuDetailTo> detail = itemFeignClient.getDetail(skuId);
        SkuDetailTo data = detail.getData();

        return data;
    }
}
