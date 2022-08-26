package com.gm.gmall.product.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.product.service.SpuSaleAttrService;
import com.gm.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-23 15:32:33
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Autowired
    SpuSaleAttrMapper saleAttrMapper;

    @Override
    public List<SpuSaleAttr> getAttrAndSale(Long spuId, Integer skuId) {
        List<SpuSaleAttr> list=    saleAttrMapper.getAttrAndSale(spuId,skuId);
        return list;
    }

    @Override
    public List<SpuSaleAttr> getSaleAndValueBySpuId(Integer spuId) {
        List<SpuSaleAttr> list=  saleAttrMapper.getSaleAndValueBySpuId(spuId);
        return list;
    }
}




