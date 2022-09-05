package com.gm.gmall.search.service;

import com.gm.gmall.model.list.Goods;
import com.gm.gmall.model.list.SearchParam;
import com.gm.gmall.model.list.SearchResponseVo;

/**
 * @author gym
 * @create 2022/9/5 0005 10:18
 */
public interface GoodsService {
    void save(Goods goods);
    void delete(Long skuId);

    SearchResponseVo search(SearchParam searchParam);
}
