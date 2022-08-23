package com.gm.gmall.product.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.BaseAttrInfo;
import com.gm.gmall.model.product.BaseAttrValue;
import com.gm.gmall.product.mapper.BaseAttrInfoMapper;
import com.gm.gmall.product.mapper.BaseAttrValueMapper;
import com.gm.gmall.product.service.BaseAttrInfoService;
import com.gm.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-23 09:00:32
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getInfoList(Integer category1Id, Integer category2Id, Integer category3Id) {

        List<BaseAttrInfo> list= baseAttrInfoMapper.getInfoList(category1Id,category2Id,category3Id);

        return list;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insert(baseAttrInfo);

        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        valueList.forEach(info->{
            if (info.getId() !=null){

            }
        });
    }
}




