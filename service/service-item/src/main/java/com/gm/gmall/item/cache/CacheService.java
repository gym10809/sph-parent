package com.gm.gmall.item.cache;

import com.gm.gmall.model.to.SkuDetailTo;

/**
 * @author gym
 * @create 2022/8/30 0030 15:51
 */
public interface CacheService {
    String getCache(Integer skuId);

    boolean tryLock(Integer skuId);

    void saveDetail(Integer skuId, SkuDetailTo sqlDetail);

    void unLock(Integer skuId);

    boolean bloomContains(Integer skuId);
}
