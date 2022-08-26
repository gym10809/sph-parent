package com.gm.gmall.product.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.to.SkuDetailTo;
import com.gm.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/8/26 0026 21:55
 */
@RestController
@RequestMapping("api/inner/rpc/product")
public class ItemDetailController {
    @Autowired
    SkuInfoService infoService;

    @GetMapping("/skudetail/{skuId}")
     public  Result<SkuDetailTo> getDetail(@PathVariable("skuId")Integer skuId){
        SkuDetailTo skuDetailTo=  infoService.getDetail(skuId);
        return Result.ok(skuDetailTo);
    }
}
