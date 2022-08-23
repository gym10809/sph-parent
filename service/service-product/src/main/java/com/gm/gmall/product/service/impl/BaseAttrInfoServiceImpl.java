package com.gm.gmall.product.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.product.BaseAttrInfo;
import com.gm.gmall.model.product.BaseAttrValue;
import com.gm.gmall.product.mapper.BaseAttrInfoMapper;
import com.gm.gmall.product.mapper.BaseAttrValueMapper;
import com.gm.gmall.product.service.BaseAttrInfoService;
import com.gm.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
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

    /**
     * 查询对应的级别属性，有值的集合
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getInfoList(Integer category1Id, Integer category2Id, Integer category3Id) {
        List<BaseAttrInfo> list= baseAttrInfoMapper.getInfoList(category1Id,category2Id,category3Id);
        return list;
    }

    /**
     * 更新或者新增属性值
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断是否是新增
        if (baseAttrInfo.getId() == null){
            saveInfo(baseAttrInfo);//新增
        }else {
            updateInfo(baseAttrInfo);
        }

    }

    /**
     * 通过attrId获取对应的values
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValue(Integer attrId) {
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseAttrValue::getAttrId,attrId);
        List<BaseAttrValue> attrValues = baseAttrValueMapper.selectList(lambdaQueryWrapper);
        return attrValues;
    }

    private void updateInfo(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.updateById(baseAttrInfo);//更新
        //更新前先删除掉数据库里面前端删掉的数据
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        List<Long> ids=new ArrayList<>();
        //把存在被修改的属性值的id提取出来
        for ( BaseAttrValue attrValue:attrValueList){
            if (attrValue.getId() !=null){
                ids.add(attrValue.getId());
            }
        }
        //如果存在修改的id，把数据库中存在，但已被前端删除掉的属性值删掉
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseAttrValue::getAttrId, baseAttrInfo.getId());
        if (ids.size()>0){
            lambdaQueryWrapper.notIn(BaseAttrValue::getId,ids);
            baseAttrValueMapper.delete(lambdaQueryWrapper);
        }else {
            //如果不存在，则说明前端把数据库的属性值全删除掉了，需要删除数据库对应的值
            baseAttrValueMapper.delete(lambdaQueryWrapper);
        }
        //进行更新或者新增操作：
        for (BaseAttrValue attrValue:attrValueList){
            if (attrValue.getAttrId()!=null){
                baseAttrValueMapper.updateById(attrValue);
            }else {
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(attrValue);
            }
        }
    }

    private void saveInfo(BaseAttrInfo baseAttrInfo) {
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        //新增
        baseAttrInfoMapper.insert(baseAttrInfo);
        Long attrInfoId= baseAttrInfo.getId();
        for (BaseAttrValue attrValue: valueList) {
            attrValue.setAttrId(attrInfoId);
            baseAttrValueMapper.insert(attrValue);
        }
    }
}




