package com.gm.gmall.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.to.SkuSaleValueTo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-08-23 15:32:33
* @Entity com.gm.gmall.product.domain.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void updateBySkuId(@Param("skuId") Long skuId,@Param("isSale")Integer isSale);

    List<SkuSaleValueTo> getSkuSaleAndValue(Long spuId);
}




