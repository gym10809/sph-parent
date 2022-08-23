package com.gm.gmall.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.BaseCategory2;
import com.gm.gmall.product.service.BaseCategory2Service;
import com.gm.gmall.product.mapper.BaseCategory2Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 20:13:56
*/
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
    implements BaseCategory2Service{

    @Override
    public List<BaseCategory2> getByCategoryId(Integer categoryId) {
        LambdaQueryWrapper<BaseCategory2> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory2::getCategory1Id,categoryId);

        List<BaseCategory2> baseCategory2s = baseMapper.selectList(queryWrapper);
        return baseCategory2s;
    }
}




