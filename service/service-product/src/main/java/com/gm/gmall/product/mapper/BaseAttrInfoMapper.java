package com.gm.gmall.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.product.BaseAttrInfo;


import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Mapper
* @createDate 2022-08-23 09:00:32
* @Entity com.gm.gmall.product.domain.BaseAttrInfo
*/
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<BaseAttrInfo> getInfoList(Integer category1Id, Integer category2Id, Integer category3Id);
}




