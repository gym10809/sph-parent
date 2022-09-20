package com.gm.gmall.seckill.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
* @author Administrator
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2022-09-19 19:32:26
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {

    List<SeckillGoods> getList();

    void dscStock(Long skuId);
}
