package com.gm.gmall.product.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.BaseCategory1;
import com.gm.gmall.model.to.IndexTreeTo;
import com.gm.gmall.product.service.BaseCategory1Service;
import com.gm.gmall.product.mapper.BaseCategory1Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category1(一级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 20:13:56
*/
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1>
    implements BaseCategory1Service{
    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;
    @Override
    public List<IndexTreeTo> indexTree() {
       List<IndexTreeTo> list= baseCategory1Mapper.indexTree();
        return list;
    }
}




