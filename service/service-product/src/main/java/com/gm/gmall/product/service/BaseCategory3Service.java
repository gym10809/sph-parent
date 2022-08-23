package com.gm.gmall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.product.BaseCategory3;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2022-08-22 20:13:56
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getByCategoryId(Integer category2Id);
}
