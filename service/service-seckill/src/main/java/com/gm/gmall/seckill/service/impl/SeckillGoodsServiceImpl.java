package com.gm.gmall.seckill.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.common.util.DateUtil;
import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.seckill.mapper.SeckillGoodsMapper;
import com.gm.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【seckill_goods】的数据库操作Service实现
* @createDate 2022-09-19 19:32:26
*/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService {
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public List<SeckillGoods> getList() {
        String date = DateUtil.formatDate(new Date());
        return seckillGoodsMapper.getList(date);

    }

    @Override
    public void dscStock(Long skuId) {
        seckillGoodsMapper.dscStock(skuId);
    }


}




