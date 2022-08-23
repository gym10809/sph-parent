package com.gm.gmall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.product.BaseCategory2;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service
* @createDate 2022-08-22 20:13:56
*/
public interface BaseCategory2Service extends IService<BaseCategory2> {

    List<BaseCategory2> getByCategoryId(Integer categoryId);
}
