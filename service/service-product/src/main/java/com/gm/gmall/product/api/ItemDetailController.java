package com.gm.gmall.product.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuImage;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.model.to.SkuDetailTo;
import com.gm.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 21:55
 */
@RestController
@RequestMapping("api/inner/rpc/product")
public class ItemDetailController {
    @Autowired
    SkuInfoService infoService;

//    @GetMapping("/skudetail/{skuId}")
//     public  Result<SkuDetailTo> getDetail(@PathVariable("skuId")Integer skuId){
//        SkuDetailTo skuDetailTo=  infoService.getDetail(skuId);
//        return Result.ok(skuDetailTo);
//    }

    /**
     * 获取sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId")Integer skuId){
      SkuInfo skuInfo=  infoService.getSkuInfo(skuId);
      return Result.ok(skuInfo);
    }

    /**
     * 查询sku对应的spu定义的所有销售属性名和值。并且标记出当前sku是哪个
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}/{skuId}")
    Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("spuId")Long spuId, @PathVariable("skuId")Integer skuId){
        List<SpuSaleAttr> list=  infoService.getSpuSaleAttrList(spuId,skuId);
        return Result.ok(list);
    }

    /**
     * 查询对应的图片信息
     * @param skuId
     * @return
     */
    @GetMapping("/skuImageList/{skuId}")
    Result<List<SkuImage>> getSkuImageList(@PathVariable("skuId")Integer skuId){
        List<SkuImage> list=   infoService.getSkuImageList(skuId);
        return Result.ok(list);
    }

    /**
     * 查询分类
     * @param category3Id
     * @return
     */

    @GetMapping("/categoryView/{category3Id}")
    Result<CategoryViewTo> getCategoryView(@PathVariable("category3Id") Long category3Id){
        CategoryViewTo categoryViewTo=   infoService.getCategoryView(category3Id);
        return Result.ok(categoryViewTo);
    }

    /**
     * 查询对应sku组合的json字符
     * @param spuId
     * @return
     */

    @GetMapping("/valuesSkuJson/{spuId}")
    Result<String> getValuesSkuJson(@PathVariable("spuId")Long spuId){
       String s=  infoService.getValuesSkuJson(spuId);
       return Result.ok(s);
    }
}
