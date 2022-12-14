package com.gm.gmall.product.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.list.SearchAttr;
import com.gm.gmall.model.product.SkuAttrValue;
import com.gm.gmall.product.mapper.SkuAttrValueMapper;
import com.gm.gmall.product.service.SkuAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service实现
* @createDate 2022-08-23 15:32:32
*/
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
    implements SkuAttrValueService {

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Override
    public List<SearchAttr> getSkuAttrNameAndValue(Long skuId) {
        List<SearchAttr> list=skuAttrValueMapper.getSkuAttrNameAndValue(skuId);
        return list;
    }
}




