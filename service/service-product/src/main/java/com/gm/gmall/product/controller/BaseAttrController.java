package com.gm.gmall.product.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.BaseAttrInfo;
import com.gm.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/23 0023 10:10
 */
@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {
    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据三个分类分类查询对应的平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id") Integer category1Id
                                     , @PathVariable("category2Id")Integer category2Id,
                                     @PathVariable("category3Id")Integer category3Id){
        List<BaseAttrInfo> list= baseAttrInfoService.getInfoList(category1Id,category2Id,category3Id);
        return Result.ok(list);
    }

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

}
