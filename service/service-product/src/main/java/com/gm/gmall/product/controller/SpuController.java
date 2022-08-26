package com.gm.gmall.product.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SpuImage;
import com.gm.gmall.model.product.SpuInfo;
import com.gm.gmall.model.product.SpuSaleAttr;
import com.gm.gmall.model.product.SpuSaleAttrValue;
import com.gm.gmall.product.service.SpuImageService;
import com.gm.gmall.product.service.SpuInfoService;
import com.gm.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/25 0025 14:17
 */
@RestController
@RequestMapping("/admin/product")
public class SpuController {
    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService saleAttrService;
    @GetMapping("/{pageNumber}/{size}")
    public Result getPage(@PathVariable("pageNumber") Long pageNumber,
                          @PathVariable("size")Long size,
                          @RequestParam("category3Id")Integer category3Id){
        Page<SpuInfo> page=new Page<>(pageNumber,size);
        LambdaQueryWrapper<SpuInfo> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuInfo::getCategory3Id,category3Id);
        Page<SpuInfo> infoPage = spuInfoService.page(page, queryWrapper);
        return Result.ok(infoPage);
    }

    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);

        return Result.ok();
    }
    @GetMapping("/spuImageList/{spuId}")
    public Result getSaleImageList(@PathVariable("spuId")Integer spuId){
        LambdaQueryWrapper<SpuImage> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuImage::getSpuId,spuId);
        List<SpuImage> spuImages = spuImageService.list(queryWrapper);
        return Result.ok(spuImages);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSaleAttrList(@PathVariable("spuId")Integer spuId){
        List<SpuSaleAttr> saleAttrs = saleAttrService.getSaleAndValueBySpuId(spuId);
        return Result.ok(saleAttrs);
    }
}
