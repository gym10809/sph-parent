package com.gm.gmall.item.service.impl;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.item.feign.ItemFeignClient;
import com.gm.gmall.item.service.SkuDetailService;
import com.gm.gmall.model.product.SkuImage;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author gym
 * @create 2022/8/26 0026 21:48
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    ItemFeignClient itemFeignClient;
    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public SkuDetailTo getDetail(Integer skuId) {
//        Result<SkuDetailTo> detail = itemFeignClient.getDetail(skuId);
        SkuDetailTo data = new SkuDetailTo();

        CompletableFuture<SkuInfo> skuInfoFuture =CompletableFuture.supplyAsync(()->{
            Result<SkuInfo> result = itemFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            data.setSkuInfo(skuInfo);
            return skuInfo;
        },executor);

        CompletableFuture<Void> spuSaleAttr = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> result = itemFeignClient.getSpuSaleAttrList(spuId, skuId);
            List<SpuSaleAttr> saleAttrList = result.getData();
            data.setSpuSaleAttrList(saleAttrList);
        }, executor);
        CompletableFuture<Void> images = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SkuImage>> result = itemFeignClient.getSkuImageList(skuId);
            List<SkuImage> skuImages = result.getData();
            skuInfo.setSkuImageList(skuImages);
        }, executor);
        CompletableFuture<Void> categoryView = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryViewTo> r = itemFeignClient.getCategoryView(skuInfo.getCategory3Id());
            CategoryViewTo categoryViewTo = r.getData();
            data.setCategoryView(categoryViewTo);
        }, executor);

        CompletableFuture<Void> json = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<String> result = itemFeignClient.getValuesSkuJson(skuInfo.getSpuId());
            String s = result.getData();
            data.setValuesSkuJson(s);
        }, executor);
        CompletableFuture<Void> getPrice = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            BigDecimal price = skuInfo.getPrice();
            data.setPrice(price);
        }, executor);

        //前面的异步任务都完成后才能返回data
        CompletableFuture.allOf(spuSaleAttr,images,categoryView,json,getPrice).join();
        return data;
    }
}
