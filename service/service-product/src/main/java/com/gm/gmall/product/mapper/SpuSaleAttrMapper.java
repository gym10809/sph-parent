package com.gm.gmall.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.product.SpuSaleAttr;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2022-08-23 15:32:33
* @Entity com.gm.gmall.product.domain.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    List<SpuSaleAttr> getSaleAndValueBySpuId(Integer spuId);
}




