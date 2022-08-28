package com.gm.gmall.item.feign;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuImage;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 21:57
 */
@RequestMapping("api/inner/rpc/product")
@FeignClient("service-product")
public interface ItemFeignClient {
    @GetMapping("/skudetail/{skuId}")
     Result<SkuDetailTo> getDetail(@PathVariable("skuId")Integer skuId);

    @GetMapping("/skuInfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId")Integer skuId);

    @GetMapping("/spuSaleAttrList/{spuId}/{skuId}")
   Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("spuId")Long spuId,@PathVariable("skuId")Integer skuId);

    @GetMapping("/skuImageList/{skuId}")
    Result<List<SkuImage>> getSkuImageList(@PathVariable("skuId")Integer skuId);

    @GetMapping("/categoryView/{category3Id}")
   Result<CategoryViewTo> getCategoryView(@PathVariable("category3Id") Long category3Id);

    @GetMapping("/valuesSkuJson/{spuId}")
    Result<String> getValuesSkuJson(@PathVariable("spuId")Long spuId);
}
