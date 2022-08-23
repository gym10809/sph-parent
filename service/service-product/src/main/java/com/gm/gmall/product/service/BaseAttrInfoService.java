package com.gm.gmall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.product.BaseAttrInfo;
import com.gm.gmall.model.product.BaseAttrValue;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2022-08-23 09:00:32
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    List<BaseAttrInfo> getInfoList(Integer category1Id, Integer category2Id, Integer category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValue(Integer attrId);
}
