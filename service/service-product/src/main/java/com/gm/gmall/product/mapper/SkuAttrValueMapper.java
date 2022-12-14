package com.gm.gmall.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.list.SearchAttr;
import com.gm.gmall.model.product.SkuAttrValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
* @createDate 2022-08-23 15:32:32
* @Entity com.gm.gmall.product.domain.SkuAttrValue
*/
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {

    List<SearchAttr> getSkuAttrNameAndValue(@Param("skuId") Long skuId);
}




