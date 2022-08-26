package com.gm.gmall.product.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.SkuAttrValue;
import com.gm.gmall.model.product.SkuImage;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.product.SkuSaleAttrValue;
import com.gm.gmall.product.mapper.SkuInfoMapper;
import com.gm.gmall.product.service.SkuAttrValueService;
import com.gm.gmall.product.service.SkuImageService;
import com.gm.gmall.product.service.SkuInfoService;
import com.gm.gmall.product.service.SkuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-23 15:32:33
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService {
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageService imageService;
    @Autowired
    SkuAttrValueService attrValueService;
    @Autowired
    SkuSaleAttrValueService saleAttrValueService;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        //存图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage:skuImageList) {
            skuImage.setSkuId(skuId);
        }
        imageService.saveBatch(skuImageList);
        //存平台属性
        List<SkuAttrValue> attrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue:attrValueList) {
            skuAttrValue.setSkuId(skuId);
        }
        attrValueService.saveBatch(attrValueList);
        //存销售属性
        List<SkuSaleAttrValue> saleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue:saleAttrValueList) {
                saleAttrValue.setSkuId(skuId);
                saleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        saleAttrValueService.saveBatch(saleAttrValueList);
    }

    /**
     * 1为上架，0为下架
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.updateBySkuId(skuId,1);
    }
    @Override
    public void cancelSale(Long skuId) {

        skuInfoMapper.updateBySkuId(skuId,0);
    }
}




