package com.gm.gmall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/8/25 0025 23:24
 */
@RestController
@RequestMapping("admin/product")
public class SkuController {
    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 保存商品具体信息
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 分页获取列表信息
     * @param pageNum
     * @param size
     * @return
     */
    @GetMapping("/list/{pageNum}/{size}")
    public Result getPage(@PathVariable("pageNum")Long pageNum,@PathVariable("size")Long size){

        Page<SkuInfo> page=new Page<>(pageNum,size);
        skuInfoService.page(page);
        return Result.ok(page);
    }

    /**
     * 上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId")Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }
    /**
     * 下架
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId")Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }
}
