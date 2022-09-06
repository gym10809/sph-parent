package com.gm.gmall.search.repository;

import com.gm.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author gym
 * @create 2022/9/3 0003 18:25
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
