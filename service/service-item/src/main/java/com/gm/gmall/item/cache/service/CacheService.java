package com.gm.gmall.item.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gm.gmall.model.to.SkuDetailTo;

import java.lang.reflect.Type;

/**
 * @author gym
 * @create 2022/8/30 0030 15:51
 */
public interface CacheService {
    String getCache(Integer skuId);
    Object getCache(String info, Type returnType) throws JsonProcessingException;
    boolean tryLock(Integer skuId);
    boolean tryLock(String lock);
    void saveDetail(Integer skuId, SkuDetailTo sqlDetail);
    void saveDetail(String info, Object skuInfoDetailTo);
    void unLock(Integer skuId);
    void unLock(String lock);
    boolean bloomContains(Integer skuId);


    boolean bloomContains(String bloomName, Object val);



}
