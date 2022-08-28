package com.gm.gmall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.product.SkuImage;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.model.to.SkuDetailTo;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 15:32:33
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void onSale(Long skuId);
    void cancelSale(Long skuId);

//    SkuDetailTo getDetail(Integer skuId);

    SkuInfo getSkuInfo(Integer skuId);

    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId, Integer skuId);

    List<SkuImage> getSkuImageList(Integer skuId);

    CategoryViewTo getCategoryView(Long category3Id);

    String getValuesSkuJson(Long spuId);
}
