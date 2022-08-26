package com.gm.gmall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.product.SpuSaleAttr;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2022-08-23 15:32:33
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    List<SpuSaleAttr> getSaleAndValueBySpuId(Integer spuId);
}
