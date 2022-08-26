package com.gm.gmall.item.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.item.service.SkuDetailService;
import com.gm.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/8/26 0026 21:43
 */
@RestController
@RequestMapping("api/inner/rpc/item")
public class SkuDetailItemController {
    @Autowired
    SkuDetailService skuDetailService;

    @GetMapping("/skudetail/{skuId}")
    Result<SkuDetailTo> getDetails(@PathVariable("skuId")Integer skuId){
        SkuDetailTo skuDetailTo=  skuDetailService.getDetail(skuId);
        return Result.ok(skuDetailTo);
    }
}
