package com.gm.gmall.seckill.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.activity.SeckillGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【seckill_goods】的数据库操作Mapper
* @createDate 2022-09-19 19:32:26
* @Entity com.gm.gmall.seckill.domain.SeckillGoods
*/
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    List<SeckillGoods> getList(@Param("date") String date);

    void dscStock(@Param("skuId") Long skuId);
}




