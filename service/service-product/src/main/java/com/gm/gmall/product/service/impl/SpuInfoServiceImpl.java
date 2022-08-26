package com.gm.gmall.product.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.SpuImage;
import com.gm.gmall.model.product.SpuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.product.SpuSaleAttrValue;
import com.gm.gmall.product.mapper.SpuImageMapper;
import com.gm.gmall.product.mapper.SpuInfoMapper;
import com.gm.gmall.product.service.SpuImageService;
import com.gm.gmall.product.service.SpuInfoService;
import com.gm.gmall.product.service.SpuSaleAttrService;
import com.gm.gmall.product.service.SpuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2022-08-23 15:32:33
*/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService {

    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService saleAttrService;
    @Autowired
    SpuSaleAttrValueService attrValueService;

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
         spuInfoMapper.insert(spuInfo);
         //向spuImage表中存入图片数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage:spuImageList) {
            spuImage.setSpuId(spuInfo.getId());
        }
        spuImageService.saveBatch(spuImageList);
        //销售属性存表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr:spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuInfo.getId());
            //销售属性值存表
            List<SpuSaleAttrValue> attrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue saleAttrValue:attrValueList) {
                saleAttrValue.setSpuId(spuInfo.getId());
                saleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            }
            attrValueService.saveBatch(attrValueList);
        }
        saleAttrService.saveBatch(spuSaleAttrList);
    }
}




