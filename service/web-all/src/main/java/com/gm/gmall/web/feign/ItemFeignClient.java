package com.gm.gmall.web.feign;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author gym
 * @create 2022/8/26 0026 20:18
 */
@RequestMapping("api/inner/rpc/item")
@FeignClient("service-item")
public interface ItemFeignClient {

    @GetMapping("/skudetail/{skuId}")
    Result<SkuDetailTo> getDetail(@PathVariable("skuId")Integer skuId);
}
