package com.gm.gmall.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.product.BaseCategory1;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.model.to.IndexTreeTo;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category1(一级分类表)】的数据库操作Mapper
* @createDate 2022-08-22 20:13:56
* @Entity com.gm.gmall.product.domain.BaseCategory1
*/
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {

    List<IndexTreeTo> indexTree();

}




