package com.gm.gmall.item.service.impl;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.util.Jsons;

import com.gm.gmall.common.feignClient.product.ProductFeignClient;
import com.gm.gmall.item.service.SkuDetailService;
import com.gm.gmall.model.product.SkuImage;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.model.to.SkuDetailTo;
import com.gm.gmall.starter.cache.annotation.CacheSkuInfo;
import com.gm.gmall.starter.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CacheService cacheService;

    /**
     * 从数据库查询相关数据
     * @param skuId
     * @return
     */
    public SkuDetailTo getSqlDetail(Integer skuId) {
//        Result<SkuDetailTo> detail = itemFeignClient.getDetail(skuId);
        SkuDetailTo data = new SkuDetailTo();

        CompletableFuture<SkuInfo> skuInfoFuture =CompletableFuture.supplyAsync(()->{
            Result<SkuInfo> result = productFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            data.setSkuInfo(skuInfo);
            return skuInfo;
        },executor);

        CompletableFuture<Void> spuSaleAttr = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null){
                Long spuId = skuInfo.getSpuId();
                Result<List<SpuSaleAttr>> result = productFeignClient.getSpuSaleAttrList(spuId, skuId);
                List<SpuSaleAttr> saleAttrList = result.getData();
                data.setSpuSaleAttrList(saleAttrList);
            }
        }, executor);
        CompletableFuture<Void> images = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null) {
                Result<List<SkuImage>> result = productFeignClient.getSkuImageList(skuId);
                List<SkuImage> skuImages = result.getData();
                skuInfo.setSkuImageList(skuImages);
            }
        }, executor);
        CompletableFuture<Void> categoryView = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null) {
                Result<CategoryViewTo> r = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                CategoryViewTo categoryViewTo = r.getData();
                data.setCategoryView(categoryViewTo);
            }
        }, executor);

        CompletableFuture<Void> json = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null) {
                Result<String> result = productFeignClient.getValuesSkuJson(skuInfo.getSpuId());
                String s = result.getData();
                data.setValuesSkuJson(s);
            }
        }, executor);
        CompletableFuture<Void> getPrice = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null) {
                BigDecimal price = skuInfo.getPrice();
                data.setPrice(price);
            }
        }, executor);

        //前面的异步任务都完成后才能返回data
        CompletableFuture.allOf(spuSaleAttr,images,categoryView,json,getPrice).join();
        return data;
    }

    /**
     * 优化2.0：使用AOP切面编程
     */
    @CacheSkuInfo(redisName = "skuInfo:detail:#{#params[0]}"
            ,bloomName = "bloom:skuId"
            ,lockName = "lock:skuInfo:detail:#{#params[0]}"
            ,bloomVal = "#{#params[0]}")
    @Override
    public SkuDetailTo getDetail(Integer skuId){
        SkuDetailTo sqlDetail = getSqlDetail(skuId);
        if (sqlDetail == null) return null;
        return sqlDetail;
    }

    /**
     * 优化1.0：使用分布式锁、布隆过滤器、缓存等优化
     * @param skuId
     * @return
     */
    public SkuDetailTo getDetail1(Integer skuId) {
      String s=  cacheService.getCache(skuId);
        SkuDetailTo skuDetailTo =new SkuDetailTo();
        if (StringUtils.isEmpty(s)){
            //如果为空则说明缓存中没有
            //从布隆中查询id是否存在
          boolean contain= cacheService.bloomContains(skuId);
          if (!contain){
              //布隆没查到必没有
              return null;
          }
            boolean lock=  cacheService.tryLock(skuId);
            if (lock){
                //加锁成功，回源查询
                SkuDetailTo sqlDetail = getSqlDetail(skuId);
                //放缓存
                cacheService.saveDetail(skuId,sqlDetail);
                //解锁
                cacheService.unLock(skuId);
                skuDetailTo=sqlDetail;
            }
        }else {
            //缓存中有
            skuDetailTo = Jsons.toObject(s, SkuDetailTo.class);
        }
        return skuDetailTo;
    }
}
