package com.gm.gmall.model.to;

import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 20:11
 */
@Data
public class SkuDetailTo {
    //当前sku所属的分类信息
    private CategoryViewTo categoryView;

    //商品的基本信息
    private SkuInfo skuInfo;

    //实时价格
    private BigDecimal price;

    //spu的所有销售属性列表
    private List<SpuSaleAttr> spuSaleAttrList;

    //valueSkuJson
    private String valuesSkuJson;
}
