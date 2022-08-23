package com.gm.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.BaseCategory3;
import com.gm.gmall.product.service.BaseCategory3Service;
import com.gm.gmall.product.mapper.BaseCategory3Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 20:13:56
*/
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
    implements BaseCategory3Service{

    @Override
    public List<BaseCategory3> getByCategoryId(Integer category2Id) {
        LambdaQueryWrapper<BaseCategory3> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory3::getCategory2Id,category2Id);
        List<BaseCategory3> category3s = baseMapper.selectList(queryWrapper);
        return category3s;
    }
}




