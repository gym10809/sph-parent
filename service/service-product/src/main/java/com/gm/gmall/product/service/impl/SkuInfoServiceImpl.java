package com.gm.gmall.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.product.*;
import com.gm.gmall.model.to.CategoryViewTo;
import com.gm.gmall.product.mapper.BaseCategory3Mapper;
import com.gm.gmall.product.mapper.SkuInfoMapper;
import com.gm.gmall.product.service.*;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Autowired
    BaseCategory3Mapper category3Mapper;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    RedissonClient redissonClient;

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
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstant.BLOOM_PRE);
        bloomFilter.add(skuId);
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

//    @Override
//    public SkuDetailTo getDetail(Integer skuId) {
//        SkuDetailTo skuDetailTo=new SkuDetailTo();
//        //查询skuInfo
//        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
//        //商品（sku）的图片存放进skuinfo中
//        LambdaQueryWrapper<SkuImage> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(SkuImage::getSkuId,skuId);
//        List<SkuImage> skuImages = imageService.list(queryWrapper);
//        skuInfo.setSkuImageList(skuImages);
//
//        skuDetailTo.setSkuInfo(skuInfo);
//        //商品（sku）所属的完整分类信息
//       CategoryViewTo categoryViewTo= category3Mapper.getCategoryView(skuInfo.getCategory3Id());
//       skuDetailTo.setCategoryView(categoryViewTo);
//        //价格查询
//        BigDecimal price = skuInfo.getPrice();
//        skuDetailTo.setPrice(price);
//        //查询当前sku对应的spu所有销售属性名和值（排序）并且标记当前sku属于哪一种组合
//        Long spuId = skuInfo.getSpuId();
//        List<SpuSaleAttr> spuSaleAttrList= spuSaleAttrService.getAttrAndSale(spuId,skuId);
//        skuDetailTo.setSpuSaleAttrList(spuSaleAttrList);
//        //封装sku对应的spu的所有销售属性和值成一个json对象
//      List<SkuSaleValueTo> list= skuInfoMapper.getSkuSaleAndValue(spuId);
//        String s = Jsons.toJson(list);
//        skuDetailTo.setValuesSkuJson(s);
//        return skuDetailTo;
//    }

    @Override
    public SkuInfo getSkuInfo(Integer skuId) {
        //查询skuInfo
        return skuInfoMapper.selectById(skuId);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId, Integer skuId) {
        //查询当前sku对应的spu所有销售属性名和值（排序）并且标记当前sku属于哪一种组合
        return  spuSaleAttrService.getAttrAndSale(spuId,skuId);
    }

    @Override
    public List<SkuImage> getSkuImageList(Integer skuId) {
        LambdaQueryWrapper<SkuImage> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImage::getSkuId,skuId);
        return  imageService.list(queryWrapper);
    }

    @Override
    public CategoryViewTo getCategoryView(Long category3Id) {
        return category3Mapper.getCategoryView(category3Id);
    }

    @Override
    public String getValuesSkuJson(Long spuId) {
        return Jsons.toJson(skuInfoMapper.getSkuSaleAndValue(spuId));

    }

    @Override
    public List<Integer> getAllIds() {
        //数据很多的时候分页，
        List<Integer> ids = skuInfoMapper.getIds();
        return ids;

    }

}




